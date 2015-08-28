/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain;

import MSUmpire.PSMDataStructure.PSM;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpirePSM {
    private float observedPrecursorMz;
    private byte charge;
    private float observedMass;
    private int scanNum;
    private double rt;

    private float bScore;
    private float yScore;
    private float hyperScore;
    private float expect;
    private float probability;

    public static UmpirePSM create(PSM psm) {
        UmpirePSM p = new UmpirePSM();
        p.observedPrecursorMz = psm.ObserPrecursorMz();
        p.charge = (byte) psm.Charge;
        p.observedMass = psm.ObserPrecursorMass;
        p.scanNum = psm.ScanNo;
        p.rt = psm.RetentionTime;

        p.bScore = psm.bscore;
        p.yScore = psm.yscore;
        p.hyperScore = psm.hyperscore;
        p.expect = psm.expect;
        p.probability = psm.Probability;

        return p;
    }

    public float getObservedPrecursorMz() {
        return observedPrecursorMz;
    }

    public void setObservedPrecursorMz(float observedPrecursorMz) {
        this.observedPrecursorMz = observedPrecursorMz;
    }

    public int getScanNum() {
        return scanNum;
    }

    public void setScanNum(int scanNum) {
        this.scanNum = scanNum;
    }

    public double getRt() {
        return rt;
    }

    public void setRt(double rt) {
        this.rt = rt;
    }

    public float getbScore() {
        return bScore;
    }

    public void setbScore(float bScore) {
        this.bScore = bScore;
    }

    public float getyScore() {
        return yScore;
    }

    public void setyScore(float yScore) {
        this.yScore = yScore;
    }

    public float getHyperScore() {
        return hyperScore;
    }

    public void setHyperScore(float hyperScore) {
        this.hyperScore = hyperScore;
    }

    public float getExpect() {
        return expect;
    }

    public void setExpect(float expect) {
        this.expect = expect;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public byte getCharge() {
        return charge;
    }

    public void setCharge(byte charge) {
        this.charge = charge;
    }

    public float getObservedMass() {
        return observedMass;
    }

    public void setObservedMass(float observedMass) {
        this.observedMass = observedMass;
    }

    
    
}
