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
import main.Country;
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
    private Country selectedCountry;

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

    private void initPaintThread() {
        ScheduledService<Boolean> svc = new ScheduledService<Boolean>() {
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    protected Boolean call() {
                        Platform.runLater(() -> {
                            if (!needsRepaint) {
                                return;
                            }
                            needsRepaint = false;
                            drawMap(graphics);
                            drawGraph(graphics);
                            drawSelectedCountryInformation(graphics);
                        });
                        return true;
                    }
                };
            }
        };
        svc.setPeriod(Duration.millis(1000.0 / ConstantValues.FPS));
        svc.start();
    }

    private synchronized void drawMap(GraphicsContext gc) {
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
        // draw the base
        gc.setStroke(Color.rgb(220, 20, 60, 1));
        gc.setLineWidth(3);
        gc.setFill(Color.rgb(178, 34, 34, 1));
        gc.setFont(new Font(25));
        gc.fillText("time", 300, canvas.getHeight() - 125);
        gc.fillText("%", 20, canvas.getHeight() - 380);
        gc.strokeLine(50, canvas.getHeight() - 150, 350, canvas.getHeight() - 150);
        gc.strokeLine(50, canvas.getHeight() - 150, 50, canvas.getHeight() - 400);

        // draw the lines
        gc.setStroke(Color.rgb(240, 128, 128, 1));
        for (int firstOfPair_i = 0; firstOfPair_i < percentageInfected.size() - 1; firstOfPair_i++) {
            int firstOfPair = percentageInfected.get(firstOfPair_i);
            int secondOfPair = percentageInfected.get(firstOfPair_i + 1);

            gc.strokeLine(
                    (firstOfPair_i/(double)percentageInfected.size()*100.0) * 3 + 50,
                    canvas.getHeight() - 150 - firstOfPair * 2.5,
                    ((firstOfPair_i + 1)/(double)percentageInfected.size()*100.0) * 3 + 50,
                    canvas.getHeight() - 150 - secondOfPair * 2.5);
        }
    }

    private void drawSelectedCountryInformation(GraphicsContext gc) {
        if (selectedCountry == null) { return; }

        int baseX = (int) (canvas.getWidth() - 400);
        int baseY = (int) (canvas.getHeight() - 380);

        gc.setFill(Color.rgb(0, 200, 200, 0.8));
        gc.fillRect(baseX, baseY, 350, 280);

        gc.setFill(Color.rgb(30, 30, 200, 1));
        gc.setFont(new Font(15));

        String[] labelLines = {
                "Name:",
                "Code:",
                "Population:",
                "Population density:",
                "Medical infrastructure:",
                "Average yearly temperature:"
        };

        String[] contentLines = {
                selectedCountry.getName(),
                selectedCountry.getCode(),
                String.format("%,d", selectedCountry.getTotalPopulation()),
                String.valueOf(selectedCountry.getEnvironment().getPopulationDensity()),
                String.valueOf(selectedCountry.getEnvironment().getMedicalInfrastructure()),
                String.valueOf(selectedCountry.getEnvironment().getAvgYearlyTemp())
        };

        for (int line_i = 0; line_i < labelLines.length; line_i++) {
            gc.fillText(labelLines[line_i], baseX + 20, baseY + 40 * (line_i + 1));
        }

        gc.setFill(Color.rgb(100, 30, 120, 1));
        for (int line_i = 0; line_i < labelLines.length; line_i++) {
            gc.fillText(contentLines[line_i], baseX + 50, baseY + 17.5 + 40 * (line_i + 1));
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

            
            if ((env.getMinimum(0) >= -180 && difX > 0)
                    || (env.getMaximum(0) <= 180 && difX < 0)) {
                env.translate(env.getMinimum(0) - result.x, 0);
            }
            if ((env.getMinimum(1) >= -90 && difY < 0)
                    || (env.getMaximum(1) <= 90 && difY > 0)) {
                env.translate(0, env.getMaximum(1) - result.y);
            }

            geoFinder.setPanOffsetX(env.getMaximum(0));
            geoFinder.setPanOffsetY(env.getMaximum(1));
            setViewport(env);
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
        setMapStyle(style);
    }

    /**
     * Deselect any selected countries.
     */
    public void deselectStyle() {
        setMapStyle(styleManager.createDefaultStyle());
        selectedCountry = null;
        needsRepaint = true;
    }

    private void setMapStyle(Style style) {
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

    public void selectCountry(double x, double y, Country country) {
        if (country.equals(selectedCountry)) {
            deselectStyle();
            return;
        }
        selectStyleChange(x, y);
        setNeedsRepaint();
        selectedCountry = country;
    }
}
