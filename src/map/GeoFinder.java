package map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.*;
import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import reader.ConstantValues;

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

    private int screenWidth, screenHeight;

    private SpatialIndexFeatureCollection countries;
    private SimpleFeatureSource featureSource;

    private final static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    public GeoFinder(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;

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
     */
    private AffineTransform createWorldToScreenAffineTransform() {
        AffineTransform translate = AffineTransform.getTranslateInstance(180.0, 90.0);
        AffineTransform scale = AffineTransform.
                getScaleInstance(screenWidth / 360.0, screenHeight / 173.5);
        AffineTransform mirrorY_axis = new AffineTransform(1, 0, 0, -1, 0, screenHeight);

        AffineTransform worldToScreen = new AffineTransform(mirrorY_axis);
        worldToScreen.concatenate(scale);
        worldToScreen.concatenate(translate);
        return worldToScreen;
    }

    public String getCountryName(double x, double y) {
        SimpleFeatureCollection features = getCountryFeaturesCollection(x, y);

        Optional<String> countryName = Optional.empty();
        try (SimpleFeatureIterator itr = features.features()) {
            while (itr.hasNext()) {
                SimpleFeature feature = itr.next();
                String name = (String) feature.getAttribute("name");
                countryName = Optional.ofNullable(name);
            }
        }
        return countryName.orElse("water");
    }

    /**
     * Get the collection of the coordinates of the borders of a country that contains the given
     * point.
     */
    public SimpleFeatureCollection getCountryFeaturesCollection(double x, double y) {
        Point2D pointInWorld = screenToMapCoordinates(x, y);
        //System.out.println(pointInWorld);
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(pointInWorld.getX(), pointInWorld.getY()));

        Filter filter = ff.contains(ff.property("the_geom"), ff.literal(point));
        return countries.subCollection(filter);
    }

    /**
     * Get an array of the coordinates of each vertex that is part of the borders of a country.
     */
    public Coordinate[] getCountryVertices(String countryName) {
        try (SimpleFeatureIterator iterator = getCountryFeatures(countryName).features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                return geom.getCoordinates();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Coordinate[0];
    }

    /**
     * Using a county's name - get the collection of the coordinates of its borders.
     */
    public SimpleFeatureCollection getCountryFeatures(String name) throws Exception {
        return featureSource.getFeatures(CQL.toFilter("name = '" + name + "'"));
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }
}
