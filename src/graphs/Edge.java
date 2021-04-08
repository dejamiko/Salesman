package graphs;

import java.util.Objects;

public class Edge {
    private final Vertex first;
    private final Vertex second;
    private final double length;

    public Edge(Vertex first, Vertex second, double length) {
        this.first = first;
        this.second = second;
        this.length = length;
    }

    public Vertex getFirst() {
        return first;
    }

    public Vertex getSecond() {
        return second;
    }

    public Vertex getTheOtherOne(Vertex vertex) {
        if (vertex.equals(first))
            return second;
        else if (vertex.equals(second))
            return first;
        else
            return null;
    }

    public double getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Double.compare(edge.length, length) == 0 && Objects.equals(first, edge.first) && Objects.equals(second, edge.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, length);
    }
}
