/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.lcms.features;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * An LCMS feature, that can be overlaid on Map2D.
 * @author Dmitry Avtonomov
 * @param <T> concrete type of LCMS traces used in this feature
 */
public interface ILCMSFeature2D<T extends ILCMSTrace> {
    public static final int CHARGE_UNKNOWN = Integer.MIN_VALUE;
    /**
     * Traces in the array must be sorted by their m/z values
     * ({@link ILCMSTrace#getMz() }).
     * @return an empty array is not allowed
     */
    T[] getTraces();
    /**
     * The charge is optional, if there's only one trace in the feature, it can't
     * even be determined.
     * @return {@link #CHARGE_UNKNOWN} if the charge could not be determined.
     */
    int getCharge();

    /**
     * A bounding box or a convex hull or whatever shape you like that encloses
     * all the traces of this feature.<br/>
     * The default logic should be to provide a {@link Rectangle2D} with
     * m/z spanning from the smallest m/z trace to the largest one and RT spanning
     * from the smallest RT among traces to the highest.
     * @return
     */
    Shape getContour();

    /**
     * Rectangular bounding box fully covering the feature.
     * @return
     */
    Rectangle2D getBounds();
    
    /**
     * To be used when drawing this feature.
     * @return 
     */
    Color getColor();
}
