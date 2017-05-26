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
}
