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

            addInfectionPointToCountryAtMapCoordinates(newPoint);
        }
    }

    public void airplaneAlgorithm(){
        Point2D newPoint = getAirportPoint();
        addInfectionPointToCountryAtMapCoordinates(newPoint);
    }

    /**
     * Tries to add an infection point to the country which is at the input coordinates.
     * @param newMapPoint A point in map coordinates where an infection point should be added.
     */
    public void addInfectionPointToCountryAtMapCoordinates(Point2D newMapPoint) {
        String countryCode = mapCanvas.getGeoFinder()
                .getCountryCodeFromMapCoordinates(newMapPoint.getX(), newMapPoint.getY());

        if (world.getCountryByCode(countryCode).isPresent()){
            Country country = world.getCountryByCode(countryCode).get();
            country.addInfectionPoint(newMapPoint);
        }
    }

    /**
     * Given a source point, method creates another point near it with random coordinates.
     * @param infectionPoint the source point to spawn from
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

    private Point2D getAirportPoint() {
        int airport = random.nextInt(17);
        double x = 0;
        double y = 0;

        switch (airport) {
            case 0:
                // Hartsfield–Jackson Atlanta International Airport
                x =-83.6951559140206;
                y =33.030313668662004;
            break;
            case 1:
                // Beijing Capital International Airport
                x =115.69108481501507;
                y =40.74449381999329;
            break;
            case 2:
                // Dubai International Airport
                x =55.286382319436555;
                y =24.72033059971252;
                break;
            case 3:
                // Heathrow Airport
                x =-1.1395673171517586;
                y =52.01777447747722;
                break;
            case 4:
                // Sofiq Atatürk Airport
                x =23.46751896814726;
                y =42.496142938751554;
                break;
            case 5:
                // Sydney Airport
                x =149.8125;
                y =-33.67932692307694;
                break;
            case 6:
                // South Africa  Airport
                x =28.471338077947024;
                y =-25.281203954231955;
                break;
            case 7:
                // Brasilia  Airport
                x =-50.27569963763071;
                y =-14.786563409065337;
                break;
            case 8:
                // Santiago  Airport
                x =-70.98920615692101;
                y =-34.02673774187082;
                break;
            case 9:
                // Nigeria  Airport
                x =6.865008001790812;
                y =9.223902080634062;
                break;
            case 10:
                // Tokyo  Airport
                x =139.47190152280348;
                y =35.50610053738791;
                break;
            case 11:
                // Moscow  Airport
                x =42.1875;
                y =56.74086538461536;
                break;
            case 12:
                // Madagascar  Airport
                x =46.3125;
                y =-18.33125;
                break;
            case 13:
                // India  Airport
                x =76.6875;
                y =14.033173076923063;
                break;
            case 14:
                // Indonesia   Airport
                x =113.4375;
                y =0.019711538461521627;
                break;
            case 15:
                // New Guinea  Airport
                x =141.375;
                y =-4.484615384615395;
                break;
            case 16:
                // New Zealand  Airport
                x =175.125;
                y =-38.51730769230771;
                break;

        }
        return new Point2D.Double(x,y);
    }
}
