package main;

/**
 * Created by Yasen on 4/2/2017.
 */

/**
 * Owner: Kaloyan
 */
public class Environment {

    private float medicalInfrastructure;
    private float humidity;
    private float temperature;
    private float airPollution;
    private float waterPollution;
    private float populationDensity;

    public Environment(float medInfrastructure, float humidity, float temperature, float pollutionOfAir,
                       float pollutionOfWater, float density) {
        this.medicalInfrastructure = medInfrastructure;
        this.humidity = humidity;
        this.temperature = temperature;
        this.airPollution = pollutionOfAir;
        this.waterPollution = pollutionOfWater;
        this.populationDensity = density;
    }
    public String toString(){
        return "medic= "+medicalInfrastructure+ ", humidity= "+humidity+", temp= "+temperature+", airPol= "
                +airPollution+", waterPoll= "+waterPollution+", popDensity= "+populationDensity;
    }
}
