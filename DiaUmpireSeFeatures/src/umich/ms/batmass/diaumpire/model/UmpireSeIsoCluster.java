/*
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umich.ms.batmass.diaumpire.model;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireSeIsoCluster {
    protected double rtLo;
    protected double rtHi;
    protected int scanNumLo;
    protected int scanNumHi;
    protected int charge;
    protected double[] mz = new double[4];
    protected double peakHeight;
    protected double peakArea;

    /**
     * Bare minimum info required to plot something.
     * @param rtLo
     * @param rtHi
     * @param mz
     */
    public UmpireSeIsoCluster(double rtLo, double rtHi, double mz) {
        this.rtLo = rtLo;
        this.rtHi = rtHi;
        this.mz[0] = mz;
    }

    public UmpireSeIsoCluster() {
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

    public int getScanNumLo() {
        return scanNumLo;
    }

    public void setScanNumLo(int scanNumLo) {
        this.scanNumLo = scanNumLo;
    }

    public int getScanNumHi() {
        return scanNumHi;
    }

    public void setScanNumHi(int scanNumHi) {
        this.scanNumHi = scanNumHi;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public double[] getMz() {
        return mz;
    }

    public void setMz(double[] mz) {
        this.mz = mz;
    }
    
    public void setMz(int index, double value) {
        mz[index] = value;
    }

    public double getPeakHeight() {
        return peakHeight;
    }

    public void setPeakHeight(double peakHeight) {
        this.peakHeight = peakHeight;
    }

    public double getPeakArea() {
        return peakArea;
    }

    public void setPeakArea(double peakArea) {
        this.peakArea = peakArea;
    }
    
    /**
     * Counts the number of peaks for which the m/z value is not zero.
     * @return 
     */
    public int getNumPeaks() {
        int numPeaks = 0;
        for (int i = 0; i < mz.length; i++) {
            if (mz[i] > 0)
                numPeaks++;
        }
        return numPeaks;
    }
}
