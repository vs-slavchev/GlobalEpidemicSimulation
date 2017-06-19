package world;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Owner: Yasen
 */
public class Country implements Serializable {

    private String name;
    private String code;
    private String governmentForm;
    private long population;
    private long infectedPopulation;
    private long deadPopulation;
    private long curedPopulation;
    private long migrationRate;
    private Environment environment;
    private List<Country> neighbours;
    private List<City> cities;

    public Country(String countryName, String code, long Population,
                   String governmentForm, long infectedPopulation, long deadPopulation,
                   long curedPopulation, long rateOfMigration, Environment environment) {
        this.name = countryName;
        this.code = code;
        this.population = Population;
        this.governmentForm = governmentForm;
        this.infectedPopulation = infectedPopulation;
        this.deadPopulation = deadPopulation;
        this.curedPopulation = curedPopulation;
        this.migrationRate = rateOfMigration;
        this.environment = environment;
        neighbours = new ArrayList<>();
        cities = new ArrayList<>();
    }

    public long getTotalPopulation() {
        return population;
    }

    public List<Country> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Country country) {
        neighbours.add(country);
    }

    public long getInfectedPopulation() {
        return infectedPopulation;
    }

    public void setInfectedPopulation(long number) {
        this.infectedPopulation = number;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getName() {
        return name;
    }

    public long getCuredPopulation() {
        return curedPopulation;
    }

    public void setCuredPopulation(long number) {
        this.curedPopulation = number;
    }

    public void addCuredPopulation(long number) {
        this.curedPopulation += number;
    }

    public long gethealthyPopulation() {
        return this.population - this.infectedPopulation;
    }

    public int getPercentageOfInfectedPopulation() {
        int percentage;
        try{
            percentage = Math.round(this.infectedPopulation * 100 / this.population);
        } catch (ArithmeticException ae) {
            percentage = 0;
        }
        return percentage;
    }

    public void infectPopulation(int number) {
        this.infectedPopulation += number;
        infectedPopulation = Math.min(infectedPopulation, population - curedPopulation);
    }

    public void infectNeighbours() {
        int next = ThreadLocalRandom.current().nextInt(0, 101);
        boolean spread = next < this.getPercentageOfInfectedPopulation();
        if (spread) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, this.neighbours.size() + 1);
            for (Country country : this.neighbours) {
                if (randomNum < 2)
                    country.infectPopulation(1);
            }
        }
    }

    public String getCode() {
        return code;
    }

    public void addCity(final City city) {
        // the capital is the first city to be added
        if (cities.isEmpty()) {
            city.setIsCapital();
        }
        cities.add(city);
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (name != null ? !name.equals(country.name) : country.name != null) return false;
        return code != null ? code.equals(country.code) : country.code == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", countryCode=" + code +
                ", population=" + population +
                ", deadPopulation=" + deadPopulation +
                ", curedPopulation=" + curedPopulation +
                ", governmentForm=" + governmentForm +
                ", migrationRate=" + migrationRate +
                ", ENV=" + environment.toString() +
                '}';
    }
}