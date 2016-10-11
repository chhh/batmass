/* 
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * Coordinates start at bottom left corner, RT goes "up" (positive Y axis
     * direction), m/z goes "right" (positive X axis directions).
     * @return it's OK to return null, if no particular shape is detected by
     * your feature finding algorithm.
     * @see AbstractLCMSTrace
     */
    Shape getShape();
}
