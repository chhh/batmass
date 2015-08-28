/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.lcms.features;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A single mass trace in an LCMS run. E.g. just one isotopic peak.
 * @author Dmitry Avtonomov
 */
public interface ILCMSTrace {
    /**
     * Default spread in ppm.
     */
    public static final double DEFAULT_MZ_SPREAD_PPM = 30;
    /**
     * The assumed m/z of this trace. Even if the trace is not straight, there
     * still should be some assumed m/z value assigned to the trace, otherwise
     * it's not very helpful to have a trace at all.
     * @return
     */
    double getMz();
    /**
     * Some metric of the spread of this trace, i.e. how stable the mass was
     * over the span of its elution.
     * @return {@link Double#NaN}, if the metric is not available.
     */
    double getMzSpread();
    /**
     * The beginning of chromatographic elution.
     * @return
     */
    double getRtLo();
    /**
     * The end of chromatographic elution.
     * @return
     */
    double getRtHi();

    /**
     * Optional operation.<br/>
     * If null is returned here, the default logic should be to provide a
     * {@link Rectangle2D} using [m/z +/- spread] for m/z span and rtLo, rtHi
     * for RT span.<br/>
     * Shape's X coordinate = m/z, Y coordinate = <b>RT in minutes</b>.<br/>
     * Coordinates start at bottom right corner, RT goes "up" (positive Y axis
     * direction), m/z goes "right" (positive X axis directions).
     * @return it's OK to return null, if no particular shape is detected by
     * your feature finding algorithm.
     * @see AbstractLCMSTrace
     */
    Shape getShape();
}
