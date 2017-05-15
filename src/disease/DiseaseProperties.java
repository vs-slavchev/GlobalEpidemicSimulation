package disease;

/**
 * Created by Yasen on 4/2/2017.
 */
public class DiseaseProperties {

    private int lethality;
    private int preferredTemperature;
    private int temperatureTolerance;
    private double virulence;

    public DiseaseProperties(int lethality,
                             int preferredTemperature,
                             int temperatureTolerance,
                             double virulence) {
        this.lethality = lethality;
        this.preferredTemperature = preferredTemperature;
        this.temperatureTolerance = temperatureTolerance;
        this.virulence = virulence;
    }

    public int getLethality() {
        return lethality;
    }

    public int getPreferredTemperature() {
        return preferredTemperature;
    }

    public int getTemperatureTolerance() {
        return temperatureTolerance;
    }

    public double getVirulence() {
        return virulence;
    }
}
