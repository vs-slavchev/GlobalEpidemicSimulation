package sample;

/**
 * Created by Yasen on 4/3/2017.
 */

import java.util.ArrayList;
import java.util.List;
import sample.Environment;

public class World {

    private List<Country> listOfCountries;
    private List<Country> listOfInfectedCountries;

    public World() {
        listOfCountries = new ArrayList<Country>();
        listOfInfectedCountries = new ArrayList<Country>();
    }

    private void migrate() {

    }
}
