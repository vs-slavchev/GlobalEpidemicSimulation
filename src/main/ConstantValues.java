package main;

import java.awt.geom.Point2D;

public class ConstantValues {
    public static final int FPS = 30;

    // input files
    public static final String CSS_STYLE_FILE = "Style.css";
    public static final String PLAY_BUTTON_IMAGE_FILE = "start-button";
    public static final String PAUSE_BUTTON_IMAGE_FILE = "pause-button";
    public static final String FAST_FORWARD_BUTTON_IMAGE_FILE = "fast-forward-button-1";
    public static final String BACK_FORWARD_BUTTON_IMAGE_FILE = "back-forward-button-1";
    public static final String MAP_SHAPE_FILE = "maps/ne_110m_admin_0_countries.shp";

    public static final int POINT_RADIUS = 4;

    private static Point2D createRoundedPoint(double x, double y) {
        return new Point2D.Double(
                Math.round(x),
                Math.round(y));
    }

    public static boolean doublePointsEqual(Point2D first, Point2D second) {
        Point2D roundedFirst = createRoundedPoint(first.getX(), first.getY());
        Point2D roundedSecond = createRoundedPoint(second.getX(), second.getY());

        return Double.compare(roundedFirst.getX(), roundedSecond.getX()) == 0
                && Double.compare(roundedFirst.getY(), roundedSecond.getY()) == 0;
    }


    public static final Point2D.Double[] AIRPORTS = {
            new Point2D.Double(-83.6951559140206, 33.030313668662004), // Hartsfield–Jackson Atlanta International
            new Point2D.Double(115.69108481501507, 40.74449381999329), // Beijing Capital International
            new Point2D.Double(55.286382319436555, 24.72033059971252), // Dubai International
            new Point2D.Double(-1.1395673171517586, 52.01777447747722), // Heathrow
            new Point2D.Double(23.46751896814726, 42.496142938751554), // Sofia
            new Point2D.Double(149.8125, -33.67932692307694), // Sydney
            new Point2D.Double(28.471338077947024, -25.281203954231955), // South Africa
            new Point2D.Double(-50.27569963763071, -14.786563409065337), // Brasilia
            new Point2D.Double(-70.98920615692101, -34.02673774187082), // Santiago
            new Point2D.Double(6.865008001790812, 9.223902080634062), // Nigeria
            new Point2D.Double(139.47190152280348, 35.50610053738791), // Tokyo
            new Point2D.Double(42.1875, 56.74086538461536), // Moscow
            new Point2D.Double(46.3125, -18.33125), // Madagascar
            new Point2D.Double(76.6875, 14.033173076923063), // India
            new Point2D.Double(113.4375, 0.019711538461521627), // Indonesia
            new Point2D.Double(141.375, -4.484615384615395), // New Guinea
            new Point2D.Double(175.125, -38.51730769230771), // New Zealand
    };
}
