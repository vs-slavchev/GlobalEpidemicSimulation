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
    private List<Country> tempCount;

    public MedicineSpread() {
        countries = new ArrayList<>();
        medicines = new ArrayList<>();
        tempCount = new ArrayList<>();
        addMedicine(new Medicine("Syrup", DiseaseType.VIRUS, SymptomType.COUGH,
                new DiseaseProperties(12, 23, 12, 0.01)));
        addMedicine(new Medicine("Syrup2", DiseaseType.VIRUS, SymptomType.COUGH,
                new DiseaseProperties(22, 33, 20, 0.3)));
    }

    public List<Medicine> getMedicineList() {
        return medicines;
    }

    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }

    public void addInitialCountry(Country country) {
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
                double peopleToBeCured = medicine.getProperties().getVirulence() * (double) country.getInfectedPopulation();
                peopleToBeCured = Math.max(peopleToBeCured, 1);
                country.addCuredPopulation((long) peopleToBeCured);
                country.setInfectedPopulation(country.getInfectedPopulation() - (long) peopleToBeCured);
                if (country.getCuredPopulation() > country.getTotalPopulation() / 2) {
                    if (!countries.containsAll(country.getNeighbours())) {
                        tempCount.addAll(country.getNeighbours());
                    }
                }
            }

        }
    }

    public void addNewCountriesToBeCured() {
        if (tempCount.size() > 0) {
            for (Country c : tempCount) {
                if (countries.contains(c)) {
                    continue;
                } else {
                    countries.add(c);
                }
            }
        }
    }
}