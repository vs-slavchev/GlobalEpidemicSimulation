package sample;

/**
 * Created by Owner on 4/2/2017.
 */

import sample.DiseaseType;
import sample.DiseaseProperties;
import sample.SymptomType;

public class Medicine {

    private String name;
    private DiseaseType targetedTypes;
    private SymptomType targetedSymptoms;
    private DiseaseProperties properties;

    public Medicine(String nameOfMedicine, DiseaseType diseaseType, SymptomType symptomType,
        DiseaseProperties props) {
        this.name = nameOfMedicine;
        this.targetedTypes = diseaseType;
        this.targetedSymptoms = symptomType;
        this.properties = props;
    }
}
