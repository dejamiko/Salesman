package graphs;

import salesman.Distances;

import java.util.*;

/**
 * A class responsible for holding information about graphs.
 *
 * @author mikolajdeja
 * @version 2021.04.09
 */
public class Graph {
    private final List<Vertex> vertexList;
    private List<Vertex> isolatedVertices;
    private final Distances distances;
    private double dist;

    /**
     * A constructor for the graph with given input vertices.
     *
     * @param input The input vertices.
     * @param distances The distances to be used.
     */
    public Graph(List<Graphable> input, Distances distances) {
        this.distances = distances;
        vertexList = new ArrayList<>();
        dist = 0.0;
        for (Graphable graphable : input)
            vertexList.add(new Vertex(graphable));
        createIsolatedVertices();
    }

    /**
     * A constructor for the graph with given distances.
     *
     * @param distances The distances to be used.
     */
    public Graph(Distances distances) {
        this.distances = distances;
        vertexList = new ArrayList<>();
        dist = 0.0;
    }

    /**
     * A constructor for the graph with a given graph.
     *
     * @param graph A graph to be copied.
     */
    public Graph(Graph graph) {
        this.distances = graph.getDistances();
        this.vertexList = new ArrayList<>(graph.getVertexList());
        this.isolatedVertices = new ArrayList<>(graph.getIsolatedVertices());
        this.dist = graph.getDist();
    }

    /**
     * A constructor for the graph with given vertices and edges.
     *
     * @param input The input vertices.
     * @param distances The distances to be used.
     * @param edges The input edges.
     */
    public Graph(List<Graphable> input, Distances distances, List<Edge> edges) {
        this.distances = distances;
        vertexList = new ArrayList<>();
        dist = 0.0;
        for (Graphable graphable : input)
            vertexList.add(new Vertex(graphable));
        for (Edge edge : edges) {
            Vertex first = edge.getFirst();
            Vertex second = edge.getSecond();
            first.addEdge(second, distances.getDistance(first.getContents(), second.getContents()));
            second.addEdge(first, distances.getDistance(first.getContents(), second.getContents()));
            dist += distances.getDistance(first.getContents(), second.getContents());
        }
        createIsolatedVertices();
    }

    /**
     * @return The list of vertices.
     */
    public List<Vertex> getVertexList() {
        return vertexList;
    }

    /**
     * @return The distances used.
     */
    public Distances getDistances() {
        return distances;
    }

    /**
     * Add vertices to the graph.
     *
     * @param vertices The vertices to be added.
     */
    public void addVertex(Vertex... vertices) {
        Collections.addAll(vertexList, vertices);
    }

    /**
     * Add an edge to the graph.
     *
     * @param graphable1 The first graphable to be added.
     * @param graphable2 The second graphable to be added.
     */
    public void addEdge(Graphable graphable1, Graphable graphable2) {
        Vertex first = new Vertex(graphable1);
        Vertex second = new Vertex(graphable2);
        first.addEdge(second, distances.getDistance(graphable1, graphable2));
        second.addEdge(first, distances.getDistance(graphable2, graphable1));
        dist += distances.getDistance(graphable1, graphable2);
    }

    /**
     * Add an edge to the graph.
     *
     * @param vertex1 The first vertex to be added.
     * @param vertex2 The second vertex to be added.
     */
    public void addEdge(Vertex vertex1, Vertex vertex2) {
        if (vertex1.isIsolated())
            isolatedVertices.remove(vertex1);
        if (vertex2.isIsolated())
            isolatedVertices.remove(vertex2);
        vertex1.addEdge(vertex2, distances.getDistance(vertex1.getContents(), vertex2.getContents()));
        vertex2.addEdge(vertex1, distances.getDistance(vertex2.getContents(), vertex1.getContents()));
        dist += distances.getDistance(vertex1.getContents(), vertex2.getContents());
    }

    /**
     * Create a list of isolated vertices.
     */
    private void createIsolatedVertices() {
        isolatedVertices = new ArrayList<>();
        for (Vertex vertex : vertexList) {
            if (vertex.isIsolated())
                isolatedVertices.add(vertex);
        }
    }

    /**
     * @return The list of isolated vertices.
     */
    public List<Vertex> getIsolatedVertices() {
        return isolatedVertices;
    }

    /**
     * @return An isolated vertex.
     */
    public Vertex getIsolatedVertex() {
        return getIsolatedVertex(0);
    }

    /**
     * Get an isolated vertex with a given index.
     *
     * @param index The index used.
     * @return The isolated vertex.
     */
    public Vertex getIsolatedVertex(int index) {
        if (isolatedVertices.size() > index)
            return isolatedVertices.remove(index);
        else
            return null;
    }

    /**
     * Get the closest isolated vertex to a target vertex.
     *
     * @param target The target vertex.
     * @return The closest isolated vertex.
     */
    public Vertex getClosestIsolated(Vertex target) {
        double min = Double.MAX_VALUE;
        Vertex candidate = null;
        for (Vertex vertex : isolatedVertices) {
            double curr = distances.getDistance(vertex.getContents(), target.getContents());
            if (!vertex.equals(target) && curr < min) {
                min = curr;
                candidate = vertex;
            }
        }
        return candidate;
    }

    /**
     * Remove all edges in the graph.
     */
    public void removeAllEdges() {
        for (Vertex vertex : vertexList)
            vertex.removeAllEdges();
        dist = 0.0;
        createIsolatedVertices();
    }

    /**
     * @return The current length of all edges in the graph.
     */
    public double getDist() {
        return dist;
    }
}
