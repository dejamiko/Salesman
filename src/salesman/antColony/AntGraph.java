package salesman.antColony;

import graphs.*;
import salesman.Distances;
import salesman.TSPCalculationMethod;

import java.util.*;

public class AntGraph extends SalesmanGraph {
    private final int number;
    private final int numberSteps = 70;
    private List<Ant> ants;
    private double[][] pheromoneTrails;
    private double[][] distancesMatrix;
    private double[] desirability;
    private double[][] des;
    private final double dstPower = 3;
    private final double pheromonePower = 1;
    private final double evaporationRate = 0.5;
    private final double initialPheromoneIntensity;
    private final double Q = 500;
    private List<Vertex> best;

    public AntGraph(List<Location> input, Distances distances) {
        super(input, distances);
        ants = new ArrayList<>();
        number = getVertexList().size() / 10;
        pheromoneTrails = new double[getVertexList().size()][getVertexList().size()];
        initialPheromoneIntensity = 1.0 / getVertexList().size();
        for (int i = 0; i < pheromoneTrails.length; i++)
            for (int j = 0; j < pheromoneTrails.length; j++)
                pheromoneTrails[i][j] = initialPheromoneIntensity;
        best = new ArrayList<>();
        desirability = new double[getVertexList().size()];
        des = new double[getVertexList().size()][getVertexList().size()];
        distancesMatrix = getDistances().getDistanceMatrix(input);
    }

    public void solveTravellingSalesmanProblem(TSPCalculationMethod method) {
        if (method == TSPCalculationMethod.ANT_COLONY_OPTIMISATION) {
            runSimulation();
        } else {
            super.solveTravellingSalesmanProblem(method);
        }
    }

    private void createAnts() {
        ants = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            ants.add(new Ant(this));
        }
    }

    private void simulateOneStep() {
        for (Ant ant : ants) {
            int index = getNextIndex(ant);
            ant.visitNext(index, distancesMatrix[ant.getCurrentIndex()][index]);
        }
    }

    public void simulateOneTurn() {
        createAnts();
        calculateDes();
        for (int i = 0; i + 1 < getVertexList().size(); i++) {
            simulateOneStep();
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

    public void runSimulation() {
        List<Vertex> route;
        simulateOneTurn();
        route = best;
        for (int i = 0; i < numberSteps; i++) {
            simulateOneTurn();
            if (getDistances().getPathDistance(route) > getDistances().getPathDistance(best)) {
                route = new ArrayList<>(best);
            }
        }
        drawNewPath(route);
    }

    private int getNextIndex(Ant ant) {
        int currentIndex = ant.getCurrentIndex();
        desirability = des[currentIndex];
        double s = 0.0;
        for (int i = 0; i < getVertexList().size(); i++) {
            if (!ant.visited(i)) {
                s += desirability[i];
            }
        }
        for (int i = 0; i < getVertexList().size(); i++) {
            desirability[i] /= s;
        }
        double runningSum = 0.0;
        Random random = new Random();
        double n = random.nextDouble();
        for (int i = 0; i < getVertexList().size(); i++) {
            if (!ant.visited(i)) {
                runningSum += desirability[i];
                if (runningSum > n) {
                    return i;
                }
            }
        }
        return 0;
    }

    // Evaporate after all pheromones are deposited
    private void evaporatePheromones() {
        for (int i = 0; i < pheromoneTrails.length; i++)
            for (int j = 0; j < pheromoneTrails.length; j++)
                pheromoneTrails[i][j] *= (1 - evaporationRate);
    }

    private void depositPheromones() {
        for (Ant ant : ants) {
            boolean[][] travelled = ant.getTravelled();
            for (int i = 0; i < pheromoneTrails.length; i++)
                for (int j = 0; j < pheromoneTrails.length; j++)
                    if (travelled[i][j])
                        pheromoneTrails[i][j] += Q / distancesMatrix[i][j];
        }
    }

    private void calculateDes() {
        des = new double[getVertexList().size()][getVertexList().size()];
        for (int i = 0; i < getVertexList().size(); i++) {
            for (int j = 0; j < getVertexList().size(); j++) {
                if (i != j) {
                    double dst = distancesMatrix[i][j];
                    des[i][j] = Math.pow(Q / dst, dstPower) * Math.pow(pheromoneTrails[i][j], pheromonePower);
                }
            }
        }
    }
}
