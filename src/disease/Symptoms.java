package disease;

/**
 * Owner: Ivan
 */

public class Symptoms {

    private SymptomType type;
    private DiseaseProperties properties;

    public Symptoms(SymptomType symptonType, DiseaseProperties props) {
        this.type = symptonType;
        this.properties = props;
    }
}
