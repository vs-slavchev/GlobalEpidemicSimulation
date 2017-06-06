package world;

import java.io.Serializable;

/**
 * Owner: Kaloyan
 */
public class Environment implements Serializable {

    private float medicalInfrastructure;
    private float humidity;
    private double AvgYearlyTemp;
    private double[] temperatures;
    private float airPollution;
    private float waterPollution;
    private float populationDensity;
    private Boolean tempcheck = false;

    public Environment(float medInfrastructure, float humidity, double avgYearlyTemp, double[] temperature, float pollutionOfAir,
                       float pollutionOfWater, float density) {
        this.medicalInfrastructure = medInfrastructure;
        this.humidity = humidity;
        this.temperatures = temperature;
        this.AvgYearlyTemp = avgYearlyTemp;
        this.airPollution = pollutionOfAir;
        this.waterPollution = pollutionOfWater;
        this.populationDensity = density;
    }

    public boolean TempCheck() {
        return tempcheck;
    }

    public String AllTemps() {
        String temps = "";
        for (double d : temperatures) {
            temps += d + ",";
        }
        return "{" + temps + "}";
    }

    public void addAvgYearlyTemp(double temp) {
        this.AvgYearlyTemp = temp;
        tempcheck = true;
    }

    public void addTemperatures(double[] temps) {
        this.temperatures = temps;
        tempcheck = true;
    }

    public String toString() {
        return "medic= " + medicalInfrastructure + ", humidity= " + humidity + ", AVGYearlyTemp= " + AvgYearlyTemp + ", temps= "
                + AllTemps() + ", airPol= "
                + airPollution + ", waterPoll= " + waterPollution + ", popDensity= " + populationDensity;
    }

    public float getMedicalInfrastructure() {
        return medicalInfrastructure;
    }

    public float getHumidity() {
        return humidity;
    }

    public double getAvgYearlyTemp() {
        return AvgYearlyTemp;
    }

    public float getAirPollution() {
        return airPollution;
    }

    public float getPopulationDensity() {
        return populationDensity;
    }
}
