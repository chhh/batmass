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
package umich.ms.batmass.diaumpire.data;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSFeature2D;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSTrace;
import umich.ms.batmass.diaumpire.model.UmpireSeIsoCluster;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireSeFeature extends AbstractLCMSFeature2D<AbstractLCMSTrace>{
    protected UmpireSeIsoCluster cluster;

    public UmpireSeFeature(AbstractLCMSTrace[] traces, int charge) {
        super(traces, charge);
    }

    public UmpireSeFeature(AbstractLCMSTrace[] traces) {
        super(traces);
    }

    public UmpireSeIsoCluster getCluster() {
        return cluster;
    }

    public void setCompund(UmpireSeIsoCluster cluster) {
        this.cluster = cluster;
    }
    
    public static UmpireSeFeature create(UmpireSeIsoCluster c) {
        int numTraces = c.getNumPeaks();
        
        AbstractLCMSTrace[] tr = new AbstractLCMSTrace[numTraces];
        for (int i = 0; i < numTraces; i++) {
            tr[i] = new AbstractLCMSTrace(c.getMz()[i], c.getRtLo(), c.getRtHi());
        }
        
        int z = c.getCharge();
        
        UmpireSeFeature acf = new UmpireSeFeature(tr, z);
        acf.setCompund(c);
        return acf;
    }
    
    @Override
    public Color getColor() {
        return cluster.getNumPeaks() > 1 ? Color.MAGENTA : Color.RED;
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

    @Override
    public Float getOpacity() {
        return null;
    }
}