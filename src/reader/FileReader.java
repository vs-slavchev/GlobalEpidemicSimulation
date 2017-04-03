package reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class FileReader {
    private List<String> Countries = new ArrayList<>();
    private List<String> Population = new ArrayList<>();
    private Scanner x;

    public void openFile() {
        try {
            x = new Scanner(new File("maps/pop.txt"));
            System.out.printf("File was loaded!");
        } catch (Exception e) {
            System.out.println("could not find file");
        }
    }

    public void readFile() {
        String country;
        String population;
        try {
            while (x.hasNext()) {
                country = x.next();
                population = x.next();
                Countries.add(country);
                Population.add(population);
                System.out.printf("%s %s%n", country, population);
            }
        } catch (Exception e) {
            System.out.println("could not read file");
        }
    }

    public void closeFile() {
        x.close();
        System.out.printf("File closed!");
    }

}