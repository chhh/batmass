/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
