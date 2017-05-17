package algorithm;

/**
 * Created by Kaloyan on 4/3/2017.
 */

import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import main.Country;
import main.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import map.MapCanvas;
import reader.ConstantValues;

public class InfectionSpread {

    private List<Disease> diseaseList;
    private Random random;
    private World world;
    private static final double INFECTION_RADIUS = 2.0;
    private MapCanvas mapCanvas;
    private List<String> points;

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
                String conc = "" + String.format("%.0f", newPointX) + String.format("%.0f", newPointY);

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
