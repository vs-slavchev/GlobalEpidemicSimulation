package map;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import main.ConstantValues;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.jfree.fx.FXGraphics2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Owner: Veselin
 * <p>
 * Initializes and draws the map. Has event handlers strictly related to the map only.
 */

public class MapCanvas {

    private static final double FPS = 60.0;
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
    private Canvas canvas;
    private MapContent map;
    private GraphicsContext graphics;
    private ArrayList<java.awt.geom.Point2D> infectionPoints;
    private GeoFinder geoFinder;
    private StyleManager styleManager;
    private boolean needsRepaint = true;
    private double dragDistanceX;
    private double dragDistanceY;

    private ArrayList<Integer> percentageInfected;

    public MapCanvas(int width, int height) {
        canvas = new Canvas(width, height);
        geoFinder = new GeoFinder(width, height);
        styleManager = new StyleManager(geoFinder.getFeatureSource());
        graphics = canvas.getGraphicsContext2D();
        infectionPoints = new ArrayList<>();
        initMap();
        initializeEventHandling();
        initPaintThread();

        percentageInfected = new ArrayList<>(100);
        percentageInfected.add(0);
    }

    public Node getCanvas() {
        return canvas;
    }

    private void initMap() {
        map = new MapContent();
        Style style = styleManager.createDefaultStyle();
        FeatureLayer layer = new FeatureLayer(geoFinder.getFeatureSource(), style);
        map.addLayer(layer);
        map.getViewport().setScreenArea(new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight()));
    }

    private synchronized void drawMap(GraphicsContext gc) {
        if (!needsRepaint) {
            return;
        }
        needsRepaint = false;
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(map);
        FXGraphics2D graphics = new FXGraphics2D(gc);
        graphics.setBackground(java.awt.Color.BLUE);
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
        Rectangle rectangle = new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight());
        draw.paint(graphics, rectangle, map.getViewport().getBounds());

        gc.setFill(Color.rgb(255, 0, 0, 0.4));
        for (Point2D point : infectionPoints) {
            Point2D screenInfectionPoint = geoFinder.mapToScreenCoordinates(point.getX(), point.getY());
            gc.fillOval(screenInfectionPoint.getX(), screenInfectionPoint.getY(),
                    ConstantValues.POINT_RADIUS, ConstantValues.POINT_RADIUS);
        }
    }

    private void drawGraph(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.setFill(Color.RED);
        gc.setFont(new Font(17));
        gc.fillText("time", 150, canvas.getHeight() - 85);
        gc.fillText("%", 30, canvas.getHeight() - 200);
        gc.strokeLine(50, canvas.getHeight() - 100, 250, canvas.getHeight() - 100);
        gc.strokeLine(50, canvas.getHeight() - 100, 50, canvas.getHeight() - 300);
        for (int firstOfPair_i = 0; firstOfPair_i < percentageInfected.size() - 1; firstOfPair_i++) {
            int firstOfPair = percentageInfected.get(firstOfPair_i);
            int secondOfPair = percentageInfected.get(firstOfPair_i + 1);

            //TODO restrict to last 100 hours only, use queue?
            gc.strokeLine(
                    (firstOfPair_i/(double)percentageInfected.size()*100.0) * 2 + 50,
                    canvas.getHeight() - 100 - firstOfPair * 2,
                    ((firstOfPair_i + 1)/(double)percentageInfected.size()*100.0) * 2 + 50,
                    canvas.getHeight() - 100 - secondOfPair * 2);
        }
    }

    public void pushNewPercentageValue(int element) {
        if (percentageInfected.size() >= 100) {
            percentageInfected.set(0, element);
            Collections.rotate(percentageInfected, -1);
        } else {
            percentageInfected.add(element);
        }
        needsRepaint = true;
    }

    public void setNeedsRepaint() {
        this.needsRepaint = true;
    }

    private void initializeEventHandling() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            dragDistanceX = e.getSceneX();
            dragDistanceY = e.getSceneY();

            e.consume();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double difX = e.getSceneX() - dragDistanceX;
            double difY = e.getSceneY() - dragDistanceY;
            dragDistanceX = e.getSceneX();
            dragDistanceY = e.getSceneY();
            DirectPosition2D newPos = new DirectPosition2D(difX, difY);
            DirectPosition2D result = new DirectPosition2D();
            map.getViewport().getScreenToWorld().transform(newPos, result);
            ReferencedEnvelope env = new ReferencedEnvelope(map.getViewport().getBounds());
            env.translate(env.getMinimum(0) - result.x,
                    env.getMaximum(1) - result.y);

            if (env.getMinimum(0) >= -180
                    && env.getMaximum(0) <= 180
                    && env.getMinimum(1) >= -90
                    && env.getMaximum(1) <= 90) {
                geoFinder.setPanOffsetX(env.getMaximum(0));
                geoFinder.setPanOffsetY(env.getMaximum(1));
                setViewport(env);
            }
            e.consume();

        });

        canvas.addEventHandler(ScrollEvent.SCROLL, e -> {
            ReferencedEnvelope envelope = map.getViewport().getBounds();
            double percent = -e.getDeltaY() / canvas.getWidth();
            double width = envelope.getWidth();
            double height = envelope.getHeight();
            double deltaW = width * percent;
            double deltaH = height * percent;
            envelope.expandBy(deltaW, deltaH);

            if (envelope.getWidth() <= 360 && envelope.getHeight() <= 180) {
                geoFinder.setMapWidth(envelope.getWidth());
                geoFinder.setMapHeight(envelope.getHeight());

                geoFinder.setPanOffsetX(envelope.getMaximum(0));
                geoFinder.setPanOffsetY(envelope.getMaximum(1));

                setViewport(envelope);
            }
            e.consume();
        });
    }

    private void initPaintThread() {
        ScheduledService<Boolean> svc = new ScheduledService<Boolean>() {
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    protected Boolean call() {
                        Platform.runLater(() -> {
                            drawMap(graphics);
                            drawGraph(graphics);
                        });
                        return true;
                    }
                };
            }
        };
        svc.setPeriod(Duration.millis(1000.0 / FPS));
        svc.start();
    }

    private void setViewport(ReferencedEnvelope envelope) {
        map.getViewport().setBounds(envelope);
        needsRepaint = true;
    }

    /**
     * Changes the style of selected (if any) countries. Takes in screen coordinates.
     */
    public void selectStyleChange(double x, double y) {
        SimpleFeatureCollection features = geoFinder.getCountryFeaturesCollectionFromScreenCoordinates(x, y);

        Set<FeatureId> IDs = new HashSet<>();
        try (SimpleFeatureIterator iterator = features.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                IDs.add(feature.getIdentifier());
            }
        }
        displaySelectedFeatures(IDs);
    }

    private void displaySelectedFeatures(Set<FeatureId> IDs) {
        Style style = IDs.isEmpty() ?
                styleManager.createDefaultStyle() : styleManager.createSelectedStyle(IDs);

        Layer layer = map.layers().get(0);
        ((FeatureLayer) layer).setStyle(style);
    }


    public GeoFinder getGeoFinder() {
        return geoFinder;
    }

    public synchronized void updateInfectionPointsCoordinates(ArrayList<Point2D> infectionPoints) {
        this.infectionPoints = infectionPoints;
        setNeedsRepaint();
    }
}
