/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks.model;

/**
 *
 * @author Dmitry Avtonomov
 */
public class XCMSPeak {
    protected int rowNum;
    protected double mz;
    protected double mzMin;
    protected double mzMax;
    protected double rt;
    protected double rtMin;
    protected double rtMax;
    protected double into;
    protected double maxo;
    protected String sample;
    protected String isotopes = "";
    protected String adduct = "";
    protected int pcgroup = -1;

    /** Values <0 mean unknown. */
    protected int isotopeNum = -1;

    @Override
    public String toString() {
        return "XCMSPeak{" +
                "rowNum=" + rowNum +
                ", mz=" + mz +
                ", rt=" + rt +
                ", into=" + into +
                ", isotopes='" + isotopes + '\'' +
                ", pcgroup=" + pcgroup +
                '}';
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        this.mz = mz;
    }

    public double getMzMin() {
        return mzMin;
    }

    public void setMzMin(double mzMin) {
        this.mzMin = mzMin;
    }

    public double getMzMax() {
        return mzMax;
    }

    public void setMzMax(double mzMax) {
        this.mzMax = mzMax;
    }

    public double getRt() {
        return rt;
    }

    /**
     * @param rt in seconds, as is the default XCMS output.
     */
    public void setRt(double rt) {
        this.rt = rt / 60d;
    }

    public double getRtMin() {
        return rtMin;
    }

    /**
     * @param rtMin in seconds, as is the default XCMS output.
     */
    public void setRtMin(double rtMin) {
        this.rtMin = rtMin / 60d;
    }

    public double getRtMax() {
        return rtMax;
    }

    /**
     * @param rtMax in seconds, as is the default XCMS output.
     */
    public void setRtMax(double rtMax) {
        this.rtMax = rtMax / 60d;
    }

    public double getInto() {
        return into;
    }

    public void setInto(double into) {
        this.into = into;
    }

    public double getMaxo() {
        return maxo;
    }

    public void setMaxo(double maxo) {
        this.maxo = maxo;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getIsotopes() {
        return isotopes;
    }

    public void setIsotopes(String isotopes) {
        this.isotopes = isotopes;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public int getPcgroup() {
        return pcgroup;
    }

    public void setPcgroup(int pcgroup) {
        this.pcgroup = pcgroup;
    }

    public int getIsotopeNum() {
        return isotopeNum;
    }

    public void setIsotopeNum(int isotopeNum) {
        this.isotopeNum = isotopeNum;
    }
}
