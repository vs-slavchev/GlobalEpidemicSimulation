package main;

/**
 * Created by Yasen on 4/3/2017.
 */

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class World {

    private List<Country> countries;
    private List<Country> infectedCountries;

    public World() {
        countries = new ArrayList<>();
        infectedCountries = new ArrayList<>();

        countries.add(
                new Country("Bulgaria", 7_000_000, 0, 0, 0, 100,
                new Environment(5, 20, 29, 30, 20, 500)));
    }

    private void migrate() {

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
        ArrayList<Point2D> points = new ArrayList<>();
        countries.stream().forEach(country -> points.addAll(country.getInfectionPoints()));
        return points;
    }
}
