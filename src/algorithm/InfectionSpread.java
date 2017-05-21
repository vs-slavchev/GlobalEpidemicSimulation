package algorithm;

/**
 * Created by Kaloyan on 4/3/2017.
 */

import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import main.Country;
import main.World;
import map.MapCanvas;
import reader.ConstantValues;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InfectionSpread {

    private static final double INFECTION_RADIUS = 2.0;
    private List<Disease> diseaseList;
    private Random random;
    private World world;
    private MapCanvas mapCanvas;

    public InfectionSpread(Random random, World world, MapCanvas mapCanvas) {
        diseaseList = new ArrayList<>();
        this.random = random;
        this.world = world;
        this.mapCanvas = mapCanvas;
    }

    public void addDisease() {
        diseaseList.add(new Disease("ebola", DiseaseType.BACTERIA,
                new DiseaseProperties(10, 10,
                        10, 0.6)));
    }

    public void addDisease(String name, int diseaseType, int lethality, int prefTemp, int tempTolerance, double virulence) {
        diseaseList.add(new Disease(name, DiseaseType.values()[diseaseType - 1],
                new DiseaseProperties(lethality, prefTemp,
                        tempTolerance, virulence)));
    }

    public void addDisease(Disease disease) {
        diseaseList.add(disease);
    }

    public Disease getMainDisease() {
        return diseaseList.get(0);
    }

    public List<Disease> getDiseaseList() {
        return diseaseList;
    }

    public void applyAlgorithm(Disease disease) {
        if (this.diseaseList.isEmpty()) {
            this.addDisease();
        }
        // TODO: instead of all points, get the most recent in the queue for each country
        for (java.awt.geom.Point2D infectionPoint : world.getAllInfectionPoints()) {
            if (random.nextDouble() < getMainDisease().getProperties().getVirulence()) {
                double offsetX = random.nextDouble() * INFECTION_RADIUS + INFECTION_RADIUS;
                double offsetY = random.nextDouble() * INFECTION_RADIUS + INFECTION_RADIUS;
                double newPointX = infectionPoint.getX() +
                        (random.nextBoolean() ? +offsetX : -offsetX);
                double newPointY = infectionPoint.getY() +
                        (random.nextBoolean() ? offsetY : -offsetY);

                if (newPointX > 180) {
                    newPointX = -(newPointX - 1);
                } else if (newPointX < -180) {
                    newPointX = -(newPointX + 1);
                }

                newPointX = ConstantValues.roundPrecision(newPointX, 2);
                newPointY = ConstantValues.roundPrecision(newPointY, 2);
                Point2D newPoint = new Point2D.Double(newPointX, newPointY);

                while (world.containsInfectionPoint(newPoint)) {
                    System.out.println("A: " + newPoint);
                    double newLocationOfX = newPoint.getX() + (random.nextBoolean() ? offsetX / 5 : -offsetX / 5);
                    double newLocationOfY = newPoint.getY() + (random.nextBoolean() ? offsetY / 5 : -offsetY / 5);
                    newPoint.setLocation(
                            ConstantValues.roundPrecision(newLocationOfX, 2),
                            ConstantValues.roundPrecision(newLocationOfY, 2));
                    System.out.println("B: " + newPoint);
                }

                Point2D screenNewPoint = mapCanvas.getGeoFinder().mapToScreenCoordinates(
                        newPoint.getX(), newPoint.getY());

                // TODO: create method to get country name from map coordinates
                String countryName = mapCanvas.getGeoFinder()
                        .getCountryNameFromScreenCoordinates(screenNewPoint.getX(), screenNewPoint.getY());

                if (countryName.equals("water")) {
                    continue;
                }

                if (world.getCountry("Bulgaria").isPresent()) {
                    Country country = world.getCountry("Bulgaria").get();
                    country.addInfectionPoint(
                            new Point2D.Double(newPoint.getX(), newPoint.getY()));
                }
            }
        }
    }
}
