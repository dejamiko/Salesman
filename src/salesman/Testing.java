package salesman;

import data.cities.CityData;
import data.handout.AirbnbData;
import graphs.Location;
import graphs.SalesmanGraph;

import java.util.ArrayList;
import java.util.List;

public class Testing {
    public static void main(String[] args) {
        opt2();
    }

    public static void bruteForce() {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Location> cities = CityData.getSampleCities();
        cities = salesman.bruteForceApproach(cities);
        System.out.println(cities + " " + distances.getPathDistance(cities));
        long t2 = System.currentTimeMillis();
        TravellingSalesman salesman1 = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances1 = salesman1.getDistances();
        cities = CityData.getSampleCities();
        SalesmanGraph graph = new SalesmanGraph(cities, distances1);
        graph.bruteForceApproach();
        System.out.println(graph.getRoute() + " " + graph.getDist());
        long t3 = System.currentTimeMillis();

        System.out.println(t2 - t1 + " " + (t3 - t2));
    }

    public static void christofides() {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Location> cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 1000));
        System.out.println(distances.getPathDistance(salesman.christofidesDoubleEdges(cities)));
        long t2 = System.currentTimeMillis();
        cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 1000));
        SalesmanGraph graph = new SalesmanGraph(cities, new Distances(DistanceCalculationMethod.LAMBERT));
        graph.christofidesDouble();
        System.out.println(graph.getDist());
        long t3 = System.currentTimeMillis();
        System.out.println(t2 - t1 + " " + (t3 - t2));
    }

    public static void improvedNN() {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Location> cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 500));
        System.out.println(distances.getPathDistance(salesman.improvedNearestNeighbouring(cities)));
        long t2 = System.currentTimeMillis();
        cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 500));
        SalesmanGraph graph = new SalesmanGraph(cities, new Distances(DistanceCalculationMethod.LAMBERT));
        graph.improvedNearestNeighbouring();
        System.out.println(graph.getDist());
        long t3 = System.currentTimeMillis();
        System.out.println(t2 - t1 + " " + (t3 - t2));
    }

    public static void opt2() {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Location> cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 500));
        System.out.println(distances.getPathDistance(salesman.opt2(salesman.nearestNeighbourAlgorithm(cities, 0))));
        long t2 = System.currentTimeMillis();
        cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 500));
        SalesmanGraph graph = new SalesmanGraph(cities, new Distances(DistanceCalculationMethod.LAMBERT));
        graph.nearestNeighbourAlgorithm(0);
        graph.opt2();
        System.out.println(graph.getDist());
        long t3 = System.currentTimeMillis();
        System.out.println(t2 - t1 + " " + (t3 - t2));
    }
}
