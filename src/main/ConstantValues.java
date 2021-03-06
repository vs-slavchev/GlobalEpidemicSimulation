package main;

import javafx.scene.paint.Paint;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * Owner: Ivaylo
 */

public class ConstantValues {
    public static final int FPS = 30;
    public static final double CHANCE_TO_START_FLIGHT = 0.001;

    // input files
    public static final String CSS_STYLE_FILE = "Style.css";
    public static final String PLAY_BUTTON_IMAGE_FILE = "start-button";
    public static final String PAUSE_BUTTON_IMAGE_FILE = "pause-button";
    public static final String FAST_FORWARD_BUTTON_IMAGE_FILE = "fast-forward-button-1";
    public static final String BACK_FORWARD_BUTTON_IMAGE_FILE = "back-forward-button-1";
    public static final String MAP_SHAPE_FILE = "maps/ne_110m_admin_0_countries.shp";

    // graph
    public static final int LEFT_SIDE = 50;
    public static final int GRAPH_WIDTH = 300;
    public static final int GRAPH_HEIGHT = 250;

    //theme 1 colors
    public static final Paint GRAPH_STROKE_COLOR1 = javafx.scene.paint.Color.rgb(220, 20, 60, 1);
    public static final Paint GRAPH_TEXT_COLOR1 = javafx.scene.paint.Color.rgb(25, 25, 112, 1);
    public static final Paint BOX_COLOR1 = javafx.scene.paint.Color.rgb(220, 20, 60, 0.9);
    public static final Paint BOX_TITLES_COLOR1 = javafx.scene.paint.Color.rgb(255, 255, 100, 1);
    public static final Paint BOX_INFO_COLOR1 = javafx.scene.paint.Color.rgb(200, 255, 255, 1);
    public static final Paint PLANE_LINE1 = javafx.scene.paint.Color.rgb(255, 255, 255, 0.05);
    public static final Color LINE_COLOR1 = new Color(0, 128, 128);
    public static final Color FILL_COLOR1 = new Color(154, 205, 50);
    public static final Color SEA_COLOR1 = new Color(30, 144, 255);
    public static final Paint POINTS_COLOR1 = javafx.scene.paint.Color.rgb(255, 255, 255, 1);
    public static final Color SELECTED_COUNTRY_COLOR1 = new Color(240, 255, 240);
    public static final Paint SELECTED_COUNTRY_POINTS_COLOR1 = javafx.scene.paint.Color.rgb(0, 128, 128, 0.8);
    public static final Paint GRAPH_LINE_COLOR1 = javafx.scene.paint.Color.rgb(0, 0, 255, 0.8);
    public static final Paint GRAPH_LINE_COLOR2 = javafx.scene.paint.Color.rgb(0, 255, 0, 0.8);

    private static final Point2D.Double[] AIRPORTS = {
            new Point2D.Double(-83.6951559140206, 33.030313668662004), // Hartsfield–Jackson Atlanta International
            new Point2D.Double(-79.630556, 43.676667), // Toronto Pearson
            new Point2D.Double(-122.375, 37.618889), // San Francisco
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
            new Point2D.Double(9.75, 61.07836538461537), // Norway
            new Point2D.Double(80.0625, 7.92253384615384), // Sri Lanka
            new Point2D.Double(120.5625, 15.534615384615364), // Philippines
            new Point2D.Double(-82.4122529, 22.991533),//José Martí International Airport, Havana, Cuba
            new Point2D.Double(-71.3643645, 19.3759666),//Cibao International Airport,Domenican Republic
            new Point2D.Double(-77.9269551, 18.5004657),//Sangster International Airport, Montego Bay, St. James Parish, Jamaica
            new Point2D.Double(-78.8579463, 26.5501849),//Grand Bahama International Airport, Freeport, The Bahamas
            new Point2D.Double(-21.9499937, 64.154837),//Airport Direct, Fiskislóð, Reykjavík, Iceland
            new Point2D.Double(-50.7140483, 67.0098926),//Kangerlussuaq Airport, Greenland
            new Point2D.Double(120.84851681744593, 23.35999201509931),//Taiwan
            new Point2D.Double(177.86808396925892, -17.943455920598176)//Fiji
    };

    public static Point2D getRandomAirportCoordinates() {
        return ConstantValues.AIRPORTS[new Random().nextInt(ConstantValues.AIRPORTS.length)];
    }
}
