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
package umich.ms.batmass.filesupport.files.types.xcms.peaks.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A group of isotopic peaks. If there is just a single peak, then groupId and
 * charge are both null.
 * @author Dmitry Avtonomov
 */
public class XCMSPeakGroup {
    List<XCMSPeak> peaks;
    double mzLo;
    double mzHi;
    Integer charge = null;
    Integer xcmsIsotopeGroupId = null;

    public XCMSPeakGroup(Integer charge, Integer xcmsIsotopeGroupId) {
        this.peaks = new ArrayList<>(1);
        this.charge = charge;
        this.xcmsIsotopeGroupId = xcmsIsotopeGroupId;
    }

    public int size() {
        return peaks.size();
    }

    public boolean isEmpty() {
        return peaks.isEmpty();
    }

    public Iterator<XCMSPeak> iterator() {
        return peaks.iterator();
    }

    public XCMSPeak get(int index) {
        return peaks.get(index);
    }

    public boolean add(XCMSPeak xcmsPeak) {
        return peaks.add(xcmsPeak);
    }

    public void add(int index, XCMSPeak element) {
        peaks.add(index, element);
    }

    /**
     * Do not modify the returned list!
     * @return 
     */
    public List<XCMSPeak> getPeaks() {
        return peaks;
    }

    public void setPeaks(List<XCMSPeak> peaks) {
        this.peaks = peaks;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Integer getXcmsIsotopeGroupId() {
        return xcmsIsotopeGroupId;
    }

    public void setXcmsIsotopeGroupId(Integer xcmsIsotopeGroupId) {
        this.xcmsIsotopeGroupId = xcmsIsotopeGroupId;
    }
}
