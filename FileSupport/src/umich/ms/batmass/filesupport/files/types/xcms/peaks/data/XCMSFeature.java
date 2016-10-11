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
package umich.ms.batmass.filesupport.files.types.xcms.peaks.data;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSFeature2D;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSTrace;
import static umich.ms.batmass.data.core.lcms.features.ILCMSFeature2D.CHARGE_UNKNOWN;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.model.XCMSPeak;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.model.XCMSPeakGroup;

/**
 *
 * @author Dmitry Avtonomov
 */
public class XCMSFeature extends AbstractLCMSFeature2D<XCMSTrace>{
    protected XCMSPeakGroup group;

    public XCMSFeature(XCMSTrace[] traces, int charge) {
        super(traces, charge);
    }

    public XCMSFeature(XCMSTrace[] traces) {
        super(traces);
    }

    public XCMSPeakGroup getGroup() {
        return group;
    }

    public void setGroup(XCMSPeakGroup group) {
        this.group = group;
    }
    
    public static XCMSFeature create(XCMSPeakGroup group) {
         int numTraces = group.size();

        XCMSTrace[] traces = new XCMSTrace[numTraces];
        for (int i = 0; i < traces.length; i++) {
            XCMSPeak p = group.getPeaks().get(i);
            traces[i] = new XCMSTrace(p.getMz(), convertTime(p.getRtMin()), convertTime(p.getRtMax()), p.getMzMin(), p.getMzMax());
        }
        int charge = group.getCharge() == null ? CHARGE_UNKNOWN : group.getCharge();
        
        XCMSFeature feature = new XCMSFeature(traces, charge);
        feature.group = group;
        
        return feature;
    }
    
    private static double convertTime(double timeInSec) {
        return timeInSec; // changed back to no-op implementation
        //return timeInSec / 60d;
    }

    @Override
    public Color getColor() {
        return traces.length > 1 ? Color.MAGENTA : Color.RED;
    }
    
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
