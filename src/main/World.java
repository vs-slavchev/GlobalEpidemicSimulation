package main;

/**
 * Created by Yasen on 4/3/2017.
 */

import org.omg.PortableInterceptor.HOLDING;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class World {

    private List<Country> countries;
    private List<Country> infectedCountries;
    private int Year=0;
    private int Month = 0;
    private int Day = 0;
    private int Hour = 0;
    private int Minutes = 0;
    private int Sec= 1 ;

    public World() {
        countries = new ArrayList<>();
        infectedCountries = new ArrayList<>();
    }

    private void migrate() {

    }

    public List<Country> getCountries() {
        return countries;
    }

    public Optional<Country> getCountry(String countryName) {
        return countries.stream()
                .filter(country -> country.getName().equals(countryName))
                .findFirst();
    }

    public ArrayList<Point2D> getAllInfectionPoints() {
        ArrayList<Point2D> points = new ArrayList<>();
        countries.forEach(country -> points.addAll(country.getInfectionPoints()));
        return points;
    }
    private static boolean isAllUpper(String s) {
        for(char c : s.toCharArray()) {
            if(Character.isLetter(c) && Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }
    public void readCountryInfo(){
        String File = "./scripts/Dataset consolidation/country consolidated data.txt";
        String line = "";
        String SplitBy = ";";

        try (BufferedReader br = new BufferedReader(new FileReader(File)))
        {
            while ((line = br.readLine()) != null)
            {
                /**[Name, Code, Population, Population Density, Government Form, Air Pollution, Public Health Expenditure, Health Expenditure per Capita]*/
                String Name = "";
                String Code = "";
                int Population = 0;
                //uninfected pop
                float PopulationDensity = 0;
                String GovernmentForm = "";
                float AirPollution = 0;
                float PublicHealthExpenditure = 0;
                float HealthExpenditureperCapita = 0;
                // use semicolon as separator
                String[] country = line.split(SplitBy);
                /**going through the array and checking with what every item starts and after that assigning the value to the proper one*/
                for (String string: country
                     ) {

                    if(string.startsWith("3")){
                        string = string.substring(1);
                        Population = Integer.parseInt(string);
                    }
                    else if(string.startsWith("4")){
                        string = string.substring(1);
                        PopulationDensity = Float.parseFloat(string);
                    }
                    else if(string.startsWith("5")){
                        string = string.substring(1);
                        GovernmentForm = string;
                    }
                    else if(string.startsWith("6")){
                        string = string.substring(1);
                        AirPollution = Float.parseFloat(string);
                    }
                    else if(string.startsWith("7")){
                        string = string.substring(1);
                        PublicHealthExpenditure = Float.parseFloat(string);
                    }
                    else if(string.startsWith("8")){
                        string = string.substring(1);
                        HealthExpenditureperCapita = Float.parseFloat(string);
                    }
                    else if(!isAllUpper(string)){
                        Name = string;
                    }
                    else if(isAllUpper(string)){
                        Code = string;
                    }

                }
                countries.add(
                        new Country(Name, Code, Population,GovernmentForm, 0, 0, 0, 100,
                                new Environment(PublicHealthExpenditure, 20,0, new double[1], AirPollution, 20, PopulationDensity)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readTemps(){
        String File = "./scripts/countriesAverageTemperatures.txt";
        String line = "";
        String SplitBy = ", ";
        double [] alltemps = new double[12];
        try (BufferedReader br = new BufferedReader(new FileReader(File))) {

            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] tempFileData = line.split(SplitBy);
                for (Country c:countries
                     ) {
                    if(c.getName().toUpperCase().equals(tempFileData[0].toUpperCase())){
                        c.getEnvironment().addAvgYearlyTemp(Double.parseDouble(tempFileData[1]));
                        for (int i = 0; i<11;i++){
                            alltemps[i]= Double.parseDouble(tempFileData[i+2]);
                        }
                        c.getEnvironment().addTemperatures(alltemps);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void elipcedTime(double speed){
                    Sec++;
                    Sec*=speed;
                    if(Sec >=60){
                        Sec = 0;
                        Minutes++;
                        if(Minutes>=60){
                            Minutes = 0;
                            Hour++;
                            if(Hour>=24){
                                Hour=0;
                                Day++;
                                if(Day>=30){
                                    Month++;
                                    Day=0;
                                    if(Month>=12){
                                        Month=0;
                                        Year++;
                                    }
                                }
                            }
                        }
                    }
    }
    public String GetTime(){
        if(Minutes<10 && Sec<10 && Hour<10){
            return "0"+Hour+":0"+Minutes+":0"+Sec+", "+Day+" Day(s)"+", "+Month+" Month(s)"+", "+Year+" Year(s)";
        }
        else if (Sec<10 & Minutes<10){
            return Hour+":0"+Minutes+":0"+Sec+", "+Day+" Day(s)"+", "+Month+" Month(s)"+", "+Year+" Year(s)";
        }
        else if (Sec<10 && Hour<10){
            return "0"+Hour+":"+Minutes+":0"+Sec+", "+Day+" Day(s)"+", "+Month+" Month(s)"+", "+Year+" Year(s)";
        }
        else if (Minutes<10 && Hour<10){
            return "0"+Hour+":0"+Minutes+":"+Sec+", "+Day+" Day(s)"+", "+Month+" Month(s)"+", "+Year+" Year(s)";
        }
        else if (Sec<10){
            return Hour+":"+Minutes+":0"+Sec+", "+Day+" Day(s)"+", "+Month+" Month(s)"+", "+Year+" Year(s)";
        }
        else if (Minutes<10){
            return Hour+":0"+Minutes+":"+Sec+", "+Day+" Day(s)"+", "+Month+" Month(s)"+", "+Year+" Year(s)";
        }
        else if (Hour<10){
            return "0"+Hour+":"+Minutes+":"+Sec+", "+Day+" Day(s)"+", "+Month+" Month(s)"+", "+Year+" Year(s)";
        }
        else
            return Hour+":"+Minutes+":"+Sec+", "+Day+" Day(s)"+", "+Month+" Month(s)"+", "+Year+" Year(s)";
    }
}
