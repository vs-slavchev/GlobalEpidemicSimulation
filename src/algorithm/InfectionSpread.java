package algorithm;

import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import main.ConstantValues;
import map.MapCanvas;
import world.Country;
import world.FlightManager;
import world.World;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Owner: Nikolay
 */

public class InfectionSpread {

    private List<Disease> diseaseList;
    private Random random;
    private World world;
    private MapCanvas mapCanvas;
    private volatile FlightManager flightManager;

    public InfectionSpread(World world, MapCanvas mapCanvas) {
        diseaseList = new ArrayList<>();
        this.random = new Random();
        this.world = world;
        this.mapCanvas = mapCanvas;
        flightManager = new FlightManager();
        mapCanvas.setFlights(flightManager.getFlights());
        addDisease();
    }

    private void addDisease() {
        diseaseList.add(new Disease("Ebola", DiseaseType.BACTERIA,
                new DiseaseProperties(10, 10,
                        90, 0.6)));
    }

    public void addDisease(Disease disease) {
        diseaseList.add(disease);
    }

    public List<Disease> getDiseaseList() {
        return diseaseList;
    }

    public void applyAlgorithm(Disease disease) {
        if (diseaseList.isEmpty()) {
            addDisease();
        }
        for (Country country : world.getListOfCountries()) {
            if (country.getInfectedPopulation() == 0) {
                continue;
            }
            if (checkCountryToDiseaseCompatibility(country, disease)) {
                double currentVirulence = disease.getProperties().getVirulence();
                disease.getProperties()
                        .setVirulence(currentVirulence + country.getPercentageOfInfectedPopulation() / 1000);
                spreadInfection(country, disease.getProperties().getVirulence());
                country.infectNeighbours();
            }
        }
    }

    private void spreadInfection(Country country, Double virulence) {
        long toInfect = Math.round(country.getInfectedPopulation() * virulence);
        long fivePercent = Math.round(country.getTotalPopulation() * 5 / 100);
        toInfect = Math.min(toInfect, fivePercent);

        country.infectPopulation((int) toInfect);
    }

    private boolean checkCountryToDiseaseCompatibility(Country country, Disease disease) {

        double dTolerance = disease.getProperties().getTemperatureTolerance();
        double dTemp = disease.getProperties().getPreferredTemperature();
        double countryTemperature = country.getEnvironment().getAvgYearlyTemp();

        if (country.getInfectedPopulation() == country.getTotalPopulation()) {
            return false;
        }
        boolean temperatureIsInsideBonds = countryTemperature >= dTemp - dTolerance
                && countryTemperature <= dTemp + dTolerance;
        return temperatureIsInsideBonds && country.getInfectedPopulation() > 0;
    }

    /**
     * Update the flights and try to start a new one.
     */
    public void applyAirplaneAlgorithm() {
        flightManager.updateFlights(world.getTime().getTimeSpeed());
        //addInfectionToCountryAtMapCoordinates(ConstantValues.getRandomAirportCoordinates());

        // the chance to create a new flight depends on the run speed
        if (random.nextDouble() < world.getTime().getTimeSpeed() * ConstantValues.CHANCE_TO_START_FLIGHT) {
            flightManager.createRandomFlight();
        }
    }

    /**
     * Tries to infect Patient 0 in the country which is at the input coordinates.
     *
     * @param mapPoint A point in map coordinates where an infection point should be added.
     */
    public void addInfectionToCountryAtMapCoordinates(Point2D mapPoint) {
        String countryCode = mapCanvas.getGeoFinder()
                .getCountryCodeFromMapCoordinates(mapPoint.getX(), mapPoint.getY());

        Optional<Country> countryByCode = world.getCountryByCode(countryCode);
        countryByCode.ifPresent(country -> country.infectPopulation(1));
    }
}
