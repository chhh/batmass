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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Dmitry Avtonomov
 */
public class AgilentMSPeak implements Comparable<AgilentMSPeak> {
    public static final int CHARGE_UNKNOWN = Integer.MIN_VALUE;
    public static String GRP_MOL_IDENTITY = "grpId";
    public static String GRP_M_COUNT = "grpMcnt";
    public static String GRP_Z_SIGN = "grpZsgn";
    public static String GRP_Z_COUNT = "grpZcnt";
    public static String GRP_Z_CARRIER = "grpZcrr";
    public static String GRP_ADDUCT = "grpAdd";
    public static String GRP_ISOTOPE_NUM = "grpIsoN";

    // this works
//    public static Pattern RE_PEAK_DESCRIPTION = Pattern.compile(String.format(
//            "(\\d*M(?:\\+|-)(\\d*)(\\w+)(?:\\+\\[[\\w\\+\\-]+?\\])?(?:\\+\\d*)?)",
//            GRP_MOL_IDENTITY));
    public static Pattern RE_PEAK_DESCRIPTION = Pattern.compile(String.format(
            "(?<%1$s>(?<%2$s>\\d*)M(?<%3$s>\\+|-)(?<%4$s>\\d*)(?<%5$s>\\w+)(?:\\+(?<%6$s>\\[[\\w\\+\\-]+?\\]))?)(?:\\+(?<%7$s>\\d*))?",
            GRP_MOL_IDENTITY, GRP_M_COUNT, GRP_Z_SIGN, GRP_Z_COUNT, GRP_Z_CARRIER, GRP_ADDUCT, GRP_ISOTOPE_NUM));

    protected double rt;
    protected double mz;
    protected double abMax;
    protected double abTot;
    protected int z = CHARGE_UNKNOWN;
    protected String ionDescription = "";
    IonId ionId = null;

    public IonId getIonId() {
        return ionId;
    }

    public void setIonId(IonId ionId) {
        this.ionId = ionId;
    }
    
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

    public IonId parseIonSignature() {
        Matcher m = RE_PEAK_DESCRIPTION.matcher(ionDescription);
        String g;
        if (m.find()) {
            IonId id = new IonId();
            g = m.group(GRP_MOL_IDENTITY);
            id.setMolId(g);
            g = m.group(GRP_M_COUNT);
            if (g != null && !g.isEmpty()) {
                id.setmCount(Integer.parseInt(g));
            }
            g = m.group(GRP_ADDUCT);
            if (g != null) {
                id.setAdduct(g);
            }
            g = m.group(GRP_ISOTOPE_NUM);
            if (g != null) {
                int isoNum = g.isEmpty() ? 0 : Integer.parseInt(g);
                id.setIsotopeNumber(isoNum);
            }
            g = m.group(GRP_Z_COUNT);
            int z = g.isEmpty() ? 1: Integer.parseInt(g);
            g = m.group(GRP_Z_SIGN);
            int zSgn = "+".equals(g) ? 1 : -1;
            z = z * zSgn;
            id.setZ(z);
            g = m.group(GRP_Z_CARRIER);
            id.setzCarrier(g);
            return id;
        }
        return null;
    }

    @Override
    public int compareTo(AgilentMSPeak o) {
        return Double.compare(mz, o.mz);
    }
}
