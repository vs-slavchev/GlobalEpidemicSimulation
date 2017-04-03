package main;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yasen on 4/2/2017.
 */

public class Country {

    private String name;
    private long unaffectedPopulation;
    private long infectedPopulation;
    private long deadPopulation;
    private long curedPopulation;
    private long migrationRate;
    private Environment environment;
    private List<Point2D> infectionPoints;

    public Country(String countryDescription, long unaffectedPopulation, long infectedPopulation,
                   long deadPopulation, long curedPopulation, long rateOfMigration, Environment environment) {
        this.name = countryDescription;
        this.unaffectedPopulation = unaffectedPopulation;
        this.infectedPopulation = infectedPopulation;
        this.deadPopulation = deadPopulation;
        this.curedPopulation = curedPopulation;
        this.migrationRate = rateOfMigration;
        this.environment = environment;
        infectionPoints = new ArrayList<>();
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
            System.out.println(infectedPopulation);
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

    public void setInfectedPopulation(long infectedPopulation) {
        this.infectedPopulation = infectedPopulation;
    }

    public List<Point2D> getInfectionPoints() {
        return infectionPoints;
    }

    public void addInfectionPoint(Point2D infectionPoint) {
        infectionPoints.add(infectionPoint);
        if (infectionPoints.size() > 10_000) {
            infectionPoints.remove(0);
        }
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", unaffectedPopulation=" + unaffectedPopulation +
                ", infectedPopulation=" + infectedPopulation +
                ", deadPopulation=" + deadPopulation +
                ", curedPopulation=" + curedPopulation +
                ", migrationRate=" + migrationRate +
                ", infectionPoints.size()=" + infectionPoints.size() +
                '}';
    }
}
