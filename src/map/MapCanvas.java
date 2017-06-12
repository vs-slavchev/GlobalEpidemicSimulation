package map;

import interfaces.CountryPercentageListener;
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

/**
 * Owner: Veselin
 * <p>
 * Initializes and draws the map. Has event handlers strictly related to the map only.
 */

public class MapCanvas implements CountryPercentageListener {

    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
    private Canvas canvas;
    private MapContent map;
    private GraphicsContext graphics;
    private ArrayList<City> cities;
    private GeoFinder geoFinder;
    private StyleManager styleManager;
    private List<Flight> flights;
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

        graphics.setFill(ConstantValues.POINTS_COLOR1);

        drawCities(graphics, cities);
        if (selectedCountry != null) {
            //changes the color of the points in a selected country
            graphics.setFill(ConstantValues.SELECTED_COUNTRY_POINTS_COLOR1);
            drawCities(graphics, selectedCountry.getCities());
        }
    }

    private void drawCities(GraphicsContext gc, List<City> citiesToDraw) {
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font(25));
        for (City city : citiesToDraw) {
            Point2D screenCity = geoFinder.mapToScreenCoordinates(
                    city.getLatitude(), city.getLongitude());
            int radius = (int) calculatePointRadius(city.getPopulation());
            gc.fillOval(screenCity.getX() - radius / 2, screenCity.getY() - radius / 2,
                    radius, radius);

            if (geoFinder.createWorldToScreenAffineTransform().getScaleX() > 50) {
                gc.fillText(city.getName(), screenCity.getX(),
                        screenCity.getY() + radius / 2 + 10);
                // radius + 10
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
        return Math.max(0, 20 - cityPopulation / 1_000_000.0) / 8 + Math.sqrt(cityPopulation / 1_000_000.0) * scale;
    }

    private void drawGraph() {
        // draw the base
        graphics.setStroke(ConstantValues.GRAPH_STROKE_COLOR1);
        graphics.setLineWidth(3);
        graphics.setFill(ConstantValues.GRAPH_TEXT_COLOR1);
        graphics.setFont(new Font(25));
        graphics.fillText("time", 300, canvas.getHeight() - 125);
        graphics.fillText("%", 20, canvas.getHeight() - 380);
        graphics.strokeLine(50, canvas.getHeight() - 150, 350, canvas.getHeight() - 150);
        graphics.strokeLine(50, canvas.getHeight() - 150, 50, canvas.getHeight() - 400);

        // draw the lines
        graphics.setStroke(ConstantValues.GRAPH_LINE_COLOR1);
        for (int firstOfPair_i = 0; firstOfPair_i < percentageInfected.size() - 1; firstOfPair_i++) {
            int firstOfPair = percentageInfected.get(firstOfPair_i);
            int secondOfPair = percentageInfected.get(firstOfPair_i + 1);

            graphics.strokeLine(
                    (firstOfPair_i / (double) percentageInfected.size() * 100.0) * 3 + 50,
                    canvas.getHeight() - 150 - firstOfPair * 2.5,
                    ((firstOfPair_i + 1) / (double) percentageInfected.size() * 100.0) * 3 + 50,
                    canvas.getHeight() - 150 - secondOfPair * 2.5);
        }
    }

    private void drawFlights() {
        graphics.setFill(ConstantValues.PLANE_LINE1);
        for (Flight flight : flights) {
            graphics.setFill(javafx.scene.paint.Color.WHITE);
            double pointRadius = 0.5;
            for (double currentProgress = 0.0;
                 currentProgress <= flight.getProgress();
                 currentProgress += flight.getSingleStep() * 8) {

                Point2D toDraw = flight.step(currentProgress);
                toDraw = geoFinder.mapToScreenCoordinates(toDraw.getX(), toDraw.getY());
                graphics.fillOval(toDraw.getX(), toDraw.getY(), pointRadius, pointRadius);
            }

            Point2D planeOnScreen = geoFinder.mapToScreenCoordinates(
                    flight.getCurrentLocation().getX(), flight.getCurrentLocation().getY());
            int radius = 3;
            graphics.fillOval(planeOnScreen.getX() - radius/2, planeOnScreen.getY() - radius/2,
                    radius, radius);
        }
    }

    private void drawSelectedCountryInformation() {
        if (selectedCountry == null) {
            return;
        }

        int baseX = (int) (canvas.getWidth() - 400);
        int baseY = (int) (canvas.getHeight() - 420);

        graphics.setFill(ConstantValues.BOX_COLOR1);
        graphics.fillRect(baseX, baseY, 350, 320);

        graphics.setFill(ConstantValues.BOX_TITLES_COLOR1);
        graphics.setFont(new Font(15));

        String[] labelLines = {
                "Name:",
                "Code:",
                "Population:",
                "Population density:",
                "Infected People",
                "Cured Population",
                "Average yearly temperature:"
        };

        String[] contentLines = {
                selectedCountry.getName(),
                selectedCountry.getCode(),
                String.format("%,d", selectedCountry.getTotalPopulation()),
                String.valueOf(selectedCountry.getEnvironment().getPopulationDensity()),
                String.format("%,d", selectedCountry.getInfectedPopulation()),
                String.format("%,d", selectedCountry.getCuredPopulation()),
                String.valueOf(selectedCountry.getEnvironment().getAvgYearlyTemp())
        };

        for (int line_i = 0; line_i < labelLines.length; line_i++) {
            graphics.fillText(labelLines[line_i], baseX + 20, baseY + 40 * (line_i + 1));
        }

        graphics.setFill(ConstantValues.BOX_INFO_COLOR1);
        for (int line_i = 0; line_i < labelLines.length; line_i++) {
            graphics.fillText(contentLines[line_i], baseX + 50, baseY + 17.5 + 40 * (line_i + 1));
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

    public void setCities(final ArrayList<City> citiesToAdd) {
        this.cities = citiesToAdd;
        removeOverlappingCities();
        removeCitiesInWater();
    }

    /**
     * The smaller city of 2 overlapping ones is removed.
     * <p>
     * Each city is compared against another one only a single time.
     */
    private void removeOverlappingCities() {
        Set<City> toRemove = new HashSet<>();

        for (int first_i = 0; first_i < this.cities.size(); first_i++) {
            for (int other_i = first_i + 1; other_i < this.cities.size(); other_i++) {
                City first = cities.get(first_i);
                City other = cities.get(other_i);
                if ((int) first.getLatitude() == (int) other.getLatitude()
                        && (int) first.getLongitude() == (int) other.getLongitude()) {
                    City smallerCity = first.getPopulation() > other.getPopulation() ? other : first;
                    toRemove.add(smallerCity);
                }
            }
        }
        cities.removeAll(toRemove);
    }

    private void removeCitiesInWater() {
        Set<City> toRemove = new HashSet<>();
        for (City city : this.cities) {
            if (geoFinder.getCountryCodeFromMapCoordinates(city.getLatitude(), city.getLongitude())
                    .equals("water") && city.getPopulation() < 1_000_000) {
                toRemove.add(city);
            }
        }
        cities.removeAll(toRemove);
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

    @Override
    public void CountryReachedBreakPoint(double x, double y) {
//        PercentageStyleChange(x, y);
        //System.out.print(geoFinder.getCountryCodeFromMapCoordinates(x, y) + " reached 50% infected population \n");
        setNeedsRepaint();
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
