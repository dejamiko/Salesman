package salesman;

import data.cities.CityData;
import data.handout.AirbnbData;
import graphs.Location;
import graphs.SalesmanGraph;

import java.util.ArrayList;
import java.util.List;

public class Testing {
    public static void main(String[] args) {
        ants();
    }

    public static void ants() {
        List<Location> cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 400));
        Distances distances = new Distances(DistanceCalculationMethod.LAMBERT);
        SalesmanGraph graph = new SalesmanGraph(cities, distances);
        graph.solveTravellingSalesmanProblem(TSPCalculationMethod.ANT_COLONY_OPTIMISATION);
        System.out.println(graph.getRoute() + " " + graph.getDist());
    }


    public static void bruteForce() {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Location> cities = CityData.getSampleCities();
        cities = salesman.solveTravellingSalesmanProblem(cities, TSPCalculationMethod.BRUTE_FORCE);
        System.out.println(cities + " " + distances.getPathDistance(cities));
        long t2 = System.currentTimeMillis();
        TravellingSalesman salesman1 = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances1 = salesman1.getDistances();
        cities = CityData.getSampleCities();
        SalesmanGraph graph = new SalesmanGraph(cities, distances1);
        graph.solveTravellingSalesmanProblem(TSPCalculationMethod.BRUTE_FORCE);
        System.out.println(graph.getRoute() + " " + graph.getDist());
        long t3 = System.currentTimeMillis();

        System.out.println(t2 - t1 + " " + (t3 - t2));
    }

    public static void christofides() {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Location> cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 1000));
        cities = salesman.solveTravellingSalesmanProblem(cities, TSPCalculationMethod.CHRISTOFIDES_DOUBLE_EDGES);
        System.out.println(distances.getPathDistance(cities));
        long t2 = System.currentTimeMillis();
        cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 1000));
        SalesmanGraph graph = new SalesmanGraph(cities, new Distances(DistanceCalculationMethod.LAMBERT));
        graph.solveTravellingSalesmanProblem(TSPCalculationMethod.CHRISTOFIDES_DOUBLE_EDGES);
        System.out.println(graph.getDist());
        long t3 = System.currentTimeMillis();
        System.out.println(t2 - t1 + " " + (t3 - t2));
    }

    public static void improvedNN() {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Location> cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 500));
        System.out.println(distances.getPathDistance(salesman.solveTravellingSalesmanProblem(cities, TSPCalculationMethod.IMPROVED_NEAREST_NEIGHBOUR)));
        long t2 = System.currentTimeMillis();
        cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 500));
        SalesmanGraph graph = new SalesmanGraph(cities, new Distances(DistanceCalculationMethod.LAMBERT));
        graph.solveTravellingSalesmanProblem(TSPCalculationMethod.IMPROVED_NEAREST_NEIGHBOUR);
        System.out.println(graph.getDist());
        long t3 = System.currentTimeMillis();
        System.out.println(t2 - t1 + " " + (t3 - t2));
    }

    public static void opt2() {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Location> cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 300));
        System.out.println(distances.getPathDistance(salesman.opt2(salesman.solveTravellingSalesmanProblem(cities, TSPCalculationMethod.NEAREST_NEIGHBOUR))));
        long t2 = System.currentTimeMillis();
        cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 300));
        SalesmanGraph graph = new SalesmanGraph(cities, new Distances(DistanceCalculationMethod.LAMBERT));
        graph.solveTravellingSalesmanProblem(TSPCalculationMethod.NEAREST_NEIGHBOUR);
        graph.opt2();
        System.out.println(graph.getDist());
        long t3 = System.currentTimeMillis();
        System.out.println(t2 - t1 + " " + (t3 - t2));
    }
}
