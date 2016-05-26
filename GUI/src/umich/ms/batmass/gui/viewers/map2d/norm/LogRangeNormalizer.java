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
package umich.ms.batmass.gui.viewers.map2d.norm;

import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author Dmitry Avtonomov
 */
public class LogRangeNormalizer implements RangeNormalizer {
    double maxVal;
    double minNonZeroVal;
    double targetRange;
    double logBase;

    @Override
    public double getScaled(double x) {
        return FastMath.log(logBase, x);
    }

    @Override
    public double getOriginal(double x) {
        return FastMath.pow(logBase, x);
    }

    @Override
    public boolean equals(RangeNormalizer obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogRangeNormalizer other = (LogRangeNormalizer) obj;
        if (Double.doubleToLongBits(this.maxVal) != Double.doubleToLongBits(other.maxVal)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minNonZeroVal) != Double.doubleToLongBits(other.minNonZeroVal)) {
            return false;
        }
        return true;
    }

    @Override
    public void configure(double maxVal, double minNonZeroVal, double targetRange) {
        this.maxVal = maxVal;
        this.minNonZeroVal = minNonZeroVal;
        this.targetRange = targetRange;
        double range = maxVal - minNonZeroVal;

        logBase = FastMath.exp((FastMath.log(maxVal) - FastMath.log(minNonZeroVal)) / targetRange);
    }
}
