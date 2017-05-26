package algorithm;

import com.sun.istack.internal.Nullable;
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

/**
 * Owner: Nikolay
 */

public class InfectionSpread {

    private static final double INFECTION_RADIUS = 2.0;
    private List<Disease> diseaseList;
    private Random random;
    private World world;
    private MapCanvas mapCanvas;

    public InfectionSpread(World world, MapCanvas mapCanvas) {
        diseaseList = new ArrayList<>();
        this.random = new Random();
        this.world = world;
        this.mapCanvas = mapCanvas;
    }

    private void addDisease() {
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

    private Disease getMainDisease() {
        return diseaseList.get(0);
    }

    public List<Disease> getDiseaseList() {
        return diseaseList;
    }

    public void applyAlgorithm(Disease disease) {
        if (this.diseaseList.isEmpty()) {
            this.addDisease();
        }
        // TODO: instead of all points, get the most recent in the queue for each country?
        for (java.awt.geom.Point2D infectionPoint : world.getAllInfectionPoints()) {
            boolean pointWillSpread = random.nextDouble() < getMainDisease().getProperties().getVirulence();
            if (!pointWillSpread) {
                continue;
            }

            Point2D newPoint = generateNewRandomPoint(infectionPoint);
            newPoint = findSuitablePlaceForPoint(newPoint);
            if (newPoint == null) {
                continue;
            }

            String countryCode = mapCanvas.getGeoFinder()
                    .getCountryNameCodeFromMapCoordinates(newPoint.getX(), newPoint.getY());

            if (countryCode.equals("water")) {
                continue;
            } else if (world.getCountryByCode(countryCode).isPresent()){
                Country country = world.getCountryByCode(countryCode).get();
                country.addInfectionPoint(newPoint);
            } else {
                // not found in our list of countries
                System.out.println("COUNTRY NOT FOUND: " + countryCode);
            }
        }
    }

    /**
     * Given a source point, method creates another point near it with random coordinates.
     */
    private Point2D generateNewRandomPoint(Point2D infectionPoint) {
        double offsetX = random.nextDouble() * INFECTION_RADIUS + INFECTION_RADIUS;
        double offsetY = random.nextDouble() * INFECTION_RADIUS + INFECTION_RADIUS;
        double newPointX = infectionPoint.getX() +
                (random.nextBoolean() ? +offsetX : -offsetX);
        double newPointY = infectionPoint.getY() +
                (random.nextBoolean() ? offsetY : -offsetY);

        newPointX = wrapAroundHorizontally(newPointX);
        return new Point2D.Double(newPointX, newPointY);
    }

    /**
     * Calculate the new horizontal coordinate of a point if it needs to wrap around.
     * Example: Russia's Chukotka
     */
    private double wrapAroundHorizontally(final double newPointX) {
        if (newPointX > 180) {
            return -(newPointX - 1);
        } else if (newPointX < -180) {
            return -(newPointX + 1);
        }
        return newPointX;
    }

    /**
     * Method tries to find an unoccupied place for a point. If the number of tries is
     * exhausted then null is returned.
     *
     * Points which round up to different values may have a visual overlap because of the
     * circle radius.
     */
    @Nullable
    private Point2D findSuitablePlaceForPoint(Point2D point) {
        double OFFSET = INFECTION_RADIUS / 5;
        int triesLeft = 5;
        while (world.containsInfectionPoint(point)) {
            if (triesLeft-- < 0) {
                return null;
            }
            double newRoundedX = (random.nextBoolean() ? OFFSET : -OFFSET);
            double newRoundedY = (random.nextBoolean() ? OFFSET : -OFFSET);
            point.setLocation(newRoundedX, newRoundedY);
        }
        return point;
    }


}
