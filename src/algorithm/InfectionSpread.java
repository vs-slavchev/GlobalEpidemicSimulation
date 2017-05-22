package algorithm;

/**
 * Owner: Nikolay
 */

import disease.Disease;
import disease.DiseaseProperties;
import disease.DiseaseType;
import main.Country;
import main.World;
import map.MapCanvas;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static main.ConstantValues.PRECISION;

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

    public void addDisease(String name, int diseaseType, int lethality,
                           int prefTemp, int tempTolerance,double virulence) {
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

                Point2D roundedPoint = findSuitablePlaceForPoint(newPointX, newPointY);
                if (roundedPoint == null) {
                    continue;
                }

                String countryName = mapCanvas.getGeoFinder()
                        .getCountryNameFromMapCoordinates(roundedPoint.getX(), roundedPoint.getY());

                if (countryName.equals("water")) {
                    continue;
                } else if (world.getCountry("Bulgaria").isPresent()) {
                    Country country = world.getCountry("Bulgaria").get();
                    //double tweakedX = roundedPoint.getX() + (random.nextDouble() - 0.5) / 1;
                    //double tweakedY = roundedPoint.getY() + (random.nextDouble() - 0.5) / 1;
                    country.addInfectionPoint(roundedPoint);
                }
            }
        }
    }

    private Point2D findSuitablePlaceForPoint(double newPointX, double newPointY) {

        Point2D point = new Point2D.Double(newPointX, newPointY);
        int divider = 10;
        int triesLeft = 5;
        while (world.containsInfectionPoint(point, PRECISION)) {
            if (triesLeft-- < 0) {
                return null;
            }
            double newRoundedX = (random.nextBoolean() ? INFECTION_RADIUS / divider : -INFECTION_RADIUS / divider);
            double newRoundedY = (random.nextBoolean() ? INFECTION_RADIUS / divider : -INFECTION_RADIUS / divider);
            point.setLocation(newRoundedX, newRoundedY);
        }
        return point;
    }


}
