package world;

import java.awt.geom.Point2D;

public class Flight {
    private final double singleStep;
    private Point2D departure;
    private Point2D destination;
    private Point2D bezier;
    private volatile double currentX;
    private volatile double currentY;
    private double progress;

    public Flight(Point2D departure, Point2D destination) {
        this.departure = departure;
        this.destination = destination;
        bezier = new Point2D.Double(
                20 + Math.min(
                        departure.getX(), destination.getX()) + Math.abs(departure.getX() - destination.getX()) / 4,
                Math.min(
                        departure.getY(), destination.getY()) + Math.abs(departure.getY() - destination.getY()) / 4);

        // the single step is proportional to the distance to travel
        singleStep = 1.0 / (Math.sqrt(Math.pow(Math.abs(departure.getX() - destination.getX()), 2)
                + Math.pow(Math.abs(departure.getY() - destination.getY()), 2)) * 100);

        // take off
        updateCurrentLocation(1);
    }

    /**
     * Move forward based on the simulation time speed.
     */
    public void updateCurrentLocation(final int timeSpeed) {
        progress = Math.min(progress + singleStep * timeSpeed, 1.0);
        Point2D current = step(progress);
        currentX = current.getX();
        currentY = current.getY();
    }

    /**
     * Calculate the position after making a step in a bezier curve manner.
     */
    public Point2D.Double step(final double currentProgress) {
        double x = (1 - currentProgress) * (1 - currentProgress) * departure.getX()
                + 2 * (1 - currentProgress) * currentProgress * bezier.getX()
                + currentProgress * currentProgress * destination.getX();
        double y = (1 - currentProgress) * (1 - currentProgress) * departure.getY()
                + 2 * (1 - currentProgress) * currentProgress * bezier.getY()
                + currentProgress * currentProgress * destination.getY();
        return new Point2D.Double(x, y);
    }

    public Point2D getCurrentLocation() {
        return new Point2D.Double(currentX, currentY);
    }

    public double getSingleStep() {
        return singleStep;
    }

    public boolean isLanded() {
        return progress >= 1.0;
    }

    public double getProgress() {
        return progress;
    }
}
