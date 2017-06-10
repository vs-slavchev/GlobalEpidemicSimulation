package world;

/**
 * Owner: Yasen
 */

import files.DatasetReader;
import main.ConstantValues;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

public class World implements Serializable {

    private ArrayList<Point2D> points;
    private List<Country> countries;
    private Time time;

    public World() {
        countries = DatasetReader.readCountryInfo();
        time = new Time();
    }

    private Optional<Country> getFirstCountry(Predicate<Country> countryPredicate) {
        return countries.stream()
                .filter(countryPredicate)
                .findFirst();
    }

    /*public Optional<Country> getCountryByName(String countryName) {
        return getFirstCountry(country -> country.getName().equals(countryName));
    }*/

    public Optional<Country> getCountryByCode(String code) {
        return getFirstCountry(country -> country.getCode().equals(code));
    }

    public ArrayList<Point2D> getAllInfectionPoints() {
        points = new ArrayList<>();
        countries.forEach(country -> points.addAll(country.getInfectionPoints()));
        return points;
    }

    public ArrayList<City> getAllCities() {
        ArrayList<City> cities = new ArrayList<>();
        countries.forEach(country -> cities.addAll(country.getCities()));
        return cities;
    }

    private long sumPopulationFrom(ToLongFunction<Country> countryFunction) {
        return countries.stream()
                .mapToLong(countryFunction)
                .sum();
    }

    private long getWorldTotalPopulation() {
        return sumPopulationFrom(Country::getTotalPopulation);
    }

    private long getWorldTotalInfectedPopulation() {
        return sumPopulationFrom(Country::getInfectedPopulation);
    }

    public List<Country> getListOfCountries() {
        return countries;
    }


    public int calculateWorldTotalInfectedPercentage() {
        return (int) (
                (float) getWorldTotalInfectedPopulation() / (float) getWorldTotalPopulation()
                        * 100);
    }

    public boolean containsInfectionPoint(Point2D toCheck) {
        return getAllInfectionPoints().stream()
                .anyMatch(point2D -> ConstantValues.doublePointsEqual(point2D, toCheck));
    }

    /**
     * Checks if a point is contained in a specific country identified by its code.
     */
    public boolean countryContainsInfectionPoint(String countryCode, Point2D toCheck) {
        return getCountryByCode(countryCode)
                .map(country -> country.getInfectionPoints()
                        .stream()
                        .anyMatch(point2D -> ConstantValues.doublePointsEqual(point2D, toCheck)))
                .orElse(false);
    }

    public Time getTime() {
        return time;
    }
}