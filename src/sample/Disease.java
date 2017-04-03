package sample;

/**
 * Created by Yasen on 4/2/2017.
 */

import sample.DiseaseType;
import sample.DiseaseProperties;

public class Disease {

    private String name;
    private DiseaseType type;
    private DiseaseProperties properties;

    public Disease(String nameOfDisease, DiseaseType typeOfDisease,
        DiseaseProperties diseaseProperties) {
        this.name = nameOfDisease;
        this.type = typeOfDisease;
        this.properties = diseaseProperties;
    }

}



