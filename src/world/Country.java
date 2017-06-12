package world;


import interfaces.CountryPercentageListener;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Owner: Yasen
 */
public class Country implements Serializable {

    private static final int QUEUE_MAX_SIZE = 10_000;
    private String name;
    private String code;
    private String governmentForm;
    private long population;
    private long infectedPopulation;
    private long deadPopulation;
    private long curedPopulation;
    private long migrationRate;
    private Random random;
    private Environment environment;
    private Queue<Point2D> infectionPoints;
    private List<Country> neighbours;
    private List<CountryPercentageListener> listeners;
    private List<City> cities;

    public Country(String countryName, String code, long Population,
                   String governmentForm, long infectedPopulation, long deadPopulation,
                   long curedPopulation, long rateOfMigration, Environment environment) {
        this.name = countryName;
        this.code = code;
        this.population = Population;
        this.governmentForm = governmentForm;
        this.infectedPopulation = infectedPopulation;
        this.deadPopulation = deadPopulation;
        this.curedPopulation = curedPopulation;
        this.migrationRate = rateOfMigration;
        this.environment = environment;
        this.random = new Random();
        infectionPoints = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);
        neighbours = new ArrayList<>();
        listeners = new ArrayList<>();
        cities = new ArrayList<>();
    }

    public long getTotalPopulation() {
        return population;
    }

    public List<Country> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Country country) {
        neighbours.add(country);
    }

    public long getInfectedPopulation() {
        return infectedPopulation;
    }

    public void setInfectedPopulation(long number) {
        this.infectedPopulation = number;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getName() {
        return name;
    }

    public Queue<Point2D> getInfectionPoints() {
        return infectionPoints;
    }


    /**
     * Add an infection point in map coordinates.
     */
    public void addInfectionPoint(Point2D infectionPoint) {
        if (infectionPoints.size() >= QUEUE_MAX_SIZE) {
            infectionPoints.poll();
        }
        infectionPoints.add(infectionPoint);
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", countryCode=" + code +
                ", population=" + population +
                ", deadPopulation=" + deadPopulation +
                ", curedPopulation=" + curedPopulation +
                ", governmentForm=" + governmentForm +
                ", migrationRate=" + migrationRate +
                ", infectionPoints.size()=" + infectionPoints.size() +
                ", ENV=" + environment.toString() +
                '}';
    }

    public long getCuredPopulation() {
        return curedPopulation;
    }

    public void setCuredPopulation(long number) {
        this.curedPopulation = number;
    }

    public void addCuredPopulation(long number) {
        this.curedPopulation += number;
    }

    public long gethealthyPopulation() {
        return this.population - this.infectedPopulation;
    }

    public int getPercentageOfInfectedPopulation() {
        int Percentage = Math.round(this.infectedPopulation * 100 / this.population);
        return Percentage;
    }

    public void infectPopulation(int number) {
        this.infectedPopulation += number;
        infectedPopulation = Math.min(infectedPopulation, population);

        if (getPercentageOfInfectedPopulation() >= 50 && getPercentageOfInfectedPopulation() <= 80) {
            for (CountryPercentageListener listener : listeners) {
                listener.CountryReachedBreakPoint(52.12, 54.12);
            }
        }
    }
    public void infectNeighbours(){
        int next = ThreadLocalRandom.current().nextInt(0, 101);
        boolean Spread = next < this.getPercentageOfInfectedPopulation();
               if (Spread) {
                   int randomNum = ThreadLocalRandom.current().nextInt(0, this.neighbours.size() + 1);
                   for (Country country : this.neighbours){
                       if (randomNum<1)
                       country.infectPopulation(1);
                   }
                }
    }
    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (name != null ? !name.equals(country.name) : country.name != null) return false;
        return code != null ? code.equals(country.code) : country.code == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }

    public void addCity(City city) {
        cities.add(city);
    }

    public List<City> getCities() {
        return cities;
    }

    public void addListeners(CountryPercentageListener listener) {
        listeners.add(listener);
    }
}