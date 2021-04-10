package salesman;

import graphs.Graph;
import graphs.Graphable;
import graphs.SalesmanGraph;
import graphs.Vertex;

import java.util.*;

/**
 * A class responsible for the TravellingSalesman problem calculations.
 *
 * @author Mikolaj Deja 20010020
 * @version 2021.03.27
 */
public class TravellingSalesman {
    private final Distances distances;
    private List<Graphable> best;
    private double min;

    /**
     * Constructor for a travelling salesman with a particular distance
     * method.
     *
     * @param method The method for calculating the distance.
     */
    public TravellingSalesman(DistanceCalculationMethod method) {
        distances = new Distances(method);
        best = new ArrayList<>();
        min = Double.MAX_VALUE;
    }


    /**
     * Solve the Travelling Salesman Problem:
     * <p>
     * https://en.wikipedia.org/wiki/Travelling_salesman_problem
     * <p>
     * Given a list of places and the distances between them, what is
     * the shortest possible route that visits each city exactly once and
     * returns to the origin place?
     * <p>
     * It's an NP-hard problem, well known and useful in many branches
     * of computer science. I implemented a few different algorithms to solve it.
     *
     * @param graphables The list of places.
     * @param method     The method of calculating the route.
     * @return The list of places in an order in which they should be visited.
     */
    public List<Graphable> solveTravellingSalesmanProblem(List<Graphable> graphables, TSPCalculationMethod method) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("Travelling salesman solve problem had an empty or null input.");

        Random rand = new Random();

