package data.cities;

import graphs.Graphable;

/**
 * A class responsible for holding information about a city.
 *
 * @author mikolajdeja
 * @version 20.03.2021
 */
public class City extends Graphable {

    /**
     * A constructor for a city.
     *
     * @param name      Name of the city.
     * @param latitude  The latitude of the city.
     * @param longitude The longitude of the city.
     */
    public City(String name, double latitude, double longitude) {
        super(name, latitude, longitude);
    }
}