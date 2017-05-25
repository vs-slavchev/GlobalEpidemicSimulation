package main;

/**
 * Owner: Yasen
 */

import files.DatasetReader;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToLongFunction;

public class World implements Serializable {

    private ArrayList<Point2D> points;
    private List<Country> countries;
    private Time time;

    public World() {
        countries = DatasetReader.readCountryInfo();
        time = new Time();
    }

    public Optional<Country> getCountry(String countryName) {
        return countries.stream()
                .filter(country -> country.getName().equals(countryName))
                .findFirst();
    }

    public ArrayList<Point2D> getAllInfectionPoints() {
        points = new ArrayList<>();
        countries.forEach(country -> points.addAll(country.getInfectionPoints()));
        return points;
    }

    public long sumPopulationFrom(ToLongFunction<Country> countryFunction) {
        return countries.stream()
                .mapToLong(countryFunction)
                .sum();
    }

    public long getWorldTotalPopulation() {
        return sumPopulationFrom(Country::getTotalPopulation);
    }

    public long getWorldTotalInfectedPopulation() {
        return sumPopulationFrom(Country::getInfectedPopulation);
    }

    public int calculateWorldTotalInfectedPercentage() {
        return (int)(getWorldTotalInfectedPopulation() / (double)getWorldTotalPopulation());
    }

    public boolean containsInfectionPoint(Point2D toCheck) {
        return getAllInfectionPoints().stream()
                .anyMatch(point2D -> ConstantValues.doublePointsEqual(point2D, toCheck));
    }

    public Time getTime() {
        return time;
    }
}
