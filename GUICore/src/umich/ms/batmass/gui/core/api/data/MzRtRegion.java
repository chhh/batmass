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
package umich.ms.batmass.gui.core.api.data;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * Just a rectangular area of LC/MS.
 * <b>WARNING:</b>
 * Supposed to be immutable, but for convenience (intersections etc) extends
 * Rectangle.Double, so dont try to use Rectangle.Double methods to set values
 * like <code>this.height</code> and do not use methods
 * like {@link #setRect(java.awt.geom.Rectangle2D) }.
 */
public class MzRtRegion extends Rectangle.Double {

    /**
     * @param mzStart
     * @param mzEnd
     * @param rtStart in minutes
     * @param rtEnd in minutes
     */
    public MzRtRegion(double mzStart, double mzEnd, double rtStart, double rtEnd) {
        super(mzStart, rtStart, mzEnd - mzStart, rtEnd - rtStart);
    }

    /** copy constructor
     * @param orig */
    public MzRtRegion(MzRtRegion orig) {
        super(orig.getMzLo(), orig.getRtLo(), orig.getMzSpan(), orig.getRtSpan());
    }

    public MzRtRegion(Rectangle2D orig) {
        super(orig.getX(), orig.getY(), orig.getWidth(), orig.getHeight());
    }

    public double getMzLo() {
        return x;
    }

    public double getMzHi() {
        return x + width;
    }

    /** In minutes
     * @return  */
    public double getRtLo() {
        return y;
    }

    /** In minutes
     * @return  */
    public double getRtHi() {
        return y + height;
    }

    public double getMzSpan() {
        return width;
    }

    /** In minutes
     * @return  */
    public double getRtSpan() {
        return height;
    }

    public void setMzLo(double mz) {
        this.x = mz;
    }

    /** In minutes
     * @param rt */
    public void setRtLo(double rt) {
        this.y = rt;
    }

    public void setMzSpan(double mzSpan) {
        this.width = mzSpan;
    }

    /** In minutes
     * @param rtSpan */
    public void setRtSpan(double rtSpan) {
        this.height = rtSpan;
    }

    @Override
    public String toString() {
        return String.format("[m/z: %.1f-%.1f, RT: %.1f-%.1f]", getMzLo(), getMzHi(), getRtLo(), getRtHi());
    }


}
