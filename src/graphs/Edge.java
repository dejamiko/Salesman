package graphs;

import java.util.Objects;

/**
 * A class responsible for holding information about
 * edges in a graph.
 *
 * @author mikolajdeja
 * @version 2021.04.07
 */
public class Edge {
    private final Vertex first;
    private final Vertex second;
    private final double length;

    /**
     * A constructor for the Edge objects.
     *
     * @param first The first vertex.
     * @param second The second vertex.
     * @param length The length of the edge.
     */
    public Edge(Vertex first, Vertex second, double length) {
        this.first = first;
        this.second = second;
        this.length = length;
    }

    /**
     * @return The first vertex.
     */
    public Vertex getFirst() {
        return first;
    }

    /**
     * @return The second vertex.
     */
    public Vertex getSecond() {
        return second;
    }

    /**
     * Return the other vertex, given one.
     *
     * @param vertex The given vertex.
     * @return The other vertex, given one.
     */
    public Vertex getTheOtherOne(Vertex vertex) {
        if (vertex.equals(first))
            return second;
        else if (vertex.equals(second))
            return first;
        else
            return null;
    }

    /**
     * Get the length of the edge.
     *
     * @return The length of the edge.
     */
    public double getLength() {
        return length;
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
        Edge edge = (Edge) o;
        return Double.compare(edge.length, length) == 0 && Objects.equals(first, edge.first) && Objects.equals(second, edge.second);
    }

    /**
     * @return The hashcode of an edge object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second, length);
    }
}
