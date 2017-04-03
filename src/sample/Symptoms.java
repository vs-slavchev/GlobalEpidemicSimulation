package sample;

/**
 * Created by Yasen on 4/2/2017.
 */

import sample.DiseaseProperties;
import sample.SymptomType;


public class Symptoms {

    private SymptomType type;
    private DiseaseProperties properties;

    public Symptoms(SymptomType symptonType, DiseaseProperties props) {
        this.type = symptonType;
        this.properties = props;
    }
}
