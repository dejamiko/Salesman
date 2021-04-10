package graphs;

import salesman.Distances;

import java.util.*;

/**
 * A class responsible for graphs specific to the travelling salesman problem.
 *
 * @author mikolajdeja
 * @version 2021.04.10
 */
public class SalesmanGraph extends Graph {

    /**
     * A constructor for the graph with given input vertices.
     *
     * @param input The input vertices.
     * @param distances The distances to be used.
     */
    public SalesmanGraph(List<Graphable> input, Distances distances) {
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
     * A constructor for the graph with given vertices and edges.
     *
     * @param input The input vertices.
     * @param distances The distances to be used.
     * @param edges The input edges.
     */
    public SalesmanGraph(List<Graphable> input, Distances distances, List<Edge> edges) {
        super(input, distances, edges);
    }

    /**
     * Get a route in the graph. (Assuming there is a hamilton cycle)
     *
     * @return The route in the graph.
     */
    public List<Graphable> getRoute() {
        Set<Vertex> visited = new HashSet<>();
        List<Graphable> route = new ArrayList<>();
        Vertex curr = getVertexList().get(0);
        for (Vertex vertex : getVertexList())
            if (vertex.getNumberOfAdjacent() == 1) {
                curr = vertex;
                break;
            }

        while (visited.size() < getVertexList().size()) {
            route.add(curr.getContents());
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
        return route;
    }

    /**
     * Create a minimal spanning tree in the graph.
     * I use Prim's algorithm to achieve that:
     * <p>
     * https://en.wikipedia.org/wiki/Prim%27s_algorithm
     */
    public void createMinimalSpanningTree() {
        removeAllEdges();

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
            noEdges ++;
        }
    }

    /**
     * Get the vertices with an odd number of edges adjacent to them.
     *
     * @return The list of vertices with odd number of edges.
     */
    public List<Vertex> getOddNumbered() {
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
    public void createPerfectMatching() {
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
    public void doubleAllEdges() {
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
    public List<Vertex> createEulerCircuit() {
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
        removeAllEdges();
        for (int i = 0; i < circuit.size() - 1; i++) {
            addEdge(circuit.get(i), circuit.get(i + 1));
        }
        addEdge(circuit.get(0), circuit.get(circuit.size() - 1));

        return circuit;
    }

    /**
     * Create a Hamiltonian cycle from the Euler circuit by skipping the
     * repeated vertices.
     */
    public void createHamiltonianCycle() {
        Set<Vertex> visited = new HashSet<>();
        List<Vertex> hamiltonianCycle = new ArrayList<>();
        List<Vertex> euler = createEulerCircuit();
        for (Vertex vertex : euler) {
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                hamiltonianCycle.add(vertex);
            }
        }
        removeAllEdges();
        for (int i = 0; i < hamiltonianCycle.size() - 1; i++) {
            addEdge(hamiltonianCycle.get(i), hamiltonianCycle.get(i + 1));
        }
        addEdge(hamiltonianCycle.get(0), hamiltonianCycle.get(hamiltonianCycle.size() - 1));
    }


}
