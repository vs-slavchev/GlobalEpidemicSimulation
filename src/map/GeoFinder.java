package map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import main.ConstantValues;
import org.geotools.data.*;
import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;

/**
 * Owner: Veselin
 */

public class GeoFinder {

    private final static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    private int viewportWidth, viewportHeight;
    private double mapWidth = 360.0, mapHeight = 173.5;
    private double panOffsetX = 180.0, panOffsetY = 83.75;
    private SpatialIndexFeatureCollection countries;
    private SimpleFeatureSource featureSource;

    public GeoFinder(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;

        URL countryURL = DataUtilities.fileToURL(new File(ConstantValues.MAP_SHAPE_FILE));
        HashMap<String, Object> params = new HashMap<>();
        params.put("url", countryURL);
        try {
            DataStore dataStore = DataStoreFinder.getDataStore(params);
            if (dataStore == null) {
                throw new IOException("Couldn't open " + params.get("url"));
            }
            Name name = dataStore.getNames().get(0);
            countries = new SpatialIndexFeatureCollection(
                    dataStore.getFeatureSource(name).getFeatures());

            FileDataStore fileStore = FileDataStoreFinder.getDataStore(
                    new File(ConstantValues.MAP_SHAPE_FILE));
            featureSource = fileStore.getFeatureSource();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Point2D screenToMapCoordinates(double x, double y) {
        return createScreenToWorldAffineTransform().transform(new Point2D.Double(x, y), null);
    }

    public Point2D mapToScreenCoordinates(double x, double y) {
        return createWorldToScreenAffineTransform().transform(new Point2D.Double(x, y), null);
    }

    /**
     * Creates an object that is responsible for the matrix math to transform a point from screen
     * to world coordinates.
     */
    private AffineTransform createScreenToWorldAffineTransform() {
        AffineTransform worldToScreen = createWorldToScreenAffineTransform();

        AffineTransform screenToWorld = null;
        try {
            screenToWorld = worldToScreen.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        return screenToWorld;
    }

    /**
     * Creates an object that is responsible for the matrix math to transform a point from world
     * to screen coordinates.
     * <p>
     * Explanation:
     * The translation sets both origins to the same point.
     * The scaling makes both matrices have the same width and height.
     * The mirroring takes care of the the fact that the screen vertical increases downward, but
     * the map vertical increases upward.
     * All 3 transforms take into account the offset caused by the panning and also the current
     * viewport width and height, which are affected by the zoom.
     */
    public AffineTransform createWorldToScreenAffineTransform() {
        AffineTransform translate = AffineTransform.
                getTranslateInstance(mapWidth / 2 - (panOffsetX - mapWidth / 2),
                        mapHeight / 2 - (panOffsetY - (mapHeight / 2 - 3.1)) + 3.25);
        AffineTransform scale = AffineTransform.
                getScaleInstance(viewportWidth / mapWidth, viewportHeight / mapHeight);
        AffineTransform mirrorY_axis =
                new AffineTransform(1, 0, 0, -1, 0, viewportHeight);

        AffineTransform worldToScreen = new AffineTransform(mirrorY_axis);
        worldToScreen.concatenate(scale);
        worldToScreen.concatenate(translate);
        return worldToScreen;
    }

    public String getCountryCodeFromScreenCoordinates(double x, double y) {
        SimpleFeatureCollection features = getCountryFeaturesCollectionFromScreenCoordinates(x, y);
        return extractCountryCode(features);
    }

    public String getCountryCodeFromMapCoordinates(double x, double y) {
        SimpleFeatureCollection features = getCountryFeaturesCollectionFromMapCoordinates(x, y);
        return extractCountryCode(features);
    }

    private String extractCountryCode(SimpleFeatureCollection features) {
        Optional<String> countryNameCode = Optional.empty();
        try (SimpleFeatureIterator itr = features.features()) {
            while (itr.hasNext()) {
                SimpleFeature feature = itr.next();
                // other codes: sov_a3, gu_a3, su_a3, brk_a3, iso_a2, iso_a3
                String name = (String) feature.getAttribute("adm0_a3");
                countryNameCode = Optional.ofNullable(name);
            }
        }
        return countryNameCode.orElse("water");
    }

    /**
     * Get the collection of the coordinates of the borders of a country that contains the given
     * point.
     */
    private SimpleFeatureCollection getCountryFeaturesCollectionFromMapCoordinates(double x, double y) {
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(x, y));

        Filter filter = ff.contains(ff.property("the_geom"), ff.literal(point));

        return countries.subCollection(filter);
    }

    SimpleFeatureCollection getCountryFeaturesCollectionFromScreenCoordinates(double x, double y) {
        Point2D pointInWorld = screenToMapCoordinates(x, y);
        return getCountryFeaturesCollectionFromMapCoordinates(pointInWorld.getX(), pointInWorld.getY());
    }

    SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    void setMapWidth(double mapWidth) {
        this.mapWidth = mapWidth;
    }

    void setMapHeight(double mapHeight) {
        this.mapHeight = mapHeight;
    }

    void setPanOffsetX(double panOffsetX) {
        this.panOffsetX = panOffsetX;
    }

    void setPanOffsetY(double panOffsetY) {
        this.panOffsetY = panOffsetY;
    }
}
