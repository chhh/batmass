/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks.data;

import java.awt.geom.Rectangle2D;
import umich.ms.batmass.data.core.lcms.features.AbstractShapedLCMSTrace;

/**
 *
 * @author Dmitry Avtonomov
 */
public class XCMSTrace extends AbstractShapedLCMSTrace {
    double mzLo;
    double mzHi;

    public XCMSTrace(double mz, double rtLo, double rtHi, double mzLo, double mzHi) {
        super(mz, rtLo, rtHi);
        this.mzLo = mzLo;
        this.mzHi = mzHi;
        this.spread = mzHi - mzLo;
        this.shape = new Rectangle2D.Double(mzLo, rtHi, mzHi-mzLo, rtHi-rtLo);
    }
    
}
