/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.agilent.cef.model;

/**
 *
 * @author Dmitry Avtonomov
 */
public class AgilentMSPeak {
    public static final int CHARGE_UNKNOWN = Integer.MIN_VALUE;
    
    protected double rt;
    protected double mz;
    protected double abMax;
    protected double abTot;
    protected int z = CHARGE_UNKNOWN;
    protected String ionDescription = "";

    public double getRt() {
        return rt;
    }

    public void setRt(double rt) {
        this.rt = rt;
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        this.mz = mz;
    }

    public double getAbMax() {
        return abMax;
    }

    public void setAbMax(double abMax) {
        this.abMax = abMax;
    }

    public double getAbTot() {
        return abTot;
    }

    public void setAbTot(double abTot) {
        this.abTot = abTot;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getIonDescription() {
        return ionDescription;
    }

    public void setIonDescription(String ionDescription) {
        this.ionDescription = ionDescription;
    }
}
