package main;

import java.awt.geom.Point2D;

public class ConstantValues {
    // input files
    public static final String CSS_STYLE_FILE = "Style.css";
    public static final String PLAY_BUTTON_IMAGE_FILE = "start-button";
    public static final String PAUSE_BUTTON_IMAGE_FILE = "pause-button";
    public static final String FAST_FORWARD_BUTTON_IMAGE_FILE = "fast-forward-button-1";
    public static final String BACK_FORWARD_BUTTON_IMAGE_FILE = "back-forward-button-1";
    public static final String MAP_SHAPE_FILE = "maps/ne_110m_admin_0_countries.shp";

    public static final int POINT_RADIUS = 4;
    public static final double PRECISION = 1;

    public static double roundPrecision(double value, double precision) {
        return Math.round(value * precision) / precision;
    }

    public static Point2D createRoundedPoint(double x, double y, double precision) {
        return new Point2D.Double(
                ConstantValues.roundPrecision(x, precision),
                ConstantValues.roundPrecision(y, precision));
    }

    public static boolean doublePointsEqual(Point2D first, Point2D second, double precision) {
        Point2D roundedFirst = createRoundedPoint(first.getX(), first.getY(), precision);
        Point2D roundedSecond = createRoundedPoint(second.getX(), second.getY(), precision);

        return Double.compare(roundedFirst.getX(), roundedSecond.getX()) == 0
                && Double.compare(roundedFirst.getY(), roundedSecond.getY()) == 0;
    }
}
