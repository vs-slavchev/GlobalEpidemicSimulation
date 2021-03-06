package map;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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
import world.City;
import world.Country;
import world.Flight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

import static main.ConstantValues.*;

/**
 * Owner: Veselin
 * <p>
 * Initializes and draws the map. Has event handlers strictly related to the map only.
 */

public class MapCanvas {

    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
    private final int BOTTOM_SIDE;
    private Canvas canvas;
    private MapContent map;
    private GraphicsContext graphics;
    private List<Country> countries;
    private GeoFinder geoFinder;
    private StyleManager styleManager;
    private List<Flight> flights;
    private boolean needsRepaint = true;
    private double dragDistanceX;
    private double dragDistanceY;
    private ArrayList<Integer> percentageInfected;
    private ArrayList<Integer> percentageCured;
    private Country selectedCountry;

    public MapCanvas(int width, int height) {
        canvas = new Canvas(width, height);
        geoFinder = new GeoFinder(width, height);
        styleManager = new StyleManager(geoFinder.getFeatureSource());
        graphics = canvas.getGraphicsContext2D();
        initMap();
        initializeEventHandling();
        initPaintThread();

        BOTTOM_SIDE = (int) (canvas.getHeight() - 150);

        percentageInfected = new ArrayList<>(100);
        percentageInfected.add(0);
        percentageCured = new ArrayList<>(100);
        percentageCured.add(0);
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
                            //needsRepaint = false;
                            needsRepaint = true;
                            drawMap();
                            drawGraph();
                            drawFlights();
                            drawSelectedCountryInformation();
                        });
                        return true;
                    }
                };
            }
        };
        svc.setPeriod(Duration.millis(1000.0 / ConstantValues.FPS));
        svc.start();
    }

    private synchronized void drawMap() {
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(map);
        FXGraphics2D fxGraphics2D = new FXGraphics2D(graphics);
        fxGraphics2D.setBackground(ConstantValues.SEA_COLOR1);
        fxGraphics2D.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
        Rectangle rectangle = new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight());
        draw.paint(fxGraphics2D, rectangle, map.getViewport().getBounds());

        for (Country country : countries) {
            int toSubtractFromGreen = (int) Math.round(country.getPercentageOfInfectedPopulation() * 2.35);
            int toSubtractFromBlue = (int) Math.round(country.getPercentageOfInfectedPopulation() * 1.95);
            graphics.setFill(javafx.scene.paint.Color.rgb(
                    220, 255 - toSubtractFromGreen, 255 - toSubtractFromBlue, 1));
            drawCities(country.getCities());
        }
        if (selectedCountry != null) {
            //changes the color of the points in a selected country
            graphics.setFill(ConstantValues.SELECTED_COUNTRY_POINTS_COLOR1);
            drawCities(selectedCountry.getCities());
        }
    }

    private void drawCities(List<City> citiesToDraw) {
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.setFont(new Font(25));
        graphics.setStroke(javafx.scene.paint.Color.CYAN);
        for (City city : citiesToDraw) {
            Point2D screenCity = geoFinder.mapToScreenCoordinates(
                    city.getLatitude(), city.getLongitude());

            int radius = (int) calculatePointRadius(city.getPopulation());
            graphics.fillOval(screenCity.getX() - radius / 2, screenCity.getY() - radius / 2,
                    radius, radius);

            // draw extra circle for capitals
            if (city.isCapital()) {
                double circleWidth = geoFinder.createWorldToScreenAffineTransform().getScaleX() / 15;
                graphics.setLineWidth(circleWidth);
                radius = radius + (int) Math.ceil(circleWidth * 5);
                graphics.strokeOval(screenCity.getX() - radius / 2,
                        screenCity.getY() - radius / 2,
                        radius, radius);
            }

            if (geoFinder.createWorldToScreenAffineTransform().getScaleX() > 50) {
                graphics.fillText(city.getName(), screenCity.getX(),
                        screenCity.getY() + radius / 2 + 10);
            }
        }
        graphics.setTextAlign(TextAlignment.LEFT);
        graphics.setTextBaseline(VPos.BOTTOM);
    }

    /**
     * Calculates the radius of the city point regarding the scale of X
     * Scaling the points when zooming in/out
     */
    private double calculatePointRadius(double cityPopulation) {
        double scale = geoFinder.createWorldToScreenAffineTransform().getScaleX();
        return Math.max(0, 20 - cityPopulation / 1_000_000.0) / 8 + Math.sqrt(cityPopulation / 3_500_000.0) * scale;
    }

    private void drawGraph() {
        // draw the base
        graphics.setStroke(ConstantValues.GRAPH_STROKE_COLOR1);
        graphics.setLineWidth(3);
        graphics.setFill(ConstantValues.GRAPH_TEXT_COLOR1);
        graphics.setFont(new Font(25));
        graphics.fillText("time", 300, canvas.getHeight() - 125);
        graphics.fillText("%", 20, canvas.getHeight() - 380);

        // horizontal base
        graphics.strokeLine(LEFT_SIDE, BOTTOM_SIDE, LEFT_SIDE + GRAPH_WIDTH, BOTTOM_SIDE);
        // vertical base
        graphics.strokeLine(LEFT_SIDE, BOTTOM_SIDE, LEFT_SIDE, BOTTOM_SIDE - GRAPH_HEIGHT);

        // draw the actual lines
        graphics.setStroke(ConstantValues.GRAPH_LINE_COLOR1);
        drawLineInGraph(percentageInfected);
        graphics.setStroke(ConstantValues.GRAPH_LINE_COLOR2);
        drawLineInGraph(percentageCured);
    }

    /**
     * Draw the actual squiggly line with the data points.
     */
    private void drawLineInGraph(List<Integer> dataPoints) {
        for (int firstOfPair_i = 0; firstOfPair_i < dataPoints.size() - 1; firstOfPair_i++) {
            int firstOfPair = dataPoints.get(firstOfPair_i);
            int secondOfPair = dataPoints.get(firstOfPair_i + 1);

            graphics.strokeLine(
                    firstOfPair_i / (double) dataPoints.size() * GRAPH_WIDTH + LEFT_SIDE,
                    BOTTOM_SIDE - firstOfPair * GRAPH_HEIGHT / 100,
                    (firstOfPair_i + 1) / (double) dataPoints.size() * GRAPH_WIDTH + LEFT_SIDE,
                    BOTTOM_SIDE - secondOfPair * GRAPH_HEIGHT / 100);
        }

        graphics.setFont(new Font(15));
        graphics.fillText(dataPoints.get(dataPoints.size() - 1) + "%",
                LEFT_SIDE + GRAPH_WIDTH,
                BOTTOM_SIDE - dataPoints.get(dataPoints.size() - 1) * 2.5);
    }

    private void drawFlights() {
        graphics.setFill(ConstantValues.PLANE_LINE1);
        for (Flight flight : flights) {
            if (flight.isInfected()) {
                graphics.setFill(javafx.scene.paint.Color.RED);
            } else {
                graphics.setFill(javafx.scene.paint.Color.WHITE);
            }
            double pointRadius = 0.5;
            for (double currentProgress = 0.0;
                 currentProgress <= flight.getProgress();
                 currentProgress += flight.getSingleStep() * 30) {

                Point2D toDraw = flight.step(currentProgress);
                toDraw = geoFinder.mapToScreenCoordinates(toDraw.getX(), toDraw.getY());
                graphics.fillOval(toDraw.getX(), toDraw.getY(), pointRadius, pointRadius);
            }

            Point2D planeOnScreen = geoFinder.mapToScreenCoordinates(
                    flight.getCurrentLocation().getX(), flight.getCurrentLocation().getY());
            double radius = 3.0;
            graphics.fillOval(planeOnScreen.getX() - radius / 2, planeOnScreen.getY() - radius / 2,
                    radius, radius);
        }
    }

    /**
     * Draw the box in lower right with the country information.
     */
    private void drawSelectedCountryInformation() {
        if (selectedCountry == null) {
            return;
        }

        int baseX = (int) (canvas.getWidth() - 400);
        int baseY = (int) (canvas.getHeight() - 450);

        graphics.setFill(ConstantValues.BOX_COLOR1);
        graphics.fillRoundRect(baseX, baseY, 350, 350, 30, 30);


        graphics.setFill(ConstantValues.BOX_TITLES_COLOR1);
        graphics.setFont(new Font(17));

        String[] labelLines = {
                "Name:",
                "Code:",
                "Population:",
                "Population density:",
                "Infected People",
                "Cured Population",
                "Average yearly temperature:",
                "Current temperature:"
        };

        String[] contentLines = {
                selectedCountry.getName(),
                selectedCountry.getCode(),
                String.format("%,d", selectedCountry.getTotalPopulation()),
                String.valueOf(selectedCountry.getEnvironment().getPopulationDensity()),
                String.format("%,d", selectedCountry.getInfectedPopulation()),
                String.format("%,d", selectedCountry.getCuredPopulation()),
                String.valueOf(selectedCountry.getEnvironment().getAvgYearlyTemp()),
                String.valueOf(selectedCountry.getEnvironment().getCurrentTemperature())
        };

        for (int line_i = 0; line_i < labelLines.length; line_i++) {
            graphics.fillText(labelLines[line_i], baseX + 20, baseY + 40 * (line_i + 1));
        }

        graphics.setFill(ConstantValues.BOX_INFO_COLOR1);
        for (int line_i = 0; line_i < labelLines.length; line_i++) {
            graphics.fillText(contentLines[line_i], baseX + 50, baseY + 17.5 + 40 * (line_i + 1));
        }
    }

    /**
     * A percentage value is pushed to the argument list.
     *
     * @param percentageList the list to push to
     * @param element        the value to push in
     */
    public void pushNewPercentageValue(List<Integer> percentageList, int element) {
        if (percentageList.size() >= 100) {
            percentageList.set(0, element);
            Collections.rotate(percentageList, -1);
        } else {
            percentageList.add(element);
        }
        needsRepaint = true;
    }

    public void pushNewInfectedPercentageValue(int element) {
        pushNewPercentageValue(percentageInfected, element);
    }

    public void pushNewCuredPercentageValue(int element) {
        pushNewPercentageValue(percentageCured, element);
    }

    private void setNeedsRepaint() {
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
    private void selectStyleChange(double x, double y) {
        SimpleFeatureCollection features = geoFinder.getCountryFeaturesCollectionFromScreenCoordinates(x, y);

        Set<FeatureId> idSet = new HashSet<>();
        try (SimpleFeatureIterator iterator = features.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                idSet.add(feature.getIdentifier());
            }
        }
        displaySelectedFeatures(idSet);
    }

    private void displaySelectedFeatures(Set<FeatureId> IDs) {
        Style style = IDs.isEmpty() ?
                styleManager.createDefaultStyle() : styleManager.createSelectedStyle(IDs);
        setMapStyle(style);
    }

    /**
     * Deselect any selected countries.
     */
    private void deselectStyle() {
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

    public void setCountries(final List<Country> countriesToAdd) {
        this.countries = countriesToAdd;
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

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
