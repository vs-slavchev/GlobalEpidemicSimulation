package world;

import files.DatasetReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

/**
 * Owner: Yasen
 */

public class World implements Serializable {

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

    public Optional<Country> getCountryByCode(String code) {
        return getFirstCountry(country -> country.getCode().equals(code));
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

    private long getWorldTotalCuredPopulation() {
        return sumPopulationFrom(Country::getCuredPopulation);
    }

    public List<Country> getListOfCountries() {
        return countries;
    }


    public int calculateWorldTotalInfectedPercentage() {
        return (int) (
                (float) getWorldTotalInfectedPopulation() / (float) getWorldTotalPopulation()
                        * 100);
    }

    public int calculateWorldTotalCuredPercentage() {
        return (int) (
                (float) getWorldTotalCuredPopulation() / (float) getWorldTotalPopulation()
                        * 100);
    }

    public Time getTime() {
        return time;
    }
}
