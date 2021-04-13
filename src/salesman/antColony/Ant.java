package salesman.antColony;

import graphs.Vertex;

import java.util.*;

/**
 * A class responsible for agents in the
 * ant colony optimisation algorithm.
 *
 * @author mikolajdeja
 * @version 2021.04.12
 */
public class Ant {
    private final AntColony colony;
    private final List<Vertex> path;
    private final boolean[] visited;
    private final boolean[][] travelled;
    private final int first;
    private double dist;
    private int currentIndex;

    /**
     * A constructor for an ant object.
     *
     * @param colony The graph the ant is in.
     */
    public Ant(AntColony colony) {
        this.colony = colony;
        dist = 0.0;
        Random random = new Random();
        travelled = new boolean[colony.getVertices().size()][colony.getVertices().size()];
        visited = new boolean[colony.getVertices().size()];
        path = new ArrayList<>();
        first = random.nextInt(colony.getVertices().size());
        visitNext(first, 0.0);
    }

    /**
     * Visit the next location.
     *
     * @param index The index of the new location.
     * @param distance The distance travelled.
     */
    public void visitNext(int index, double distance) {
        path.add(colony.getVertices().get(index));
        travelled[currentIndex][index] = true;
        visited[index] = true;
        currentIndex = index;
        dist += distance;
    }

    /**
     * Get the path the ant travelled.
     *
     * @return The path the ant travelled.
     */
    public List<Vertex> getPath() {
        return path;
    }

    /**
     * Get the distance the ant travelled.
     *
     * @return The distance the ant travelled.
     */
    public double getDist() {
        return dist;
    }

    /**
     * Return whether the ant visited the vertex with a given index.
     *
     * @param index The index in question.
     * @return True if the ant has visited it.
     */
    public boolean visited(int index) {
        return visited[index];
    }

    /**
     * Get the current index of the vertex the ant is in.
     *
     * @return The current index of the vertex the ant is in.
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Get the index of the first vertex the ant visited.
     *
     * @return The index of the first vertex the ant visited.
     */
    public int getFirstIndex() {
        return first;
    }

    /**
     * Return the matrix of traversed edges.
     *
     * @return The matrix of traversed edges.
     */
    public boolean[][] getTravelled() {
        return travelled;
    }
}
