package sample;

/**
 * Created by Yasen on 4/2/2017.
 */
public class DiseaseProperties {

    private int lethality;
    private int preferredTemperature;
    private int temperatureTolerance;
    private int virulence;

    public DiseaseProperties(int lethality, int preferredTemperature, int temperatureTolerance,
        int virulence) {
        this.lethality = lethality;
        this.preferredTemperature = preferredTemperature;
        this.temperatureTolerance = temperatureTolerance;
        this.virulence = virulence;
    }
}
