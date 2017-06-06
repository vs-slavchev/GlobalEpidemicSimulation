package world;


import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private Environment environment;
    private Queue<Point2D> infectionPoints;
    private List<Country> neighbours;

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
        infectionPoints = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);
        neighbours = new ArrayList<>();
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
        long Percentage = this.infectedPopulation / this.population * 100;
        return (int) Percentage;
    }

    public void infectPopulation(int number) {
        this.infectedPopulation += number;
        infectedPopulation = Math.min(infectedPopulation, population);
    }
//    public void infectPercentageOfPopulation(int percentage){
//        long number = this.population * percentage /100;
//        this.infectedPopulation =+ number;
//        if (this.infectedPopulation > this.population){
//            this.infectedPopulation = this.population;
//        }
//    }

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
}