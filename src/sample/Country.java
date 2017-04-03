package sample;

/**
 * Created by Yasen on 4/2/2017.
 */

import sample.Environment;

public class Country {

    private String description;
    private long unaffectedPopulation;
    private long infectedPopulation;
    private long deadPopulation;
    private long curedPopulation;
    private long migrationRate;
    private Environment environment;

    public Country(String countryDescription, long unaffectedPopulation, long infectedPopulation,
        long deadPopulation, long curedPopulation, long rateOfMigration, Environment environment) {
        this.description = countryDescription;
        this.unaffectedPopulation = unaffectedPopulation;
        this.infectedPopulation = infectedPopulation;
        this.deadPopulation = deadPopulation;
        this.curedPopulation = curedPopulation;
        this.migrationRate = rateOfMigration;
        this.environment = environment;
    }
}
