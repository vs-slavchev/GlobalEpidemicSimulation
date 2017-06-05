package disease;

/**
 * Owner: Yasen
 */
public class DiseaseProperties {

    private int lethality;
    private double preferredTemperature;
    private double temperatureTolerance;
    private double virulence;

    public DiseaseProperties(int lethality,
                             double preferredTemperature,
                             double temperatureTolerance,
                             double virulence) {
        this.lethality = lethality;
        this.preferredTemperature = preferredTemperature;
        this.temperatureTolerance = temperatureTolerance;
        this.virulence = virulence;
    }

    public int getLethality() {
        return lethality;
    }

    public double getPreferredTemperature() {
        return preferredTemperature;
    }

    public double getTemperatureTolerance() {
        return temperatureTolerance;
    }

    public double getVirulence() {
        return virulence;
    }

    public void setVirulence(double number) {
        this.virulence = number;
    }

    @Override
    public String toString() {
        return lethality + ", " + preferredTemperature + ", " + temperatureTolerance + ", " + virulence;
    }
}
