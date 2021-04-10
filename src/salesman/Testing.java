package salesman;

import data.handout.AirbnbData;
import graphs.Graphable;

import java.util.ArrayList;
import java.util.List;

public class Testing {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        TravellingSalesman salesman = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        Distances distances = salesman.getDistances();
        List<Graphable> cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 1000));
        System.out.println(distances.getPathDistance(salesman.christofidesNN(cities)));
        long t2 = System.currentTimeMillis();
        TravellingSalesman salesman2 = new TravellingSalesman(DistanceCalculationMethod.LAMBERT);
        cities = new ArrayList<>(AirbnbData.getListingList().subList(0, 1000));
        System.out.println(salesman2.christofides(cities).getDist());
        long t3 = System.currentTimeMillis();

        System.out.println(t2 - t1 + " " + (t3 - t2));
    }
}
