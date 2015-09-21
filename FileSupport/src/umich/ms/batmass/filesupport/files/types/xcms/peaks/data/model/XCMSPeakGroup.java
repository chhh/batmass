/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks.data.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A group of isotopic peaks. If there is just a single peak, then groupId and
 * charge are both null.
 * @author Dmitry Avtonomov
 */
public class XCMSPeakGroup {
    List<XCMSPeak> peaks;
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

    public boolean addAll(Collection<? extends XCMSPeak> c) {
        return peaks.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends XCMSPeak> c) {
        return peaks.addAll(index, c);
    }

    public boolean add(XCMSPeak xcmsPeak) {
        return peaks.add(xcmsPeak);
    }

    public void add(int index, XCMSPeak element) {
        peaks.add(index, element);
    }

    public XCMSPeak remove(int index) {
        return peaks.remove(index);
    }

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
