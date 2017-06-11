package disease;

/**
 * Owner: Ivan
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

    public void setLethality(int number) {
        this.lethality = number;
    }

    public double getPreferredTemperature() {
        return preferredTemperature;
    }

    public void setPreferredTemperature(double number) {
        this.preferredTemperature = number;
    }

    public double getTemperatureTolerance() {
        return temperatureTolerance;
    }

    public void setTemperatureTolerance(double number) {
        this.temperatureTolerance = number;
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