package graphs;

import salesman.Distances;
import salesman.TSPCalculationMethod;

import java.util.*;

/**
 * A class responsible for graphs specific to the travelling salesman problem.
 *
 * @author mikolajdeja
 * @version 2021.04.10
 */
public class SalesmanGraph extends Graph {
    private List<Vertex> best;
    private double min;

    /**
     * A constructor for the graph with given input locations.
     *
     * @param input     The input locations.
     * @param distances The distances to be used.
     */
    public SalesmanGraph(List<Location> input, Distances distances) {
        super(input, distances);
    }

    /**
     * A constructor for the graph with given distances.
     *
     * @param distances The distances to be used.
     */
    public SalesmanGraph(Distances distances) {
        super(distances);
    }

    /**
     * A constructor for the graph with a given graph.
     *
     * @param graph A graph to be copied.
     */
    public SalesmanGraph(Graph graph) {
        super(graph);
    }

    /**
     * A constructor for the graph with given locations and edges.
     *
     * @param input     The input locations.
     * @param distances The distances to be used.
     * @param edges     The input edges.
     */
    public SalesmanGraph(List<Location> input, Distances distances, List<Edge> edges) {
        super(input, distances, edges);
    }

    /**
     * Solve the Travelling Salesman Problem:
     * <p>
     * https://en.wikipedia.org/wiki/Travelling_salesman_problem
     * <p>
     * Given a list of places and the distances between them, what is
     * the shortest possible route that visits each place exactly once and
     * returns to the origin place?
     * <p>
     * It's an NP-hard problem, well known and useful in many branches
     * of computer science. I implemented a few different algorithms to solve it.
     *
     * @param method The method of calculating the route.
     */
    public void solveTravellingSalesmanProblem(TSPCalculationMethod method) {
        Random rand = new Random();

        switch (method) {
            case NEAREST_NEIGHBOUR -> nearestNeighbourAlgorithm(rand.nextInt(getVertexList().size()));
            case IMPROVED_NEAREST_NEIGHBOUR -> improvedNearestNeighbouring();
            case CHRISTOFIDES_DOUBLE_EDGES -> christofidesDouble();
            case CHRISTOFIDES_MATCHING -> christofidesNN();
            case BRUTE_FORCE -> bruteForceApproach();
        }
    }

    /**
     * A heuristic approach to solving the TSP.
     * <p>
     * This approach combines the minimum spanning tree with minimum-weight
     * perfect matching (or in my case its heuristic approximation).
     * The algorithm is described here:
     * <p>
     * https://en.wikipedia.org/wiki/Christofides_algorithm
     */
    private void christofidesNN() {
        removeAllEdges();
        createMinimalSpanningTree();
        createPerfectMatching();
        createEulerCircuit();
        createHamiltonianCycle();
    }

    /**
     * A heuristic approach to solving the TSP.
     * <p>
     * This approach combines the minimum spanning tree with doubling the trees edges.
     * The algorithm is described here:
     * <p>
     * https://en.wikipedia.org/wiki/Travelling_salesman_problem#Heuristic_and_approximation_algorithms
     */
    private void christofidesDouble() {
        removeAllEdges();
        createMinimalSpanningTree();
        doubleAllEdges();
        createEulerCircuit();
        createHamiltonianCycle();
    }

    /**
     * Get a route in the graph. (Assuming there is a hamilton cycle)
     *
     * @return The route in the graph.
     */
    public List<Vertex> getRoute() {
        Set<Vertex> visited = new HashSet<>();
        List<Vertex> route = new ArrayList<>();
        Vertex curr = getVertexList().get(0);
        for (Vertex vertex : getVertexList())
            if (vertex.isPendant()) {
                curr = vertex;
                break;
            }

        while (visited.size() < getVertexList().size()) {
            route.add(curr);
            visited.add(curr);
            List<Vertex> adjacent = curr.getAdjacentVertices();
            if (visited.containsAll(adjacent)) {
                break;
            }
            for (Vertex vertex : adjacent)
                if (!visited.contains(vertex)) {
                    curr = vertex;
                    break;
                }
        }

        if (visited.size() != route.size())
            return getVertexList();

        return route;
    }

