package salesman.antColony;

import graphs.*;
import salesman.Distances;

import java.util.*;

/**
 * A class responsible for the Ant Colony Optimisation
 * algorithm for solving the Travelling Salesman Problem.
 *
 * @author mikolajdeja
 * @version 2021.04.13
 */
public class AntColony {
    private List<Ant> ants;
    private final double[][] pheromoneTrails;
    private final double[][] distancesMatrix;
    private double[][] desirability;
    private double[] desirabilityRow;
    private final Distances distances;
    private final List<Vertex> vertices;
    private List<Vertex> best;

    // Constants for the simulation
    private final int numberOfAnts;
    private final static int NUMBER_STEPS = 100;
    private final static double DST_POWER = 3;
    private final static double PHEROMONE_POWER = 1;
    private final static double EVAPORATION_RATE = 0.5;
    private final static double CITY_FACTOR = 500;

    /**
     * The constructor for the AntColony.
     *
     * @param input The input list of locations.
     * @param distances The distances used.
     */
    public AntColony(List<Vertex> input, Distances distances) {
        vertices = input;
        this.distances = distances;
        ants = new ArrayList<>();
        numberOfAnts = vertices.size() / 5;
        pheromoneTrails = new double[vertices.size()][vertices.size()];
        best = new ArrayList<>();
        desirabilityRow = new double[vertices.size()];
        desirability = new double[vertices.size()][vertices.size()];
        distancesMatrix = distances.getDistanceMatrix(input);
        double initialPheromoneIntensity = 1.0 / vertices.size();
        for (int i = 0; i < pheromoneTrails.length; i++)
            for (int j = 0; j < pheromoneTrails.length; j++)
                pheromoneTrails[i][j] = initialPheromoneIntensity;
    }

    /**
     * Solve the travelling salesman problem.
     *
     * @return THe resulting list of vertices.
     */
    public List<Vertex> solveTravellingSalesmanProblem() {
            runSimulation();
            return best;
    }

    /**
     * Create the ants that act in the simulation.
     */
    private void createAnts() {
        ants = new ArrayList<>();
        for (int i = 0; i < numberOfAnts; i++) {
            ants.add(new Ant(this));
        }
    }

    /**
     * Simulate one turn of the simulation.
     */
    private void simulateOneTurn() {
        for (Ant ant : ants) {
            int index = getNextIndex(ant);
            ant.visitNext(index, distancesMatrix[ant.getCurrentIndex()][index]);
        }
    }

    /**
     * Simulate one step for the simulation.
     */
    public void simulateOneStep() {
        createAnts();
        calculateDesirability();
        for (int i = 0; i + 1 < vertices.size(); i++) {
            simulateOneTurn();
        }
        double min = Double.MAX_VALUE;
        for (Ant ant : ants) {
            ant.visitNext(ant.getFirstIndex(), distancesMatrix[ant.getCurrentIndex()][ant.getFirstIndex()]);
            if (ant.getDist() < min) {
                min = ant.getDist();
                best = new ArrayList<>(ant.getPath());
            }
        }
        depositPheromones();
        evaporatePheromones();
    }

    /**
     * Run the whole simulation.
     */
    public void runSimulation() {
        List<Vertex> route;
        simulateOneStep();
        route = best;
        for (int i = 0; i < NUMBER_STEPS ; i++) {
            simulateOneStep();
            if (distances.getPathDistance(route) > distances.getPathDistance(best)) {
                route = new ArrayList<>(best);
            }
        }
    }

    /**
     * Get the next index for a given ant.
     *
     * @param ant The ant for which the next index is to be found.
     * @return The next index for a given ant.
     */
    private int getNextIndex(Ant ant) {
        int currentIndex = ant.getCurrentIndex();
        desirabilityRow = desirability[currentIndex];
        double s = 0.0;
        for (int i = 0; i < vertices.size(); i++) {
            if (!ant.visited(i)) {
                s += desirabilityRow[i];
            }
        }
        for (int i = 0; i < vertices.size(); i++) {
            desirabilityRow[i] /= s;
        }
        double runningSum = 0.0;
        Random random = new Random();
        double n = random.nextDouble();
        for (int i = 0; i < vertices.size(); i++) {
            if (!ant.visited(i)) {
                runningSum += desirabilityRow[i];
                if (runningSum > n) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * Evaporate the pheromones after each step.
     */
    private void evaporatePheromones() {
        for (int i = 0; i < pheromoneTrails.length; i++)
            for (int j = 0; j < pheromoneTrails.length; j++)
                pheromoneTrails[i][j] *= (1 - EVAPORATION_RATE);
    }

    /**
     * Deposit all the pheromones after each step.
     */
    private void depositPheromones() {
        for (Ant ant : ants) {
            boolean[][] travelled = ant.getTravelled();
            for (int i = 0; i < pheromoneTrails.length; i++)
                for (int j = 0; j < pheromoneTrails.length; j++)
                    if (travelled[i][j])
                        pheromoneTrails[i][j] += CITY_FACTOR / distancesMatrix[i][j];
        }
    }

    /**
     * Calculate the desirability (at least partially).
     */
    private void calculateDesirability() {
        desirability = new double[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (i != j) {
                    double dst = distancesMatrix[i][j];
                    desirability[i][j] = Math.pow(CITY_FACTOR / dst, DST_POWER) * Math.pow(pheromoneTrails[i][j], PHEROMONE_POWER);
                }
            }
        }
    }

    /**
     * Get the vertex list.
     *
     * @return The vertices.
     */
    public List<Vertex> getVertices() {
        return vertices;
    }
}
