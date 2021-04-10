package salesman;

import graphs.Graphable;
import graphs.Vertex;

import java.util.*;

/**
 * The class responsible for calculations related to distances.
 *
 * @author Mikolaj Deja 20010020
 * @version 2021.03.25
 */
public class Distances {
    // The Earth's flattening as defined by WGS 84
    private final static double FLATTENING = 1 / 298.257223563;
    // The Earth's mean radius as defined by IUGG
    private final static double RADIUS = 6371.0009;
    // The Earth's equatorial radius as defined by IUGG
    private final static double EQUATORIAL_RADIUS = 6378.1370;
    private final boolean usingLambert;
    private final Map<Set<Graphable>, Double> pairwiseDistances;

    /**
     * Initialise the object and set a boolean flag to see
     * what method of calculating distance is to be used.
     *
     * @param method The method to be used.
     */
    public Distances(DistanceCalculationMethod method) {
        usingLambert = !method.equals(DistanceCalculationMethod.HAVERSINE);
        pairwiseDistances = new HashMap<>();
    }

    /**
     * Get the whole path length for the list of places.
     *
     * @param route The list of places.
     * @return The length of the path between the places.
     */
    public double getPathDistance(List<Graphable> route) {
        if (route == null)
            throw new IllegalArgumentException("getPathDistance got a null input");
        if (route.size() < 2)
            throw new IllegalArgumentException("getPathDistance got an input of length <2");

        double ans = 0;
        for (int i = 0; i + 1 < route.size(); ++i) {
            ans += getDistance(route.get(i), route.get(i + 1));
        }
        // We want the whole cycle
        ans += getDistance(route.get(0), route.get(route.size() - 1));
        return ans;
    }


    public double[][] getDistanceMatrix(ArrayList<Vertex> vertexList) {
        double[][] distances = new double[vertexList.size()][vertexList.size()];
        for (int i = 0; i < distances.length; ++i)
            for (int j = i + 1; j < distances[i].length; ++j) {
                distances[i][j] = getDistance(vertexList.get(i), vertexList.get(j));
                distances[j][i] = distances[i][j]; //symmetry
            }
        return distances;
    }

    public double getDistance(Vertex vertex1, Vertex vertex2) {
        if (vertex1.equals(vertex2))
            return 0.0;

        Set<Graphable> set = new HashSet<>(Arrays.asList(vertex1.getContents(), vertex2.getContents()));
        Double distance = pairwiseDistances.get(set);
        if (distance == null) {
            distance = calculateDistance(vertex1.getContents(), vertex2.getContents());
            pairwiseDistances.put(set, distance);
        }
        return distance;
    }

    /**
     * Create a distance matrix for all places.
     *
     * @param listings List of places.
     * @return The calculated distance matrix for all the places.
     */
    public double[][] getDistanceMatrix(List<Graphable> listings) {
        if (listings == null)
            throw new IllegalArgumentException("getDistanceMatrix got a null input");
        if (listings.size() < 2)
            throw new IllegalArgumentException("getDistanceMatrix got an input of length <2");

        double[][] distances = new double[listings.size()][listings.size()];
        for (int i = 0; i < distances.length; ++i)
            for (int j = i + 1; j < distances[i].length; ++j) {
                distances[i][j] = getDistance(listings.get(i), listings.get(j));
                distances[j][i] = distances[i][j]; //symmetry
            }
        return distances;
    }

    /**
     * Get the distance between two listings: try finding it in the hashmap,
     * if not present, calculate it and store it in the hashmap.
     *
     * @param listing1 The first place.
     * @param listing2 The second place.
     * @return Distance between the two places.
     */
    public double getDistance(Graphable listing1, Graphable listing2) {
        if (listing1 == null || listing2 == null)
            throw new IllegalArgumentException("getDistance got a null input");
        if (listing1.equals(listing2))
            return 0.0;

        Set<Graphable> set = new HashSet<>(Arrays.asList(listing1, listing2));
        Double distance = pairwiseDistances.get(set);
        if (distance == null) {
            distance = calculateDistance(listing1, listing2);
            pairwiseDistances.put(set, distance);
        }
        return distance;
    }

