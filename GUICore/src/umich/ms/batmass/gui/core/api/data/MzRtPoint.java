/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api.data;

import java.awt.Point;

/**
 * Just an m/z and retention time pair. RT is in minutes.
 * Supposed to be immutable, but for convenience extends Point.Double: to use
 * it's methods and for easy compatibility with {@link MzRtRegion} - you can
 * check if the point is in a region for example.
 * Don't try to use Point.Double methods to set values
 * like <code>this.x</code> and do not use methods
 * like {@link #setLocation(double, double) }.
 */
public class MzRtPoint extends Point.Double {

    /**
     *
     * @param mz
     * @param rt in minutes
     */
    public MzRtPoint(double mz, double rt) {
        super(mz, rt);
    }

    /** copy constructor
     * @param orig */
    public MzRtPoint(MzRtPoint orig) {
        super(orig.getMz(), orig.getRt());
    }

    public double getMz() {
        return x;
    }

    /** In minutes
     * @return  */
    public double getRt() {
        return y;
    }

    public void setMz(double mz) {
        this.x = mz;
    }

    /** In minutes
     * @param rt */
    public void setRt(double rt) {
        this.y = rt;
    }

}
