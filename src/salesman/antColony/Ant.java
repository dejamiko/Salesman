package salesman.antColony;

import graphs.Vertex;

import java.util.*;

public class Ant {
    private List<Vertex> path;
    private boolean[] visited;
    private final AntGraph graph;
    private double dist;
    private int currentIndex;
    private final int first;
    private final boolean[][] travelled;

    public Ant(AntGraph graph) {
        this.graph = graph;
        dist = 0.0;
        Random random = new Random();
        travelled = new boolean[graph.getVertexList().size()][graph.getVertexList().size()];
        visited = new boolean[graph.getVertexList().size()];
        path = new ArrayList<>();
        first = random.nextInt(graph.getVertexList().size());
        visitNext(first, 0.0);
    }

    public void visitNext(int index, double distance) {
        path.add(graph.getVertexList().get(index));
        travelled[currentIndex][index] = true;
        visited[index] = true;
        currentIndex = index;
        dist += distance;
    }

    public List<Vertex> getPath() {
        return path;
    }

    public double getDist() {
        return dist;
    }

    public boolean visited(int index) {
        return visited[index];
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getFirstIndex() {
        return first;
    }

    public boolean[][] getTravelled() {
        return travelled;
    }
}
