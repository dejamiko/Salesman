package graphs;

import salesman.DistanceCalculationMethod;
import salesman.Distances;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final List<Vertex<Graphable>> vertexList;
    private final List<Edge> edgeList;
    private final Distances distances;

    public Graph(List<Graphable> input, Distances distances) {
        this.distances = distances;
        edgeList = new ArrayList<>();
        vertexList = new ArrayList<>();
        for (Graphable graphable: input)
            vertexList.add(new Vertex<>(graphable));
    }

    public List<Vertex<Graphable>> getVertexList() {
        return vertexList;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public Distances getDistances() {
        return distances;
    }

    public void addEdge(Graphable object1, Graphable object2) {
        Vertex<Graphable> first = new Vertex<>(object1);
        Vertex<Graphable> second = new Vertex<>(object2);
        vertexList.add(first);
        vertexList.add(second);
        first.addEdge(second, distances.getDistance(object1, object2));
        second.addEdge(first, distances.getDistance(object2, object1));
        edgeList.add(new Edge(first, second, distances.getDistance(object1, object2)));
    }

    public List<Vertex> getIsolatedVertices() {
        List<Vertex> isolatedVertices = new ArrayList<>();
        for (Vertex vertex: vertexList) {
            if (vertex.isIsolated())
                isolatedVertices.add(vertex);
        }
        return isolatedVertices;
    }
}
