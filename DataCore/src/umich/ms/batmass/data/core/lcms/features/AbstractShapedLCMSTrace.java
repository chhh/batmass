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
 * Implementation of an LCMS trace which has a shape.
 * @author Dmitry Avtonomov
 */
public class AbstractShapedLCMSTrace extends AbstractLCMSTrace {
    protected volatile Shape shape = null;

    public AbstractShapedLCMSTrace(double mz, double spread, double rtLo, double rtHi) {
        super(mz, spread, rtLo, rtHi);
    }

    public AbstractShapedLCMSTrace(double mz, double rtLo, double rtHi) {
        super(mz, rtLo, rtHi);
    }

    public AbstractShapedLCMSTrace(double mz, double spread, double rtLo, double rtHi, Shape shape) {
        super(mz, spread, rtLo, rtHi);
        this.shape = shape;
    }

    public AbstractShapedLCMSTrace(double mz, double rtLo, double rtHi, Shape shape) {
        super(mz, rtLo, rtHi);
        this.shape = shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override
    public Shape getShape() {
        Shape s = shape;
        if (s == null) {
            synchronized (this) {
                s = shape;
                if (s == null) {
                    s = new Rectangle2D.Double(mz-spread, rtLo, spread*2d, rtHi-rtLo);
                    shape = s;
                }
            }
        }
        return s;
    }

}
