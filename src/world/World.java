package world;

import files.DatasetReader;
import map.GeoFinder;

import java.io.Serializable;
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

    public World(GeoFinder geoFinder) {
        countries = DatasetReader.readCountryInfo();
        cleanCities(geoFinder);
        time = new Time();
        changeMonthsDependingOnInSimulationTime();
    }

    public void cleanCities(GeoFinder geoFinder) {
        for (Country country : countries) {
            country.setCities(geoFinder.removeCitiesInWater(geoFinder.removeOverlappingCities(country.getCities())));
        }
    }

    private Optional<Country> getFirstCountry(Predicate<Country> countryPredicate) {
        return countries.stream()
                .filter(countryPredicate)
                .findFirst();
    }

    public Optional<Country> getCountryByCode(String code) {
        return getFirstCountry(country -> country.getCode().equals(code));
    }

    private long sumPopulationFrom(ToLongFunction<Country> countryFunction) {
        return countries.stream()
                .mapToLong(countryFunction)
                .sum();
    }

    public void changeMonthsDependingOnInSimulationTime() {
        for (Country county : countries) {
            if (county.getEnvironment().getTemperatures().length > 1) {
                county.getEnvironment().setCurrentTemperature(county.getEnvironment()
                        .getTemperatureFromArrayByIntex(time.getMonthAsInteger() - 1));
            }
        }
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
        return Math.round((float) getWorldTotalInfectedPopulation() / (float) getWorldTotalPopulation() * 100);
    }

    public int calculateWorldTotalCuredPercentage() {
        return Math.round((float) getWorldTotalCuredPopulation() / (float) getWorldTotalPopulation() * 100);
    }

    public Time getTime() {
        return time;
    }
}
