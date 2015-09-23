/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.agilent.cef.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * One entry in Agilent .cef file, created by Molecular Feature Extractor algorithm.
 * @author Dmitry Avtonomov
 */
public class AgilentCompound {
    protected double mass;
    /** RT in minutes. */
    protected double rt;
    protected int abMax;
    protected int abTot;

    protected double rtLo;
    protected double rtHi;
    List<AgilentMSPeak> peaks;

    public AgilentCompound() {
        peaks = new ArrayList(2);
    }

    public boolean addAll(Collection<? extends AgilentMSPeak> c) {
        return peaks.addAll(c);
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRt() {
        return rt;
    }

    public void setRt(double rt) {
        this.rt = rt;
    }

    public int getAbMax() {
        return abMax;
    }

    public void setAbMax(int abMax) {
        this.abMax = abMax;
    }

    public int getAbTot() {
        return abTot;
    }

    public void setAbTot(int abTot) {
        this.abTot = abTot;
    }

    public List<AgilentMSPeak> getPeaks() {
        return peaks;
    }

    public double getRtLo() {
        return rtLo;
    }

    public void setRtLo(double rtLo) {
        this.rtLo = rtLo;
    }

    public double getRtHi() {
        return rtHi;
    }

    public void setRtHi(double rtHi) {
        this.rtHi = rtHi;
    }

    public int size() {
        return peaks.size();
    }

    public boolean isEmpty() {
        return peaks.isEmpty();
    }

    public boolean add(AgilentMSPeak e) {
        return peaks.add(e);
    }

    public AgilentCompound cloneWithoutPeaks() {
        AgilentCompound c = new AgilentCompound();
        c.setAbMax(abMax);
        c.setAbTot(abTot);
        c.setMass(mass);
        c.setRt(rt);
        c.setRtHi(rtHi);
        c.setRtLo(rtLo);
        return c;
    }
}
