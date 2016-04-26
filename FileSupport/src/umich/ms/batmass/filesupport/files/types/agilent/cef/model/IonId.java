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
package umich.ms.batmass.filesupport.files.types.agilent.cef.model;

/**
 * @author Dmitry Avtonomov
 */
public class IonId {
    public static final int CHARGE_UNKNOWN = Integer.MIN_VALUE;
    public String molId = "";
    public int mCount = 1;
    public int z = CHARGE_UNKNOWN;
    public String zCarrier;
    public String adduct = "";
    int isotopeNumber = 0;

    public int getmCount() {
        return mCount;
    }

    public String getMolId() {
        return molId;
    }

    public void setMolId(String molId) {
        this.molId = molId;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getzCarrier() {
        return zCarrier;
    }

    public void setzCarrier(String zCarrier) {
        this.zCarrier = zCarrier;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public int getIsotopeNumber() {
        return isotopeNumber;
    }

    public void setIsotopeNumber(int isotopeNumber) {
        this.isotopeNumber = isotopeNumber;
    }
}
