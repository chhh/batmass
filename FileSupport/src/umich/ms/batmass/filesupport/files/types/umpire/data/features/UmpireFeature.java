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
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import MSUmpire.PeakDataStructure.PeakCluster;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSFeature2D;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSTrace;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeature extends AbstractLCMSFeature2D<AbstractLCMSTrace> {
    private boolean isIdentified = false;

    /**
     * Use factory method {@link #create(MSUmpire.PeakDataStructure.PeakCluster) }.
     * @param traces
     * @param charge
     */
    protected UmpireFeature(AbstractLCMSTrace[] traces, int charge) {
        super(traces, charge);

    }

    /**
     * Use factory method {@link #create(MSUmpire.PeakDataStructure.PeakCluster) }.
     * @param traces
     */
    protected UmpireFeature(AbstractLCMSTrace[] traces) {
        super(traces);
    }

    /**
     * Factory method to convert features from Chih-Chiang's .ser parser.
     * @param pc
     * @return 
     */
    public static UmpireFeature create(PeakCluster pc) {
        int numTraces = countTraces(pc);

        AbstractLCMSTrace[] tr = new AbstractLCMSTrace[numTraces];
        for (int i = 0; i < tr.length; i++) {
            tr[i] = new AbstractLCMSTrace(pc.mz[i], pc.startRT, pc.endRT);
        }

        int charge = pc.Charge;
        if (charge == 0) {
            charge = CHARGE_UNKNOWN;
        }

        UmpireFeature feature = new UmpireFeature(tr, charge);
        feature.setIsIdentified(pc.Identified);
        
        return feature;
    }

    protected static int countTraces(PeakCluster pc) {
        int numTraces = 0;
        for (int i = 0; i < pc.mz.length; i++) {
            if (pc.mz[i] == 0f) {
                break;
            }
            numTraces++;
        }
        if (numTraces == 0) {
            throw new IllegalStateException("The provided peak cluster had no non-zero isotopic m/z values");
        }
        return numTraces;
    }

    protected static AbstractLCMSTrace[] createTraces(PeakCluster pc) {
        int numTraces = countTraces(pc);
        AbstractLCMSTrace[] tr = new AbstractLCMSTrace[numTraces];
        for (int i = 0; i < tr.length; i++) {
            tr[i] = new AbstractLCMSTrace(pc.mz[i], pc.startRT, pc.endRT);
        }
        return tr;
    }

    
    /**
     * Overriding this method, because we don't store shapes/bounds for LCMS Traces
     * in Umpire Features.
     * @return
     */
    @Override
    protected Rectangle2D.Double createBoundsFromTraces() {
        int traceNumLo, traceNumHi;
        traceNumLo = 0; // 0-th trace is required to be there
        traceNumHi = traces.length-1;

        double mzLo = traces[traceNumLo].getMz() - traces[traceNumLo].getMzSpread();
        double mzHi = traces[traceNumHi].getMz() + traces[traceNumHi].getMzSpread();
        double rtLo = Double.POSITIVE_INFINITY;
        double rtHi = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < traces.length; i++) {
            AbstractLCMSTrace trace = traces[i];
            if (trace.getRtLo() < rtLo) {
                rtLo = trace.getRtLo();
            }
            if (trace.getRtHi() > rtHi) {
                rtHi = trace.getRtHi();
            }
        }

        return new Rectangle2D.Double(mzLo, rtHi, mzHi - mzLo, rtHi - rtLo);
    }

    
    public boolean getIsIdentified() {
        return isIdentified;
    }

    public boolean isIsIdentified() {
        return isIdentified;
    }

    public void setIsIdentified(boolean isIdentified) {
        this.isIdentified = isIdentified;
    }

    @Override
    public Color getColor() {
        return isIdentified ? Color.GREEN : Color.RED;
    }
}
