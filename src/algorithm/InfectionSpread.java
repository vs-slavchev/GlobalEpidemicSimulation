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

    public List<Disease> diseaseList;

    public InfectionSpread() {
        diseaseList = new ArrayList<Disease>();
        diseaseList.add(new Disease("ebola", DiseaseType.BACTERIA,
                new DiseaseProperties(10, 25, 10, 0.1)));
    }

    public void infectCountry(Country country) {

        Disease ebola = diseaseList.get(0);

        int toBeInfected = (int)Math.round(
                ((ebola.getProperties().getVirulence() + 1)
                        * country.getInfectedPopulation()));

        country.modifyInfectedPopulation(toBeInfected);
    }

    public void multiplyCountry(Country country) {

    }
}
