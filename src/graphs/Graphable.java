package graphs;

import java.util.Objects;

/**
 * An abstract class responsible for holding information about
 * graphable objects.
 *
 * @author mikolajdeja
 * @version 2021.04.08
 */
public abstract class Graphable {
    private final String name;
    private final double latitude;
    private final double longitude;

    /**
     * A constructor for the graphable objects.
     *
     * @param name The name of the graphable.
     * @param latitude The latitude of the graphable.
     * @param longitude The longitude of the graphable.
     */
    public Graphable(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return The name of the graphable.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The latitude of the graphable.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return The longitude of the graphable.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Check if an object is equal to this.
     *
     * @param o The object to check.
     * @return True if object o is equal to this.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graphable graphable = (Graphable) o;
        return Double.compare(graphable.latitude, latitude) == 0 && Double.compare(graphable.longitude, longitude) == 0 && Objects.equals(name, graphable.name);
    }

    /**
     * @return The hashcode of a graphable object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude);
    }

    /**
     * @return The string representation of the object.
     */
    @Override
    public String toString() {
        return getName();
    }
}
