package sample;

/**
 * Created by Yasen on 4/2/2017.
 */
public class Environment {

    private int medicalInfrastructure;
    private int humidity;
    private int temperature;
    private int airPollution;
    private int waterPollution;
    private int populationDensity;

    public Environment(int medInfrastructure, int humidity, int temperature, int pollutionOfAir,
        int pollutionOfWater, int density) {
        this.medicalInfrastructure = medInfrastructure;
        this.humidity = humidity;
        this.temperature = temperature;
        this.airPollution = pollutionOfAir;
        this.waterPollution = pollutionOfWater;
        this.populationDensity = density;
    }
}
