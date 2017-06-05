package main;


import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Owner: Yasen
 */
public class Country implements Serializable {

    private static final int QUEUE_MAX_SIZE = 10_000;
    private String name;
    private String code;
    private String GovernmentForm;
    private long Population;
    private long infectedPopulation;
    private long deadPopulation;
    private long curedPopulation;
    private long migrationRate;
    private Environment environment;
    private Queue<Point2D> infectionPoints;

    public Country(String countryName, String code, long Population,
                   String governmentForm, long infectedPopulation, long deadPopulation,
                   long curedPopulation, long rateOfMigration, Environment environment) {
        this.name = countryName;
        this.code = code;
        this.Population = Population;
        this.GovernmentForm = governmentForm;
        this.infectedPopulation = infectedPopulation;
        this.deadPopulation = deadPopulation;
        this.curedPopulation = curedPopulation;
        this.migrationRate = rateOfMigration;
        this.environment = environment;
        infectionPoints = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);
    }

    public long getTotalPopulation() {
        return Population;
    }

    // let's say a point represents 2 percent of the population
    public long getInfectedPopulation() {
//        return (int)(Math.min(50, infectionPoints.size()) * 2 / 100.0 * Population);
        return infectedPopulation;
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
                ", Population=" + Population +
                ", deadPopulation=" + deadPopulation +
                ", curedPopulation=" + curedPopulation +
                ", GovernmentForm=" + GovernmentForm +
                ", migrationRate=" + migrationRate +
                ", infectionPoints.size()=" + infectionPoints.size() +
                ", ENV=" + environment.toString() +
                '}';
    }
    public long gethealthyPopulation() {
        return this.Population - this.infectedPopulation;
    }
    public int getPercentageOfInfectedPopulation(){
        long Percentage = this.infectedPopulation/this.Population*100;
        return (int)Percentage;
    }
    public void infectPopulation(int number){
        this.infectedPopulation += number;
        if (this.infectedPopulation > this.Population){
            this.infectedPopulation = this.Population;
        }
    }
//    public void infectPercentageOfPopulation(int percentage){
//        long number = this.Population * percentage /100;
//        this.infectedPopulation =+ number;
//        if (this.infectedPopulation > this.Population){
//            this.infectedPopulation = this.Population;
//        }
//    }
    /*public void modifyInfectedPopulation(int value) {
        if (unaffectedPopulation > 0) {
            if (unaffectedPopulation < value) {
                infectedPopulation += unaffectedPopulation;
                unaffectedPopulation = 0;
            } else {
                infectedPopulation += value;
                unaffectedPopulation -= value;
            }
        }
    }*/

    /*public long getUnaffectedPopulation() {
        return unaffectedPopulation;
    }*/

    /*public void setInfectedPopulation(long infectedPopulation) {
        this.infectedPopulation = infectedPopulation;
    }

    public long getDeadPopulation() {
        return deadPopulation;
    }

    public long getCuredPopulation() {
        return curedPopulation;
    }

    public long getMigrationRate() {
        return migrationRate;
    }*/

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
