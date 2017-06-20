package main;

/**
 * Owner: Nikolay
 */

import disease.DiseaseProperties;
import disease.DiseaseType;
import disease.SymptomType;

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

    public String getName() {
        return name;
    }

    public DiseaseProperties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return name + ", " + targetedTypes + ", " + targetedSymptoms + ", " + properties.toString();
    }
}