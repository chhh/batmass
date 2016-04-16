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
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeatureBean {
    private double mzMono;
    private int charge;
    private double mzLo;
    private double mzHi;
    private double rtLo;
    private double rtHi;
    private boolean identified;
    private String name;

    public String getName() {
        return name;
    }

//    public void setName(String name) {
//        this.name = name;
//    }

    public UmpireFeatureBean() {
    }

    public UmpireFeatureBean(double mzMono, int charge, double mzLo, double mzHi, double rtLo, double rtHi, boolean identified) {
        this.mzMono = mzMono;
        this.charge = charge;
        this.mzLo = mzLo;
        this.mzHi = mzHi;
        this.rtLo = rtLo;
        this.rtHi = rtHi;
        this.identified = identified;
        this.name = String.format("%.4f[%+d]@%.2f-%.2f", mzMono, charge, rtLo, rtHi);
    }

    public UmpireFeatureBean(UmpireFeature f) {
        this.mzMono = f.getTraces()[0].getMz();
        this.charge = f.getCharge();
        this.mzLo = f.getBounds().getMinX();
        this.mzHi = f.getBounds().getMaxX();
        this.rtLo = f.getBounds().getMaxY();
        this.rtHi = f.getBounds().getMinY();
        this.identified = f.isIsIdentified();
        this.name = String.format("%.4f[%+d]@%.2f-%.2f", mzMono, charge, rtLo, rtHi);
    }



    public double getMzMono() {
        return mzMono;
    }

//    public void setMzMono(double mzMono) {
//        this.mzMono = mzMono;
//    }

    public int getCharge() {
        return charge;
    }

//    public void setCharge(int charge) {
//        this.charge = charge;
//    }

    public double getMzLo() {
        return mzLo;
    }

//    public void setMzLo(double mzLo) {
//        this.mzLo = mzLo;
//    }

    public double getMzHi() {
        return mzHi;
    }

//    public void setMzHi(double mzHi) {
//        this.mzHi = mzHi;
//    }

    public double getRtLo() {
        return rtLo;
    }

//    public void setRtLo(double rtLo) {
//        this.rtLo = rtLo;
//    }

    public double getRtHi() {
        return rtHi;
    }

//    public void setRtHi(double rtHi) {
//        this.rtHi = rtHi;
//    }

    public boolean isIdentified() {
        return identified;
    }

//    public void setIdentified(boolean identified) {
//        this.identified = identified;
//    }

}
