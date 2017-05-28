package files;

import main.Country;
import main.Environment;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Owner: Kaloyan
 */

public class DatasetReader {

    public static List<Country> readCountryInfo() {
        //file path
        String File = "./scripts/Dataset consolidation/country consolidated data.txt";
        String line = "";
        String SplitBy = ";";
        List<Country> countries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(File))) {
            while ((line = br.readLine()) != null) {
                //declaring all the variable needed to assign from the file
                String Name = "";
                String Code = "";
                int Population = 0;
                //uninfected pop
                float PopulationDensity = 0;
                String GovernmentForm = "";
                float AirPollution = 0;
                float PublicHealthExpenditure = 0;
                //HealthExpenditureperCapita not used for now
                float HealthExpenditureperCapita = 0;
                /*[Name, Code, Population, Population Density,
                Government Form, Air Pollution, Public Health Expenditure,
                Health Expenditure per Capita] expected order*/
                String[] country = line.split(SplitBy);
                /*going through the array and checking with what every item
                starts with and if we have a match then after that
               we assign the value to the proper one*/
                for (String string : country) {
                    if (string.startsWith("3")) {
                        string = string.substring(1);
                        Population = Integer.parseInt(string);
                    } else if (string.startsWith("4")) {
                        string = string.substring(1);
                        PopulationDensity = Float.parseFloat(string);
                    } else if (string.startsWith("5")) {
                        string = string.substring(1);
                        GovernmentForm = string;
                    } else if (string.startsWith("6")) {
                        string = string.substring(1);
                        AirPollution = Float.parseFloat(string);
                    } else if (string.startsWith("7")) {
                        string = string.substring(1);
                        PublicHealthExpenditure = Float.parseFloat(string);
                    } else if (string.startsWith("8")) {
                        string = string.substring(1);
                        HealthExpenditureperCapita = Float.parseFloat(string);
                    } else if (!isAllUpper(string)) {
                        Name = string;
                    } else if (isAllUpper(string)) {
                        Code = string;
                    }
                }
                // adding the new countries with the taken data to the countries list
                countries.add(
                        new Country(Name, Code, Population, GovernmentForm, 0, 0,
                                0, 100,
                                new Environment(PublicHealthExpenditure, 20, 0, new double[1],
                                        AirPollution, 20, PopulationDensity)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //calling method readTemperatures
        readTemperatures(countries);
        return countries;
    }

    private static void readTemperatures(List<Country> countries) {
        //file path
        String File = "./scripts/countriesAverageTemperatures.txt";
        String line = "";
        String SplitBy = ", ";
        //array for the avg temps -11 foreach month + the avg for the year
        double[] monthTemperatures = new double[12];
        try (BufferedReader br = new BufferedReader(new FileReader(File))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] tempFileData = line.split(SplitBy);
                for (Country c : countries) {
                    /*getting the name of the current country making it all upper and
             comparing it to the first item in the array(expected country name)*/
                    if (c.getName().toUpperCase().equals(tempFileData[0].toUpperCase())) {
                        //adding the avg yearly temperature
                        c.getEnvironment().addAvgYearlyTemp(Double.parseDouble(tempFileData[1]));
                        for (int i = 0; i < 11; i++) {
                            //adding the left temps from Jan- Dec in the monthTemperatures[]
                            monthTemperatures[i] = Double.parseDouble(tempFileData[i + 2]);
                        }
                        //adding the temps
                        c.getEnvironment().addTemperatures(monthTemperatures);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param text ref for a string to be compared
     * @return true if the text parameter is all upper
     */
    private static boolean isAllUpper(String text) {
        return text.equals(text.toUpperCase());
    }
}
