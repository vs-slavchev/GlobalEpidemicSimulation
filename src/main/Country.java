package main;


import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Owner: Yasen
 */
public class Country implements Serializable {

    public static final int QUEUE_MAX_SIZE = 10_000;
    private String name;
    private String countryID;
    private String GovernmentForm;
    private long unaffectedPopulation;
    private long infectedPopulation;
    private long deadPopulation;
    private long curedPopulation;
    private long migrationRate;
    private Environment environment;
    private Queue<Point2D> infectionPoints;

    public Country(String countryName, String countryId, long unaffectedPopulation, String governmentForm, long infectedPopulation,
                   long deadPopulation, long curedPopulation, long rateOfMigration, Environment environment) {
        this.name = countryName;
        this.countryID = countryId;
        this.unaffectedPopulation = unaffectedPopulation;
        this.GovernmentForm = governmentForm;
        this.infectedPopulation = infectedPopulation;
        this.deadPopulation = deadPopulation;
        this.curedPopulation = curedPopulation;
        this.migrationRate = rateOfMigration;
        this.environment = environment;
        infectionPoints = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);
    }

    public void modifyInfectedPopulation(int value) {
        if (unaffectedPopulation > 0) {
            if (unaffectedPopulation < value) {
                infectedPopulation += unaffectedPopulation;
                unaffectedPopulation = 0;
            } else {
                infectedPopulation += value;
                unaffectedPopulation -= value;
            }
            //System.out.println(infectedPopulation);
        }
    }

    public long getTotalPopulation() {
        return infectedPopulation + unaffectedPopulation;
    }

    public long getUnaffectedPopulation() {
        return unaffectedPopulation;
    }

    public long getInfectedPopulation() {
        return infectedPopulation;
    }

    public void setInfectedPopulation(long infectedPopulation) {
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
                ", countryCode=" + countryID +
                ", unaffectedPopulation=" + unaffectedPopulation +
                ", infectedPopulation=" + infectedPopulation +
                ", deadPopulation=" + deadPopulation +
                ", curedPopulation=" + curedPopulation +
                ", GovernmentForm=" + GovernmentForm +
                ", migrationRate=" + migrationRate +
                ", infectionPoints.size()=" + infectionPoints.size() +
                ", ENV=" + environment.toString() +
                '}';
    }
}
