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
 *
 * @author Dmitry Avtonomov
 * @param <T>
 */
public abstract class AbstractLCMSFeature2D<T extends ILCMSTrace> implements ILCMSFeature2D<T> {
    protected T[] traces;
    protected int charge;

    protected volatile Rectangle2D bounds = null;

    /**
     *
     * @param traces
     * @param charge if charge is unknown use the constructor, that doesn't accept charge,
     * or provide {@link #CHARGE_UNKNOWN} as charge.
     */
    public AbstractLCMSFeature2D(T[] traces, int charge) {
        this.traces = traces;
        this.charge = charge;
    }

    public AbstractLCMSFeature2D(T[] traces) {
        this.traces = traces;
        this.charge = CHARGE_UNKNOWN;
    }


    @Override
    public T[] getTraces() {
        return traces;
    }

    @Override
    public int getCharge() {
        return charge;
    }

    /**
     * Default implementation just returns the bounding box.
     * @return
     */
    @Override
    public Shape getContour() {
        return getBounds();
    }

    @Override
    public Rectangle2D getBounds() {
        Rectangle2D tmp = bounds;
        if (tmp == null) {
            synchronized (this) {
                tmp = bounds;
                if (tmp == null) {
                    tmp = createBoundsFromTraces();
                    bounds = tmp;
                }
            }
        }
        return tmp;
    }

    /**
     * Creates a union of all the traces comprising this feature.
     * @return
     */
    protected Rectangle2D.Double createBoundsFromTraces() {
        //double mzLoLo = Double.POSITIVE_INFINITY;
        //double mzHiHi = Double.NEGATIVE_INFINITY;
        //double rtLoLo = Double.POSITIVE_INFINITY;
        //double rtHiHi = Double.NEGATIVE_INFINITY;

        if (traces.length == 0) {
            throw new IllegalStateException("Traces array can't be empty!");
        }

        // assign the bounding box to be the bounding box of the first trace
        Rectangle2D.Double boundBox = new Rectangle2D.Double();
        ILCMSTrace trace = traces[0];
        
        boundBox.setRect(trace.getShape().getBounds2D());
        // and now add all other bounding boxes (we don't need that, traces are
        // sorted by m/z, so we should be able to just take the last trace)
        //for (int i = 1; i < traces.length; i++) {
        //    Rectangle2D.union(boundBox, traces[i].getShape().getBounds2D(), boundBox);
        //}
        if (traces.length > 1) {
            trace = traces[traces.length - 1];
            Rectangle2D.union(boundBox, trace.getShape().getBounds2D(), boundBox);
        }

        return boundBox;
    }
}