    /**
     * Calculate the distance between two locations.
     *
     * @param listing1 The first place.
     * @param listing2 The second place.
     * @return Distance between the two places.
     */
    private double calculateDistance(Graphable listing1, Graphable listing2) {
        if (listing1 == null || listing2 == null)
            throw new IllegalArgumentException("getDistance got a null input");

        double lat1 = degToRad(listing1.getLatitude());
        double long1 = degToRad(listing1.getLongitude());
        double lat2 = degToRad(listing2.getLatitude());
        double long2 = degToRad(listing2.getLongitude());

        if (usingLambert)
            return calculateDistance(lat1, long1, lat2, long2);
        return getDistanceHaversine(lat1, long1, lat2, long2);
    }

    /**
     * Get distance between places using Lambert's formula for long lines.
     * It gives accuracy on the order of 10 meters over thousands of kilometers.
     * <p>
     * https://en.wikipedia.org/wiki/Geographical_distance#Lambert's_formula_for_long_lines
     *
     * @param lat1  Latitude of the first place.
     * @param long1 Longitude of the first place.
     * @param lat2  Latitude of the second place.
     * @param long2 Longitude of the second place.
     * @return The distance between the two places.
     */
    private double calculateDistance(double lat1, double long1, double lat2, double long2) {
        lat1 = reducedLat(lat1);
        lat2 = reducedLat(lat2);
        double p = (lat1 + lat2) / 2;
        double q = (lat1 - lat2) / 2;
        double centralAngle = getCentralAngle(lat1, long1, lat2, long2);
        double x = (centralAngle - Math.sin(centralAngle)) * Math.pow(Math.sin(p) * Math.cos(q) / Math.cos(centralAngle / 2), 2);
        double y = (centralAngle + Math.sin(centralAngle)) * Math.pow(Math.cos(p) * Math.sin(q) / Math.sin(centralAngle / 2), 2);

        return EQUATORIAL_RADIUS * (centralAngle - FLATTENING * (x + y) / 2);
    }

    /**
     * Calculate the distance using the haversine formula.
     *
     * @param lat1  Latitude of the first place.
     * @param long1 Longitude of the first place.
     * @param lat2  Latitude of the second place.
     * @param long2 Longitude of the second place.
     * @return The distance between those places (the great-circle distance,
     * ignoring obstacles and roads)
     */
    private double getDistanceHaversine(double lat1, double long1, double lat2, double long2) {
        return RADIUS * getCentralAngle(lat1, long1, lat2, long2);
    }

    /**
     * Get the central angle as calculated using the haversine formula
     * <p>
     * https://en.wikipedia.org/wiki/Haversine_formula
     *
     * @param lat1  Latitude of the first place.
     * @param long1 Longitude of the first place.
     * @param lat2  Latitude of the second place.
     * @param long2 Longitude of the second place.
     * @return The central angle between the two places
     */
    private double getCentralAngle(double lat1, double long1, double lat2, double long2) {
        return 2 * Math.asin(Math.sqrt(hav(lat2 - lat1) + Math.cos(lat1) * Math.cos(lat2) * hav(long2 - long1)));
    }

    /**
     * Calculate the angle in radians.
     *
     * @param deg Degrees to be converted.
     * @return Angle in radians.
     */
    private double degToRad(double deg) {
        return deg * Math.PI / 180.0;
    }

    /**
     * The haversine function sin^2(theta/2)
     * <p>
     * https://en.wikipedia.org/wiki/Versine#Haversine
     *
     * @param angle The angle for which the function is calculated.
     * @return The result of the haversine function.
     */
    private double hav(double angle) {
        return Math.pow(Math.sin(angle / 2.0), 2);
    }

    /**
     * Get the reduced latitude of a place, as described here:
     * <p>
     * https://en.wikipedia.org/wiki/Latitude#Parametric_(or_reduced)_latitude
     *
     * @param latitude The latitude of a place.
     * @return The reduced latitude of a place.
     */
    private double reducedLat(double latitude) {
        return Math.atan((1 - FLATTENING) * Math.tan(latitude));
    }
}
