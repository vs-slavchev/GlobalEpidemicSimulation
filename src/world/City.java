package world;

import java.io.Serializable;

/**
 * Owner: Ivaylo
 */

public class City implements Serializable {
    private String name;
    private long population;
    private double latitude;
    private double longitude;

    public City(String name, long population, double latitude, double longitude) {
        this.name = name;
        this.population = population;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public long getPopulation() {
        return population;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