    /**
     * Create a minimal spanning tree in the graph.
     * I use Prim's algorithm to achieve that:
     * <p>
     * https://en.wikipedia.org/wiki/Prim%27s_algorithm
     */
    private void createMinimalSpanningTree() {
        int noEdges = 0;
        int noVertices = getVertexList().size();
        double[][] distanceMatrix = getDistances().getDistanceMatrix(new ArrayList<>(getVertexList()));
        boolean[] selected = new boolean[noVertices];
        selected[0] = true;

        while (noEdges < noVertices - 1) {
            double min = Double.MAX_VALUE;
            int x = 0, y = 0;
            for (int i = 0; i < noVertices; i++)
                if (selected[i])
                    for (int j = 0; j < noVertices; j++)
                        if (!selected[j] && distanceMatrix[i][j] != 0)
                            if (min > distanceMatrix[i][j]) {
                                min = distanceMatrix[i][j];
                                x = i;
                                y = j;
                            }
            addEdge(getVertexList().get(x), getVertexList().get(y));
            selected[y] = true;
            noEdges++;
        }
    }

    /**
     * Get the vertices with an odd number of edges adjacent to them.
     *
     * @return The list of vertices with odd number of edges.
     */
    private List<Vertex> getOddNumbered() {
        List<Vertex> oddNumbered = new ArrayList<>();
        for (Vertex vertex : getVertexList())
            if (vertex.isOddNumbered())
                oddNumbered.add(vertex);

        return oddNumbered;
    }

    /**
     * Make perfect matching between the vertices with odd number of edges
     * and add it to the whole graph. In my case it's not a minimal-weight
     * perfect matching, I approximate it with the nearest neighbour algorithm.
     */
    private void createPerfectMatching() {
        List<Vertex> oddNumbered = getOddNumbered();
        while (oddNumbered.size() > 0) {
            double min = Double.MAX_VALUE;
            int ind1 = 0, ind2 = 0;
            for (int i = 0; i < oddNumbered.size(); i++) {
                for (int j = i + 1; j < oddNumbered.size(); j++) {
                    double currDist = getDistances().getDistance(oddNumbered.get(j).getContents(), oddNumbered.get(i).getContents());
                    if (currDist < min) {
                        ind1 = i;
                        ind2 = j;
                        min = currDist;
                    }
                }
            }
            Vertex first = oddNumbered.get(ind1);
            Vertex second = oddNumbered.get(ind2);
            oddNumbered.remove(first);
            oddNumbered.remove(second);
            addEdge(first, second);
        }
    }

