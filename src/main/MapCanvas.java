package main;

import com.vividsolutions.jts.geom.Coordinate;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
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
import java.util.HashSet;
import java.util.Set;

public class MapCanvas {

    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

    private Canvas canvas;
    private MapContent map;
    private GraphicsContext graphics;
    private GeoFinder geoFinder;
    private StyleManager styleManager;

    private static final double FPS = 60.0;
    private boolean needsRepaint = true;
    private double dragDistanceX;
    private double dragDistanceY;

    public MapCanvas(int width, int height) {
        canvas = new Canvas(width, height);
        geoFinder = new GeoFinder(width, height);
        styleManager = new StyleManager(geoFinder.getFeatureSource());
        graphics = canvas.getGraphicsContext2D();
        initMap();
        drawMap(graphics);
        initEvent();
        initPaintThread();
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

    private void drawMap(GraphicsContext gc) {
        if (!needsRepaint) {
            return;
        }
        needsRepaint = false;
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(map);
        FXGraphics2D graphics = new FXGraphics2D(gc);
        graphics.setBackground(Color.BLUE);
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
        Rectangle rectangle = new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight());
        draw.paint(graphics, rectangle, map.getViewport().getBounds());

        gc.setFill(javafx.scene.paint.Color.YELLOW);
        Coordinate[] points = geoFinder.getCountryVertices("Russia");
        for (Coordinate mapPoint : points) {
            Point2D screenPoint = geoFinder.mapToScreenCoordinates(mapPoint.x, mapPoint.y);
            gc.fillOval(screenPoint.getX(), screenPoint.getY(), 2, 2);
        }
    }

    private void initEvent() {
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
            env.translate(env.getMinimum(0) - result.x, env.getMaximum(1) - result.y);
            setViewport(env);
            e.consume();

        });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getClickCount() == 1) {
                //System.out.println("SCREEN: " + t.getX() + "; " + t.getY());
                /*String countryName = geoFinder.getCountryName(t.getX(), t.getY());
                System.out.println(countryName);*/

                selectStyleChange(t.getX(), t.getY());
                needsRepaint = true;
            }
            t.consume();
        });

        canvas.addEventHandler(ScrollEvent.SCROLL, e -> {
            ReferencedEnvelope envelope = map.getViewport().getBounds();
            double percent = -e.getDeltaY() / canvas.getWidth();
            double width = envelope.getWidth();
            double height = envelope.getHeight();
            double deltaW = width * percent;
            double deltaH = height * percent;
            envelope.expandBy(deltaW, deltaH);

            /*double refitting = (Math.abs(envelope.getMinimum(1)) - envelope.getMaximum(1)) / 2;
            envelope.translate(0, refitting);

            System.out.println(envelope.toString());
            System.out.println(refitting);*/

            setViewport(envelope);
            e.consume();
        });
    }

    private void initPaintThread() {
        ScheduledService<Boolean> svc = new ScheduledService<Boolean>() {
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    protected Boolean call() {
                        Platform.runLater(() -> drawMap(graphics));
                        return true;
                    }
                };
            }
        };
        svc.setPeriod(Duration.millis(1000.0 / FPS));
        svc.start();
    }

    protected void setViewport(ReferencedEnvelope envelope) {
        map.getViewport().setBounds(envelope);
        needsRepaint = true;
    }

    private void selectStyleChange(double x, double y) {
        SimpleFeatureCollection features = geoFinder.getCountryFeaturesCollection(x, y);

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
        Style style;

        if (IDs.isEmpty()) {
            style = styleManager.createDefaultStyle();

        } else {
            style = styleManager.createSelectedStyle(IDs);
        }

        Layer layer = map.layers().get(0);
        ((FeatureLayer) layer).setStyle(style);
        //mapFrame.getMapPane().repaint();
    }
}