        switch (method) {
            case NEAREST_NEIGHBOUR -> graphables = nearestNeighbourAlgorithm(graphables, rand.nextInt(graphables.size()));
            case IMPROVED_NEAREST_NEIGHBOUR -> graphables = improvedNearestNeighbouring(graphables);
            case CHRISTOFIDES_MATCHING -> graphables = christofidesNN(graphables);
            case BRUTE_FORCE -> graphables = bruteForceApproach(graphables);
            case CHRISTOFIDES_DOUBLE_EDGES -> graphables = christofidesDoubleEdges(graphables);
        }
        return graphables;
    }

    /**
     * A brute force approach to find a collection of places in
     * order with which they should be visited, for the total distance
     * to be minimal.
     *
     * @param graphables The list of places.
     * @return The list of places in a correct order.
     */
    public List<Graphable> bruteForceApproach(List<Graphable> graphables) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("Brute force had an empty or null input.");

        // It's a very inefficient method, we don't want to wait
        // too long for the calculation to finish.
        if (graphables.size() > 10)
            return graphables;

        best = new ArrayList<>(graphables);
        min = Double.MAX_VALUE;

        bruteForceApproach(graphables.size(), graphables);

        return best;
    }

    /**
     * The exhaustive enumeration approach to find a collection of places
     * as a solution to the travelling salesman problem. This
     * approach checks every permutation of the list and to do
     * that I use the Heap's algorithm:
     * <p>
     * https://en.wikipedia.org/wiki/Heap%27s_algorithm
     *
     * @param size The size of the part of the collection to be permuted.
     */
    private void bruteForceApproach(int size, List<Graphable> graphables) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("Brute force internal had an empty or null input.");

        if (size == 1) {
            double curr = distances.getPathDistance(graphables);
            if (curr < min) {
                min = curr;
                best = new ArrayList<>(graphables);
            }
        }

        for (int i = 0; i < size; ++i) {
            bruteForceApproach(size - 1, graphables);

            if (size % 2 == 0) {
                Collections.swap(graphables, i, size - 1);
            } else {
                Collections.swap(graphables, 0, size - 1);
            }
        }
    }

    /**
     * A heuristic approach to solving the TSP.
     * <p>
     * This approach takes the nearest neighbour of the current place
     * as the next one. It's a greedy algorithm, described here:
     * <p>
     * https://en.wikipedia.org/wiki/Nearest_neighbour_algorithm
     *
     * @param graphables The list of places to get the path from.
     * @param starting   The index of the starting place.
     * @return The list of the places as nearest neighbours.
     */
    public List<Graphable> nearestNeighbourAlgorithm(List<Graphable> graphables, int starting) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("nearest neighbour had an empty or null input.");

        List<Graphable> ans = new ArrayList<>();
        ans.add(graphables.remove(starting));

        while (graphables.size() > 0) {
            int minInd = 0;
            double min = Double.MAX_VALUE;
            for (int i = 0; i < graphables.size(); ++i) {
                double dist = distances.getDistance(graphables.get(i), ans.get(ans.size() - 1));
                if (dist < min) {
                    min = dist;
                    minInd = i;
                }
            }
            ans.add(graphables.remove(minInd));
        }

        return ans;
    }

    public Graph nearestNeighbourAlgorithmGraph(List<Graphable> graphables, int starting) {
        Graph graph = new Graph(graphables, distances);
        Vertex current = graph.getIsolatedVertex(starting);
        Vertex first = current;
        while (graph.getIsolatedVertices().size() > 0) {
            Vertex next = graph.getClosestIsolated(current);
            graph.addEdge(current, next);
            current = next;
        }
        graph.addEdge(current, first);
        return graph;
    }

    public Graph improvedNearestNeighbouringGraph(List<Graphable> graphables) {
        Graph best = new Graph(graphables, distances);
        Graph graph;

        double currMin = Double.MAX_VALUE;
        for (int i = 0; i < graphables.size(); ++i) {
            graph = nearestNeighbourAlgorithmGraph(graphables, i);
            if (graph.getDist() < currMin) {
                currMin = graph.getDist();
                best = new Graph(graph);
            }
        }

        return best;
    }

    public Graph christofides(List<Graphable> graphables) {
        SalesmanGraph graph = new SalesmanGraph(graphables, distances);

        graph.createMinimalSpanningTree();
        graph.createPerfectMatching();
        graph.createEulerCircuit();
        graph.createHamiltonianCycle();

        return graph;
    }

    public Graph christofidesDouble(List<Graphable> graphables) {
        SalesmanGraph graph = new SalesmanGraph(graphables, distances);

        graph.createMinimalSpanningTree();
        graph.doubleAllEdges();
        graph.createEulerCircuit();
        graph.createHamiltonianCycle();

        return graph;
    }

    /**
     * A heuristic method of solving the TSP. It builds on the Nearest
     * Neighbour algorithm.
     * <p>
     * Run the Nearest Neighbour algorithm for all possible
     * starting places. Keep the best one in list best.
     *
     * @param graphables The list of places to visit.
     * @return The list of places in an order in which they should be visited.
     */
    public List<Graphable> improvedNearestNeighbouring(List<Graphable> graphables) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("improvedNN had an empty or null input.");

        double currMin = Double.MAX_VALUE;
        List<Graphable> best = new ArrayList<>();

        for (int i = 0; i < graphables.size(); ++i) {
            List<Graphable> copy = new ArrayList<>(graphables);

            List<Graphable> candidate = nearestNeighbourAlgorithm(copy, i);

            if (distances.getPathDistance(candidate) < currMin) {
                currMin = distances.getPathDistance(candidate);
                best = new ArrayList<>(candidate);
            }
        }
        return best;
    }

    /**
     * An optimization algorithm, useful for further improving
     * the results of some heuristic algorithms.
     * <p>
     * A 2-opt algorithm, as described here:
     * <p>
     * https://en.wikipedia.org/wiki/2-opt
     *
     * @param graphables The list of places to visit.
     * @return The list of places in an order in which they should be visited.
     */
    public List<Graphable> opt2(List<Graphable> graphables) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("2opt had an empty or null input.");

        List<Graphable> ans = new ArrayList<>(graphables);
        boolean changed = true;
        double bestDistance = distances.getPathDistance(graphables);
        while (changed) {
            changed = false;
            for (int i = 1; i < graphables.size() - 1; ++i) {
                for (int j = i + 1; j < graphables.size() - 1; ++j) {

                    graphables = opt2Swap(graphables, i, j);

                    double newDistance = distances.getPathDistance(graphables);
                    if (newDistance < bestDistance) {
                        bestDistance = newDistance;
                        ans = new ArrayList<>(graphables);
                        changed = true;
                    }
                }
            }
        }
        return ans;
    }

    /**
     * A swap utilised by the 2-opt algorithm.
     *
     * @param route The route on which the algorithm works.
     * @param i     The beginning index.
     * @param j     The end index.
     * @return The route after the swap.
     */
    private List<Graphable> opt2Swap(List<Graphable> route, int i, int j) {
        if (route == null || route.size() == 0)
            throw new IllegalArgumentException("2opt swap had an empty or null input.");
        if (i < 0 || i >= route.size() || j < 0 || j >= route.size() || i >= j)
            throw new IllegalArgumentException("2opt swap had a bad index input.");

        List<Graphable> ans = new ArrayList<>(route.subList(0, i));
        List<Graphable> temp = new ArrayList<>(route.subList(i, j + 1));
        Collections.reverse(temp);
        ans.addAll(temp);
        ans.addAll(route.subList(j + 1, route.size()));

        return ans;
    }

    /**
     * A heuristic approach to solving the TSP.
     * <p>
     * This approach combines the minimum spanning tree with minimum-weight
     * perfect matching (or in my case its heuristic approximation).
     * The algorithm is described here:
     * <p>
     * https://en.wikipedia.org/wiki/Christofides_algorithm
     *
     * @param graphables The list of places for which to solve the TSP.
     * @return The route as calculated by this algorithm.
     */
    public List<Graphable> christofidesNN(List<Graphable> graphables) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("christofides had an empty or null input.");

        List<Graphable> listingsList = new ArrayList<>(graphables);
        List<Graphable> tree = getMinimalTree(listingsList);
        List<Graphable> odd = getOddVertices(tree);
        List<Graphable> match = perfectMatchingGreedy(tree, odd);
        List<Graphable> circuit = getEulerCircuit(match);

        return getHamiltonianCycle(circuit);
    }

    /**
     * A heuristic approach to solving the TSP.
     * <p>
     * This approach combines the minimum spanning tree with doubling the trees edges.
     * The algorithm is described here:
     * <p>
     * https://en.wikipedia.org/wiki/Travelling_salesman_problem#Heuristic_and_approximation_algorithms
     *
     * @param graphables The list of places for which to solve the TSP.
     * @return The route as calculated by this algorithm.
     */
    public List<Graphable> christofidesDoubleEdges(List<Graphable> graphables) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("christofidesDoubleEdges had an empty or null input.");

        List<Graphable> listingList = new ArrayList<>(graphables);
        List<Graphable> tree = getMinimalTree(listingList);
        List<Graphable> match = doubleAllEdges(tree);
        List<Graphable> circuit = getEulerCircuit(match);

        return getHamiltonianCycle(circuit);
    }

    /**
     * Get minimum spanning tree for the given places.
     * I use Prim's algorithm to achieve that:
     * <p>
     * https://en.wikipedia.org/wiki/Prim%27s_algorithm
     *
     * @param graphables The graph in which the tree is to be found.
     * @return The minimum spanning tree.
     */
    public List<Graphable> getMinimalTree(List<Graphable> graphables) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("minimal tree had an empty or null input.");

        int noEdges = 0;
        int noVertex = graphables.size();
        double[][] distanceMatrix = distances.getDistanceMatrix(graphables);
        boolean[] selected = new boolean[noVertex];
        selected[0] = true;
        List<Graphable> ans = new ArrayList<>();

        while (noEdges < noVertex - 1) {
            double min = Double.MAX_VALUE;
            int x = 0, y = 0;

            for (int i = 0; i < noVertex; ++i)
                if (selected[i])
                    for (int j = 0; j < noVertex; ++j)
                        if (!selected[j] && distanceMatrix[i][j] != 0)
                            if (min > distanceMatrix[i][j]) {
                                min = distanceMatrix[i][j];
                                x = i;
                                y = j;
                            }

            ans.add(graphables.get(x));
            ans.add(graphables.get(y));
            selected[y] = true;
            noEdges++;
        }
        return ans;
    }

    /**
     * Get the vertices with an odd number of edges adjacent to them.
     *
     * @param tree The graph in which to look for vertices.
     * @return The list of vertices with odd number of edges.
     */
    private List<Graphable> getOddVertices(List<Graphable> tree) {
        if (tree == null || tree.size() == 0)
            throw new IllegalArgumentException("odd vertices had an empty or null input.");

        List<Graphable> oddDegreeVertices = new ArrayList<>();
        Map<Graphable, Integer> map = new HashMap<>();
        for (Graphable graphable : tree)
            map.put(graphable, map.getOrDefault(graphable, 0) + 1);
        map.forEach((key, value) -> {
            if (value % 2 == 1) {
                oddDegreeVertices.add(key);
            }
        });

        return oddDegreeVertices;
    }

    /**
     * Make perfect matching between the vertices with odd number of edges
     * and add it to the whole graph. In my case it's not a minimal-weight
     * perfect matching, I approximate it with the nearest neighbour algorithm.
     *
     * @param tree The graph to which the matching is to be added.
     * @param odd  The list of vertices with odd an count of edges.
     * @return The resulting graph.
     */
    private List<Graphable> perfectMatchingGreedy(List<Graphable> tree, List<Graphable> odd) {
        if (tree == null || tree.size() == 0 || odd == null)
            throw new IllegalArgumentException("perfect matching had an empty or null input.");

        List<Graphable> matched = new ArrayList<>(tree);
        List<Graphable> temp = new ArrayList<>(odd);
        while (temp.size() > 0) {
            double min = Double.MAX_VALUE;
            int minInd = 0;
            for (int i = 1; i < temp.size(); ++i) {
                double currDistance = distances.getDistance(temp.get(0), temp.get(i));
                if (currDistance < min) {
                    min = currDistance;
                    minInd = i;
                }
            }
            matched.add(temp.remove(minInd));
            matched.add(temp.remove(0));
        }

        return matched;
    }

    /**
     * Double all the edges in the graph to ensure that every vertex
     * is of an even degree. Since I'm using directed edges, reverse
     * the new ones.
     *
     * @param graphables The graph for which to double all edges.
     * @return The graph with all edges doubled.
     */
    private List<Graphable> doubleAllEdges(List<Graphable> graphables) {
        if (graphables == null || graphables.size() == 0)
            throw new IllegalArgumentException("doubleAllEdges had an empty or null input.");

        graphables.addAll(graphables);

        for (int i = graphables.size() / 2; i < graphables.size(); i += 2)
            Collections.swap(graphables, i, i + 1);

        return graphables;
    }

    /**
     * Get the Euler circuit of the graph. To do that I use the Hierholzer's
     * algorithm, as described here:
     * <p>
     * https://en.wikipedia.org/wiki/Eulerian_path#Hierholzer's_algorithm
     *
     * @param matched The graph for which to find the Euler circuit.
     * @return The Euler circuit as calculated by the algorithm.
     */
    private List<Graphable> getEulerCircuit(List<Graphable> matched) {
        if (matched == null || matched.size() == 0)
            throw new IllegalArgumentException("euler circuit had an empty or null input.");

        Stack<Graphable> currPath = new Stack<>();
        List<Graphable> circuit = new ArrayList<>();
        Map<Graphable, List<Graphable>> edges = new HashMap<>();

        for (int i = 0; i < matched.size(); i += 2) {
            List<Graphable> list = edges.getOrDefault(matched.get(i), new ArrayList<>());
            list.add(matched.get(i + 1));
            edges.put(matched.get(i), list);
        }
        for (int i = 0; i < matched.size(); i += 2) {
            List<Graphable> list = edges.getOrDefault(matched.get(i + 1), new ArrayList<>());
            list.add(matched.get(i));
            edges.put(matched.get(i + 1), list);
        }

        for (Map.Entry<Graphable, List<Graphable>> entry : edges.entrySet()) {
            entry.setValue(new ArrayList<>(new HashSet<>(entry.getValue())));
        }

        currPath.push(matched.get(0));
        Graphable current = matched.get(0);

        while (!currPath.empty()) {
            List<Graphable> incident = edges.get(current);

            if (!incident.isEmpty()) {
                currPath.push(current);
                Graphable next = incident.remove(0);
                List<Graphable> temp = edges.get(next);
                temp.remove(current);
                current = next;
            } else {
                circuit.add(current);
                current = currPath.pop();
            }
        }
        return circuit;
    }

    /**
     * Get Hamiltonian cycle from the Euler circuit by skipping the
     * repeated vertices.
     *
     * @param eulerCircuit The Euler circuit for which to find a Hamiltonian cycle.
     * @return The resulting Hamiltonian cycle.
     */
    private List<Graphable> getHamiltonianCycle(List<Graphable> eulerCircuit) {
        if (eulerCircuit == null || eulerCircuit.size() == 0)
            throw new IllegalArgumentException("hamiltonian cycle had an empty or null input.");

        Set<Graphable> visited = new HashSet<>();
        List<Graphable> hamiltonianCycle = new ArrayList<>();
        for (Graphable graphable : eulerCircuit)
            if (!visited.contains(graphable)) {
                visited.add(graphable);
                hamiltonianCycle.add(graphable);
            }
        return hamiltonianCycle;
    }

    /**
     * Get the distances object, which is responsible for calculating
     * distances between the places.
     *
     * @return The distances object.
     */
    public Distances getDistances() {
        return distances;
    }
}