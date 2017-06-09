package map;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import main.ConstantValues;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import java.awt.*;
import java.util.Set;

import static map.MapCanvas.filterFactory;
import static map.MapCanvas.styleFactory;

/**
 * Owner: Veselin
 * <p>
 * The class is only responsible for setting and initializing the different styles in which
 * the map can be rendered. A style determines the color and width of visual elements.
 */

class StyleManager {

    private static final Color SELECTED_COLOUR = ConstantValues.SELECTED_COUNTRY_COLOR1;
    private static final Color SELECTED_LINE_COLOUR = ConstantValues.SELECTED_COUNTRY_LINE_COLOR1;
    private static final float OPACITY = 0.1f;
    private static final float LINE_WIDTH = 1.0f;

    private StyleFactory sf = CommonFactoryFinder.getStyleFactory();
    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    private GeomType geometryType;
    private String geometryAttributeName;

    public StyleManager(FeatureSource featureSource) {
        GeometryDescriptor geomDesc = featureSource.getSchema().getGeometryDescriptor();
        geometryAttributeName = geomDesc.getLocalName();

        Class<?> classDescriptor = geomDesc.getType().getBinding();

        if (Polygon.class.isAssignableFrom(classDescriptor) ||
                MultiPolygon.class.isAssignableFrom(classDescriptor)) {
            geometryType = GeomType.POLYGON;

        } else if (LineString.class.isAssignableFrom(classDescriptor) ||
                MultiLineString.class.isAssignableFrom(classDescriptor)) {

            geometryType = GeomType.LINE;
        }
    }

    public Style createDefaultStyle() {
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(ConstantValues.LINE_COLOR1),
                filterFactory.literal(1),
                filterFactory.literal(0.5));

        Fill fill = styleFactory.createFill(
                filterFactory.literal(ConstantValues.FILL_COLOR1),
                filterFactory.literal(0.5));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geometry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    public Style createSelectedStyle(Set<FeatureId> IDs) {
        Rule selectedRule = createRule(SELECTED_LINE_COLOUR, SELECTED_COLOUR);
        selectedRule.setFilter(ff.id(IDs));

        Rule otherRule = createRule(ConstantValues.LINE_COLOR1, ConstantValues.FILL_COLOR1);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    public Style changeStyle(Set<FeatureId> IDs) {
        Color red = new Color(255, 0, 0);
        Rule selectedRule = createRule(SELECTED_LINE_COLOUR, red);
        selectedRule.setFilter(ff.id(IDs));

        Rule otherRule = createRule(ConstantValues.LINE_COLOR1, ConstantValues.FILL_COLOR1);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    private Rule createRule(Color outlineColor, Color fillColor) {
        Symbolizer symbolizer = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

        switch (geometryType) {
            case POLYGON:
                Fill fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
                symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
                break;

            case LINE:
                symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
                break;
        }

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    private enum GeomType {LINE, POLYGON}
}
