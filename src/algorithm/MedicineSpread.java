package algorithm;

import disease.DiseaseProperties;
import disease.DiseaseType;
import disease.SymptomType;
import main.Medicine;
import world.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owner: Kaloyan
 */
public class MedicineSpread {
    private List<Medicine> medicines;
    private List<Country> countries;
    private Medicine medicine;

    public MedicineSpread() {
        countries = new ArrayList<>();
        medicines = new ArrayList<>();
        addMedicine(new Medicine("Syrup", DiseaseType.VIRUS, SymptomType.COUGH,
                new DiseaseProperties(12, 23, 12, 0.01)));
        addMedicine(new Medicine("Syrup2", DiseaseType.VIRUS, SymptomType.COUGH,
                new DiseaseProperties(22, 33, 20, 0.3)));
    }

    public Medicine getMedicine(String name) {
        for (Medicine m : medicines
                ) {
            if (name.equals(m.getName())) {
                return m;
            }
        }
        return null;
    }

    public List<Medicine> getMedicineList() {
        return medicines;
    }

    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }

    public void removeMedicine(Medicine medicine) {
        medicines.remove(medicine);
    }

    public void addInitialCountry(Country country) {
        countries.add(country);
    }

    public void addCountry(Country country) {
        countries.add(country);
    }

    public List<Country> getCountries() {
        return countries;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public void medicineAlgorithm() {
        for (Country country : countries) {
            if (country.getInfectedPopulation() != 0
                    && country.getInfectedPopulation() + country.getCuredPopulation() <= country.getTotalPopulation()) {
                //double currentVirulence = medicine.getProperties().getVirulence();
                double peopleToBeCured = medicine.getProperties().getVirulence() * (double) country.getInfectedPopulation();
                peopleToBeCured = Math.max(peopleToBeCured, 1);
                country.addCuredPopulation((long) peopleToBeCured);
                country.setInfectedPopulation(country.getInfectedPopulation() - (long) peopleToBeCured);
                //medicine.getProperties().setVirulence(currentVirulence + 0.02);
            }
        }
        /*if(disease.getType()==medicine.getTargetedTypesType()){
            changeDiseaseProperties(disease,medicine,country,true);
        }
        else
            changeDiseaseProperties(disease,medicine,country,false);*/
    }

    /* public void changeDiseaseProperties(Disease disease,Medicine medicine,Country country,Boolean SameType){
         if(SameType){
             disease.getProperties().setLethality(disease.getProperties().getLethality()-(int)(medicine.getProperties().getLethality()*1.1));
             disease.getProperties().setPreferredTemperature(disease.getProperties().getPreferredTemperature()-(medicine.getProperties().getPreferredTemperature()*1.1));
             disease.getProperties().setTemperatureTolerance(disease.getProperties().getTemperatureTolerance()-(medicine.getProperties().getTemperatureTolerance()*1.1));
             disease.getProperties().setVirulence(disease.getProperties().getVirulence()-(medicine.getProperties().getVirulence()*1.1));
             }
         else
         disease.getProperties().setLethality(disease.getProperties().getLethality()-medicine.getProperties().getLethality());
         disease.getProperties().setPreferredTemperature(disease.getProperties().getPreferredTemperature()-medicine.getProperties().getPreferredTemperature());
         disease.getProperties().setTemperatureTolerance(disease.getProperties().getTemperatureTolerance()-medicine.getProperties().getTemperatureTolerance());
         disease.getProperties().setVirulence(disease.getProperties().getVirulence()-medicine.getProperties().getVirulence());
     }*/
    public void changeCuredPeopleValue(long currentSickPeople, List<Country> countries) {
        for (Country country : countries
                ) {
            if (currentSickPeople > country.getInfectedPopulation()) {
                country.addCuredPopulation(currentSickPeople - country.getInfectedPopulation());
            }
        }
    }
}