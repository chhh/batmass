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

import java.awt.Point;

/**
 * Just an m/z and retention time pair. RT is in minutes.
 * Supposed to be immutable, but for convenience extends Point.Double: to use
 * it's methods and for easy compatibility with {@link MzRtRegion} - you can
 * check if the point is in a region for example.
 * Don't try to use Point.Double methods to set values
 * like <code>this.x</code> and do not use methods
 * like {@link #setLocation(double, double) }.
 */
public class MzRtPoint extends Point.Double {

    /**
     *
     * @param mz
     * @param rt in minutes
     */
    public MzRtPoint(double mz, double rt) {
        super(mz, rt);
    }

    /** copy constructor
     * @param orig */
    public MzRtPoint(MzRtPoint orig) {
        super(orig.getMz(), orig.getRt());
    }

    public double getMz() {
        return x;
    }

    /** In minutes
     * @return  */
    public double getRt() {
        return y;
    }

    public void setMz(double mz) {
        this.x = mz;
    }

    /** In minutes
     * @param rt */
    public void setRt(double rt) {
        this.y = rt;
    }

}
