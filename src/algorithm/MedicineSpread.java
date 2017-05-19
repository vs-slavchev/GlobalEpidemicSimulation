package algorithm;

import disease.DiseaseProperties;
import disease.DiseaseType;
import disease.SymptomType;
import main.Medicine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by k_vol on 17/05/2017.
 */
public class MedicineSpread {
    private List<Medicine> medicines;

    public MedicineSpread() {
        medicines = new ArrayList<>();
        addMedicine(new Medicine("Syrup", DiseaseType.VIRUS, SymptomType.COUGH,
                new DiseaseProperties(12, 23, 22, 12)));
        addMedicine(new Medicine("Syrup2", DiseaseType.VIRUS, SymptomType.COUGH,
                new DiseaseProperties(22, 33, 42, 22)));
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

}