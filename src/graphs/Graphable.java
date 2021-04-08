package graphs;

import java.util.Objects;

public abstract class Graphable {
    private String name;
    private double latitude;
    private double longitude;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graphable graphable = (Graphable) o;
        return Double.compare(graphable.latitude, latitude) == 0 && Double.compare(graphable.longitude, longitude) == 0 && Objects.equals(name, graphable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude);
    }

    @Override
    public String toString() {
        return getName();
    }
}
