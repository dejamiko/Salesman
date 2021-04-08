package graphs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vertex<E> {
    private final E contents;
    private final List<Edge> incidentEdges;

    public Vertex(E contents) {
        this.contents = contents;
        incidentEdges = new ArrayList<>();
    }

    public void addEdge(Vertex another, double distance) {
        incidentEdges.add(new Edge(this, another, distance));
    }

    public E getContents() {
        return contents;
    }

    public List<Edge> getIncidentEdges() {
        return incidentEdges;
    }

    public int getNumberOfAdjacent() {
        return incidentEdges.size();
    }

    public List<Vertex> getAdjacentVertices() {
        List<Vertex> adjacentVertices = new ArrayList<>();
        for (Edge edge: incidentEdges) {
            adjacentVertices.add(edge.getTheOtherOne(this));
        }
        return adjacentVertices;
    }

    public boolean isIsolated() {
        return getNumberOfAdjacent() == 0;
    }

    public boolean contains(E contents) {
        return this.contents.equals(contents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return Objects.equals(contents, vertex.contents) && Objects.equals(incidentEdges, vertex.incidentEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contents, incidentEdges);
    }
}
