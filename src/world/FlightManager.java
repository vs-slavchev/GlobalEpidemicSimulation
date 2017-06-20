package world;

import main.ConstantValues;
import map.MapCanvas;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlightManager {
    private List<Flight> flights;
    private World world;
    private MapCanvas mapCanvas;

    public FlightManager(MapCanvas mapCanvas, World world) {
        this.mapCanvas = mapCanvas;
        this.world = world;
        flights = new CopyOnWriteArrayList<>();
    }

    /**
     * Creates a flight between 2 random airports.
     */
    public void createRandomFlight() {
        Point2D departure = ConstantValues.getRandomAirportCoordinates();
        Point2D destination = ConstantValues.getRandomAirportCoordinates();
        while (destination.equals(departure)) {
            destination = ConstantValues.getRandomAirportCoordinates();
        }
        flights.add(new Flight(departure, destination, mapCanvas, world));
    }

    public void updateFlights(int timeSpeed) {
        for (Flight flight : flights) {
            flight.updateCurrentLocation(timeSpeed);
        }
        flights.removeIf(Flight::isLanded);
    }

    public List<Flight> getFlights() {
        return flights;
    }
}
