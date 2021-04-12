package salesman.antColony;

import graphs.Edge;
import graphs.Vertex;

public class AntEdge extends Edge {

    private double pheromone;

    /**
     * A constructor for the Edge objects.
     *
     * @param first  The first vertex.
     * @param second The second vertex.
     * @param length The length of the edge.
     * @param pheromone The pheromone on this edge.
     */
    public AntEdge(Vertex first, Vertex second, double length, double pheromone) {
        super(first, second, length);
        this.pheromone = pheromone;
    }

    /**
     * @return The pheromone on this edge.
     */
    public double getPheromone() {
        return pheromone;
    }

    /**
     * Set the pheromone on this edge.
     *
     * @param pheromone The pheromone to be set.
     */
    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }
}
