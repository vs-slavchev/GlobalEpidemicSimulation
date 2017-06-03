package disease;

/**
 * Owner: Yasen
 */

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiseaseType getType() {
        return type;
    }

    public void setType(DiseaseType type) {
        this.type = type;
    }

    public DiseaseProperties getProperties() {
        return properties;
    }

    public void setProperties(DiseaseProperties properties) {
        this.properties = properties;
    }

    @Override
    public String toString(){
        return name + ","+type + ","+properties.toString();
    }
}



