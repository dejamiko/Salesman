package data.cities;

import graphs.Graphable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class responsible for holding data about cities.
 *
 * @author mikolajdeja
 * @version 2021.04.08
 */
public class CityData {

    /**
     * Create a list of sample cities.
     *
     * @return A list of sample cities.
     */
    public static List<Graphable> getSampleCities() {
        List<Graphable> cities = new ArrayList<>();

        cities.add(new City("Krakow", 50.061389, 19.937222));
        cities.add(new City("Berlin", 52.52, 13.405));
        cities.add(new City("Katowice", 50.258333, 19.0275));
        cities.add(new City("Prague", 50.083333, 14.416667));
        cities.add(new City("Warsaw", 52.233333, 21.016667));
        cities.add(new City("Munich", 48.133333, 11.566667));
        cities.add(new City("Vienna", 48.2, 16.366667));
        cities.add(new City("Gliwice", 50.283333, 18.666667));
        cities.add(new City("Sopot", 54.433333, 18.55));
        cities.add(new City("Rome", 41.883333, 12.5));

        return cities;
    }
}
