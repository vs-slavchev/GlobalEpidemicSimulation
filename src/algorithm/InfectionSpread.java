package algorithm;

/**
 * Created by Kaloyan on 4/3/2017.
 */

import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Country;
import main.Time;
import main.World;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import map.MapCanvas;
import reader.ConstantValues;

public class InfectionSpread {

    private List<Disease> diseaseList;
    private Random random;
    private World world;
    private static final double INFECTION_RADIUS = 2.0;
    private MapCanvas mapCanvas;
    private List<String> points;
    private File file = null;
    private String timeLog;
    private File logFile;
    private Boolean isSavedAs = false;

    public InfectionSpread(Random random, World world, MapCanvas mapCanvas) {
        diseaseList = new ArrayList<>();
        points = new ArrayList<>();
        this.random = random;
        this.world = world;
        this.mapCanvas = mapCanvas;
    }

    public void infectCountry(Country country)
    {

    }
    public List<String> getPoints(){
        return points;
    }
    public void addDisease()
    {
        diseaseList.add(new Disease("ebola", DiseaseType.BACTERIA,
                new DiseaseProperties(10, 10,
                        10, 0.6)));
    }
    public void addDisease(String name,int diseaseType,int lethality,int prefTemp,int tempTolerance,double virulence){
        diseaseList.add(new Disease(name, DiseaseType.values()[diseaseType-1],
                new DiseaseProperties(lethality, prefTemp,
                        tempTolerance, virulence)));
    }
    public void addDisease(Disease disease){
        diseaseList.add(disease);
    }

    public void removeDisease(Disease disease){
        diseaseList.remove(disease);
    }

    public Disease getMainDisease()
    {
        return diseaseList.get(0);
    }

    public void multiplyCountry(Country country)
    {

    }

    public List<Disease> getDiseaseList()
    {

        return diseaseList;
    }
    public void saveInfectionSpread(Stage stage, Time time){
        BufferedWriter writer = null;
        if(file!=null){
        try {
            //create a temporary file
            if(!isSavedAs){
            timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            logFile = new File(timeLog+".txt");}
            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(file));
            for (String point: getPoints()
                 ) {
                writer.write(point);
                writer.newLine();
            }
            writer.write(".,");
            writer.write(time.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
        }
        else {
            saveAsInfectionSpread(stage, time);
        }
    }
    public void saveAsInfectionSpread(Stage stage,Time time){
        BufferedWriter writer = null;
        try {
            //create a temporary file
            timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            logFile = new File(timeLog+".txt");
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(logFile.toString());
            File filedir = new File(System.getProperty("user.dir"));
            fileChooser.setInitialDirectory(filedir);
            file = fileChooser.showSaveDialog(stage);
            // This will output the full path where the file will be written to...

            writer = new BufferedWriter(new FileWriter(file));
            for (String point: getPoints()
                    ) {
                writer.write(point);
                writer.newLine();
            }
            writer.write(".,");
            writer.write(time.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                isSavedAs = true;
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }
    public void openInfectionSpread(Stage stage,Time time){
        final FileChooser fileChooser = new FileChooser();
        File filedir = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(filedir);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String line = "";
            String SplitBy = ",";
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] tempFileData = line.split(SplitBy);
                    String pointX = "";
                    String pointY = "";
                        if(tempFileData[0].startsWith(".")){
                            time.setTime(Integer.parseInt(tempFileData[1]),Integer.parseInt(tempFileData[2]),
                                    Integer.parseInt(tempFileData[3]),Integer.parseInt(tempFileData[4]),
                                    Integer.parseInt(tempFileData[5]),Integer.parseInt(tempFileData[6]));

                        }
                        else{
                            pointX = tempFileData[0];
                            pointY = tempFileData[1];
                            if (world.getCountry("Bulgaria").isPresent()) {
                                Country country = world.getCountry("Bulgaria").get();
                                country.addInfectionPoint(
                                        new java.awt.geom.Point2D.Double(Double.parseDouble(pointX), Double.parseDouble(pointY)));
                            }
                            mapCanvas.updateInfectionPointsCoordinates(world.getAllInfectionPoints());
                        }

//                    java.awt.geom.Point2D screenNewPoint = mapCanvas.getGeoFinder()
//                            .mapToScreenCoordinates(Double.parseDouble(pointX), Double.parseDouble(pointY));

                }
            } catch (IOException e) {
            }
        }
    }

    public void applyAlgorithm()
    {
        if (this.diseaseList.isEmpty()){
            this.addDisease();
        }
        for (java.awt.geom.Point2D infectionPoint : world.getAllInfectionPoints()) {
            if (random.nextDouble() < this
                    .getMainDisease()
                    .getProperties()
                    .getVirulence()) {
                double offsetX = random.nextDouble() * INFECTION_RADIUS + INFECTION_RADIUS;
                double offsetY = random.nextDouble() * INFECTION_RADIUS + INFECTION_RADIUS;
                double newPointX = infectionPoint.getX() +
                        (random.nextBoolean() ? +offsetX : -offsetX);
                double newPointY = infectionPoint.getY() +
                        (random.nextBoolean() ? offsetY : -offsetY);
                String conc = "" + String.format("%.0f", newPointX) +","+ String.format("%.0f", newPointY);

                while (points.contains(conc)) {
                    newPointX = random.nextBoolean() ? +offsetX / 5 : -offsetX / 5;

                    newPointY = random.nextBoolean() ? offsetY / 5 : -offsetY / 5;
                    conc = "" + String.format("%.0f", newPointX) + String.format("%.0f", newPointY);
                }

                java.awt.geom.Point2D screenNewPoint = mapCanvas.getGeoFinder()
                        .mapToScreenCoordinates(newPointX, newPointY);

                String countryName = mapCanvas.getGeoFinder().getCountryNameFromScreenCoordinates(
                        screenNewPoint.getX(), screenNewPoint.getY());
                if (countryName.equals("water")) {
                    continue;
                }

                if (world.getCountry("Bulgaria").isPresent()) {
                    Country country = world.getCountry("Bulgaria").get();
                    country.addInfectionPoint(
                            new java.awt.geom.Point2D.Double(newPointX, newPointY));
                }
                points.add(conc);
            }
        }
    }
}
