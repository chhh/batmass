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
public class UmpireIsoCluster {
    protected double rtLo;
    protected double rtHi;
    protected int scanNumLo;
    protected int scanNumHi;
    protected int charge;
    protected double mz1;
    protected double mz2;
    protected double mz3;
    protected double mz4;
    protected double peakHeight;
    protected double peakArea;

    /**
     * Bare minimum info required to plot something.
     * @param rtLo
     * @param rtHi
     * @param mz1 
     */
    public UmpireIsoCluster(double rtLo, double rtHi, double mz1) {
        this.rtLo = rtLo;
        this.rtHi = rtHi;
        this.mz1 = mz1;
    }

    public UmpireIsoCluster() {
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

    public double getMz1() {
        return mz1;
    }

    public void setMz1(double mz1) {
        this.mz1 = mz1;
    }

    public double getMz2() {
        return mz2;
    }

    public void setMz2(double mz2) {
        this.mz2 = mz2;
    }

    public double getMz3() {
        return mz3;
    }

    public void setMz3(double mz3) {
        this.mz3 = mz3;
    }

    public double getMz4() {
        return mz4;
    }

    public void setMz4(double mz4) {
        this.mz4 = mz4;
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
    
    
}
