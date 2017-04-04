package algorithm;

/**
 * Created by Kaloyan on 4/3/2017.
 */

import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import main.Country;

import java.util.ArrayList;
import java.util.List;

public class InfectionSpread {

    private List<Disease> diseaseList;

    public InfectionSpread() {
        diseaseList = new ArrayList<Disease>();
    }

    public void infectCountry(Country country) {

    }

    public Disease getMainDisease() {
        diseaseList.add(new Disease("ebola", DiseaseType.BACTERIA,
                new DiseaseProperties(10, 25, 10, 0.1)));
        return diseaseList.get(0);
    }

    public void multiplyCountry(Country country) {

    }

    public List<Disease> getDiseaseList() {
        return diseaseList;
    }
}
