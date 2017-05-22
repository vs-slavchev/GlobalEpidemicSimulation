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

public class World implements Serializable {

    private ArrayList<Point2D> points;
    private List<Country> countries;
    private Time time;

    public World() {
        countries = DatasetReader.readCountryInfo();
        time = new Time();
    }

    public List<Country> getCountries() {
        return countries;
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

    public boolean containsInfectionPoint(Point2D toCheck) {
        return getAllInfectionPoints().stream()
                .anyMatch(point2D -> ConstantValues.doublePointsEqual(point2D, toCheck));
    }

    public Time getTime() {
        return time;
    }
}
