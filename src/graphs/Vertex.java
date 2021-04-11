package graphs;

import salesman.Graphable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is responsible for a vertex.
 *
 * @author mikolajdeja
 * @version 2021.04.09
 */
public class Vertex implements Graphable {
    private final Location contents;
    private List<Edge> incidentEdges;

    /**
     * A constructor for the vertex with the contents given.
     *
     * @param contents The contents stored in the vertex.
     */
    public Vertex(Location contents) {
        this.contents = contents;
        incidentEdges = new ArrayList<>();
    }

    /**
     * Add an incident edge.
     *
     * @param another A vertex this one is adjacent to.
     * @param distance The distance between the vertices.
     */
    public void addEdge(Vertex another, double distance) {
        incidentEdges.add(new Edge(this, another, distance));
    }

    /**
     * @return The contents stored in the vertex.
     */
    public Location getContents() {
        return contents;
    }

    /**
     * @return The list of incident edges.
     */
    public List<Edge> getIncidentEdges() {
        return incidentEdges;
    }

    /**
     * @return The number of adjacent vertices.
     */
    public int getNumberOfAdjacent() {
        return incidentEdges.size();
    }

    /**
     * @return The list of adjacent vertices.
     */
    public List<Vertex> getAdjacentVertices() {
        List<Vertex> adjacentVertices = new ArrayList<>();
        for (Edge edge : incidentEdges) {
            adjacentVertices.add(edge.getTheOtherOne(this));
        }
        return adjacentVertices;
    }

    /**
     * @return True if the vertex is isolated, false otherwise.
     */
    public boolean isIsolated() {
        return getNumberOfAdjacent() == 0;
    }

    /**
     * @return True if the vertex is pendant, false otherwise.
     */
    public boolean isPendant() {
        return getNumberOfAdjacent() == 1;
    }

    /**
     * @return True if the vertex has an odd number of adjacent vertices.
     */
    public boolean isOddNumbered() {
        return getNumberOfAdjacent() % 2 == 1;
    }

    /**
     * Check if the vertex contains given contents.
     *
     * @param contents The contents to be checked.
     * @return True if the vertex contains given contents.
     */
    public boolean contains(Location contents) {
        return this.contents.equals(contents);
    }

    /**
     * Remove an incident edge.
     *
     * @param vertex The vertex with which an edge is to be deleted.
     */
    public void removeEdge(Vertex vertex) {
        for (Edge edge : incidentEdges)
            if (edge.getTheOtherOne(this).equals(vertex)) {
                incidentEdges.remove(edge);
                break;
            }
    }

    /**
     * Remove all incident edges.
     */
    public void removeAllEdges() {
        incidentEdges = new ArrayList<>();
    }

    /**
     * Check if the vertex is adjacent to another one.
     *
     * @param other The vertex to be checked.
     * @return True if it is adjacent.
     */
    public boolean isAdjacent(Vertex other) {
        return incidentEdges.stream().filter(e -> e.getTheOtherOne(this).equals(other)).collect(Collectors.toList()).size() > 0;
    }

    /**
     * Check if an object is equal to this.
     *
     * @param o The object to check.
     * @return True if object o is equal to this.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(contents, vertex.contents) && Objects.equals(incidentEdges, vertex.incidentEdges);
    }

    /**
     * @return The hashcode of an edge object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(contents);
    }

    /**
     * @return The string representation of the object.
     */
    @Override
    public String toString() {
        return contents.toString();
    }
}
