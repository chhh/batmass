/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        return timeInSec / 60d;
    }

    @Override
    public Color getColor() {
        return traces.length > 1 ? Color.GREEN : Color.RED;
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

}
