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

/**
 * The default implementation of an LCMS trace. Use it as base for extensions.
 * @author Dmitry Avtonomov
 */
public class AbstractLCMSTrace implements ILCMSTrace {
    protected double mz;
    protected double spread;
    protected double rtLo;
    protected double rtHi;
    /** Multiply m/z by that factor to get m/z spread from PPM. */
    protected static final double SPREAD_PPM_FACTOR = ILCMSTrace.DEFAULT_MZ_SPREAD_PPM / 1e6d;

    public AbstractLCMSTrace(double mz, double spread, double rtLo, double rtHi) {
        this.mz = mz;
        this.spread = spread;
        this.rtLo = rtLo;
        this.rtHi = rtHi;
    }

    public AbstractLCMSTrace(double mz, double rtLo, double rtHi) {
        this.mz = mz;
        this.rtLo = rtLo;
        this.rtHi = rtHi;
        this.spread = mz * SPREAD_PPM_FACTOR;
    }

    
    public void setSpread(double spread) {
        this.spread = spread;
    }

    @Override
    public double getMz() {
        return mz;
    }

    @Override
    public double getMzSpread() {
        return spread;
    }

    @Override
    public double getRtLo() {
        return rtLo;
    }

    @Override
    public double getRtHi() {
        return rtHi;
    }

    @Override
    public Shape getShape() {
        throw new UnsupportedOperationException(
            "This implementation does not support shapes for LCMSTraces. "
            + "Most likely you get this error because you extended AbstractLCMSFeature2D "
            + "and did not override its #createBoundsFromTraces() method, which "
            + "calls LCMSTrace#getShape() internally. The solution is to override "
            + "#createBoundsFromTraces(), see example at "
            + "umich.ms.batmass.filesupport.files.types.umpire.data.features.UmpireFeature");
    }

}
