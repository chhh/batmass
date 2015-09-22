/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.lcms.features.api;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Helper functions for working with features and converting between coordinates.
 * @author Dmitry Avtonomov
 */
public class FeatureUtils {

    private FeatureUtils() {}


    public static Rectangle geometryAwtToRtree(Rectangle2D rect) {
        // this is how those coordinates are considered in the Geometries.rectangle() factory method.
        //double x1 = rect.getMinX();
        //double x2 = rect.getMaxX();
        //double y1 = rect.getY();
        //double y2 = rect.getMaxY();
        return Geometries.rectangle(rect.getMinX(), rect.getY(), rect.getMaxX(), rect.getMaxY());
    }

    public static Rectangle2D geometryRtreeToAwt(Rectangle rect) {
        return new Rectangle2D.Float(rect.x1(), rect.y2(), rect.x2()-rect.x1(), rect.y2()-rect.y1());
    }

    public static Point geometryAwtToRtree(Point2D point) {
        return Geometries.point(point.getX(), point.getY());
    }

    public static Point2D geometryAwtToRtree(Point point) {
        return new Point2D.Float(point.x(), point.y());
    }
}
