package reader;

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

    public static double roundPrecision(double value, double precisionCoefficient) {
        return Math.round(value * precisionCoefficient) / precisionCoefficient;
    }

    public static boolean doublePointsEqual(Point2D first, Point2D second) {
        return Double.compare(first.getX(), second.getX()) == 0
                && Double.compare(first.getY(), second.getY()) == 0;
    }
}
