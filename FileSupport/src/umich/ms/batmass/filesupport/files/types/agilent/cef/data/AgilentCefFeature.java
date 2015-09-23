/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.agilent.cef.data;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSFeature2D;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSTrace;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentCompound;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentMSPeak;

/**
 *
 * @author Dmitry Avtonomov
 */
public class AgilentCefFeature extends AbstractLCMSFeature2D<AbstractLCMSTrace>{
    protected AgilentCompound compund;

    public AgilentCefFeature(AbstractLCMSTrace[] traces, int charge) {
        super(traces, charge);
    }

    public AgilentCefFeature(AbstractLCMSTrace[] traces) {
        super(traces);
    }

    public AgilentCompound getCompund() {
        return compund;
    }

    public void setCompund(AgilentCompound compund) {
        this.compund = compund;
    }
    
    public static AgilentCefFeature create(AgilentCompound ac) {
        int numTraces = ac.size();
        
        AbstractLCMSTrace[] tr = new AbstractLCMSTrace[numTraces];
        for (int i = 0; i < tr.length; i++) {
            AgilentMSPeak peak = ac.getPeaks().get(i);
            tr[i] = new AbstractLCMSTrace(peak.getMz(), ac.getRtLo(), ac.getRtHi());
        }
        
        int z = ac.getPeaks().get(0).getZ();
        if (z == AgilentMSPeak.CHARGE_UNKNOWN) {
            z = CHARGE_UNKNOWN;
        }
        
        AgilentCefFeature acf = new AgilentCefFeature(tr, z);
        acf.setCompund(ac);
        return acf;
    }
    
    @Override
    public Color getColor() {
        return compund.size() > 1 ? Color.MAGENTA : Color.RED;
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
}
