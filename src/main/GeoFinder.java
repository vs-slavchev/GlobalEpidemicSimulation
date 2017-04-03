package main;

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

    final static public String countryFile = "maps/ne_110m_admin_0_countries.shp";

    public GeoFinder(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;

        URL countryURL = DataUtilities.fileToURL(new File(countryFile));
        HashMap<String, Object> params = new HashMap<>();
        params.put("url", countryURL);
        try {
            DataStore ds = DataStoreFinder.getDataStore(params);
            if (ds == null) {
                throw new IOException("couldn't open " + params.get("url"));
            }
            Name name = ds.getNames().get(0);
            countries = new SpatialIndexFeatureCollection(ds.getFeatureSource(name).getFeatures());
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        try {
            FileDataStore store = FileDataStoreFinder.getDataStore(new File(countryFile));
            featureSource = store.getFeatureSource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Point2D screenToMapCoordinates(double x, double y) {
        return createScreenToWorldAffineTransform().transform(new Point2D.Double(x, y), null);
    }

    public Point2D mapToScreenCoordinates(double x, double y) {
        return createWorldToScreenAffineTransform().transform(new Point2D.Double(x, y), null);
    }

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

    public SimpleFeatureCollection getCountryFeaturesCollection(double x, double y) {
        Point2D pointInWorld = screenToMapCoordinates(x, y);
        //System.out.println(pointInWorld);
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(pointInWorld.getX(), pointInWorld.getY()));

        Filter filter = ff.contains(ff.property("the_geom"), ff.literal(point));
        return countries.subCollection(filter);
    }

    public Coordinate[] getCountryVertices(String countryName) {
        try (SimpleFeatureIterator iterator = grabSelectedName(countryName).features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();

                //System.out.println(array_of_coords[0].x);
                //System.out.println(array_of_coords[0].y);

                Geometry geom = (Geometry) feature.getDefaultGeometry();
                return geom.getCoordinates();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Coordinate[0];
    }

    public SimpleFeatureCollection grabSelectedName(String name) throws Exception {
        return featureSource.getFeatures(CQL.toFilter("name = '" + name + "'"));
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }
}
