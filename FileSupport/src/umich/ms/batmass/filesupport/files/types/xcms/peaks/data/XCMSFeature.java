/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks.data;

import java.awt.Color;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSFeature2D;
import static umich.ms.batmass.data.core.lcms.features.ILCMSFeature2D.CHARGE_UNKNOWN;
import umich.ms.batmass.filesupport.files.types.umpire.data.features.UmpireFeature;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.data.model.XCMSPeak;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.data.model.XCMSPeakGroup;

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
    
    public static XCMSFeature create(XCMSPeakGroup group) {
         int numTraces = group.size();

        XCMSTrace[] traces = new XCMSTrace[numTraces];
        for (int i = 0; i < traces.length; i++) {
             XCMSPeak p = group.getPeaks().get(i);
            traces[i] = new XCMSTrace(p.getMz(), p.getRtMin(), p.getRtMax(), p.getMzMin(), p.getMzMax());
        }
        int charge = group.getCharge() == null ? CHARGE_UNKNOWN : group.getCharge();
        
        XCMSFeature feature = new XCMSFeature(traces, charge);
        
        return feature;
    }

    @Override
    public Color getColor() {
        return traces.length > 1 ? Color.GREEN : Color.RED;
    }

}