    /**
     * Double all edges in the graph.
     */
    private void doubleAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Vertex vertex : getVertexList())
            edges.addAll(vertex.getIncidentEdges());
        for (Edge edge : edges)
            addEdge(edge.getSecond(), edge.getFirst());
    }

    /**
     * Get the Euler circuit of the graph. To do that I use the Hierholzer's
     * algorithm, as described here:
     * <p>
     * https://en.wikipedia.org/wiki/Eulerian_path#Hierholzer's_algorithm
     *
     * @return The Euler circuit as calculated by the algorithm.
     */
    private List<Vertex> createEulerCircuit() {
        Stack<Vertex> currPath = new Stack<>();
        List<Vertex> circuit = new ArrayList<>();

        Vertex current = getVertexList().get(0);
        currPath.push(current);

        while (!currPath.empty()) {
            if (!current.isIsolated()) {
                currPath.push(current);
                Vertex next = current.getAdjacentVertices().get(0);
                current.removeEdge(next);
                next.removeEdge(current);
                current = next;
            } else {
                circuit.add(current);
                current = currPath.pop();
            }
        }
        drawNewPath(circuit);

        return circuit;
    }

    /**
     * Create a Hamiltonian cycle from the Euler circuit by skipping the
     * repeated vertices.
     */
    private void createHamiltonianCycle() {
        Set<Vertex> visited = new HashSet<>();
        List<Vertex> hamiltonianCycle = new ArrayList<>();
        List<Vertex> euler = createEulerCircuit();
        for (Vertex vertex : euler) {
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                hamiltonianCycle.add(vertex);
            }
        }
        drawNewPath(hamiltonianCycle);
    }

    /**
     * A brute force approach to find a collection of places in
     * order with which they should be visited, for the total distance
     * to be minimal.
     */
    private void bruteForceApproach() {
        if (getVertexList().size() > 10)
            return;

        List<Vertex> vertices = new ArrayList<>(getVertexList());
        best = new ArrayList<>(vertices);
        min = Double.MAX_VALUE;

        bruteForceApproach(vertices, vertices.size());

        drawNewPath(best);
    }

    /**
     * The exhaustive enumeration approach to find a collection of places
     * as a solution to the travelling salesman problem. This
     * approach checks every permutation of the list and to do
     * that I use the Heap's algorithm:
     * <p>
     * https://en.wikipedia.org/wiki/Heap%27s_algorithm
     *
     * @param size     The size of the part of the collection to be permuted.
     * @param vertices The list for which the algorithm is calculated.
     */
    private void bruteForceApproach(List<Vertex> vertices, int size) {

        if (size == 1) {
            double curr = getDistances().getPathDistance(new ArrayList<>(vertices));
            if (curr < min) {
                min = curr;
                best = new ArrayList<>(vertices);
            }
        }

        for (int i = 0; i < size; ++i) {
            bruteForceApproach(vertices, size - 1);
            if (size % 2 == 0)
                Collections.swap(vertices, i, size - 1);
            else
                Collections.swap(vertices, 0, size - 1);
        }
    }

    /**
     * A heuristic method of solving the TSP. It builds on the Nearest
     * Neighbour algorithm.
     * <p>
     * Run the Nearest Neighbour algorithm for all possible
     * starting places. Keep the best one in graph best.
     */
    private void improvedNearestNeighbouring() {
        SalesmanGraph best = new SalesmanGraph(getDistances());

        double currMin = Double.MAX_VALUE;
        for (int i = 0; i < getVertexList().size(); ++i) {
            nearestNeighbourAlgorithm(i);
            if (getDist() < currMin) {
                currMin = getDist();
                best = new SalesmanGraph(this);
            }
        }
        copy(best);
    }

    /**
     * A heuristic approach to solving the TSP.
     * <p>
     * This approach takes the nearest neighbour of the current place
     * as the next one. It's a greedy algorithm, described here:
     * <p>
     * https://en.wikipedia.org/wiki/Nearest_neighbour_algorithm
     *
     * @param starting The index of the starting place.
     */
    private void nearestNeighbourAlgorithm(int starting) {
        removeAllEdges();
        Vertex current = getIsolatedVertex(starting);
        Vertex first = current;
        while (getIsolatedVertices().size() > 0) {
            Vertex next = getClosestIsolated(current);
            addEdge(current, next);
            current = next;
        }
        addEdge(current, first);
    }

    /**
     * An optimization algorithm, useful for further improving
     * the results of some heuristic algorithms.
     * <p>
     * A 2-opt algorithm, as described here:
     * <p>
     * https://en.wikipedia.org/wiki/2-opt
     */
    public void opt2() {
        List<Vertex> vertices = getRoute();
        List<Vertex> ans = getRoute();
        boolean changed = true;
        double bestDistance = getDistances().getPathDistance(new ArrayList<>(vertices));
        while (changed) {
            changed = false;
            for (int i = 1; i < vertices.size() - 1; ++i) {
                for (int j = i + 1; j < vertices.size() - 1; ++j) {

                    vertices = opt2Swap(vertices, i, j);

                    double newDistance = getDistances().getPathDistance(new ArrayList<>(vertices));
                    if (newDistance < bestDistance) {
                        bestDistance = newDistance;
                        ans = new ArrayList<>(vertices);
                        changed = true;
                    }
                }
            }
        }
        drawNewPath(ans);
    }

    /**
     * A swap utilised by the 2-opt algorithm.
     *
     * @param route The route on which the algorithm works.
     * @param i     The beginning index.
     * @param j     The end index.
     * @return The route after the swap.
     */
    private List<Vertex> opt2Swap(List<Vertex> route, int i, int j) {
        List<Vertex> ans = new ArrayList<>(route.subList(0, i));
        List<Vertex> temp = new ArrayList<>(route.subList(i, j + 1));
        Collections.reverse(temp);
        ans.addAll(temp);
        ans.addAll(route.subList(j + 1, route.size()));

        return ans;
    }

    /**
     * Create edges between vertices in the given list.
     *
     * @param list List of vertices.
     */
    protected void drawNewPath(List<Vertex> list) {
        removeAllEdges();
        for (int i = 0; i + 1 < list.size(); i++) {
            addEdge(list.get(i), list.get(i + 1));
        }
        addEdge(list.get(0), list.get(list.size() - 1));
    }
}
