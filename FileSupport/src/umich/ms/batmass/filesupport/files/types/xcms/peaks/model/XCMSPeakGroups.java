/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Dmitry Avtonomov
 */
public class XCMSPeakGroups {
    List<XCMSPeakGroup> groups;

    public static final String RE_GRP_CLUSTERID = "clusterId";
    public static final String RE_GRP_ISOTOPENUM = "isotopeNumber";
    public static final String RE_GRP_CHARGECNT = "chargeCount";
    public static final String RE_GRP_CHARGESIGN = "chargeSign";
    public static final Pattern RE_XCMS_ISOTOPES = Pattern.compile(String.format(
            "\\[(?<%1$s>\\d+?)\\]\\[M\\+?(?<%2$s>\\d*?)\\](?<%3$s>\\d*?)(?<%4$s>\\+|\\-)",
            RE_GRP_CLUSTERID, RE_GRP_ISOTOPENUM, RE_GRP_CHARGECNT, RE_GRP_CHARGESIGN));

    public XCMSPeakGroups(List<XCMSPeakGroup> groups) {
        this.groups = groups;
    }

    public XCMSPeakGroups() {
        this.groups = new ArrayList<>();
    }

    public int size() {
        return groups.size();
    }

    public boolean add(XCMSPeakGroup xcmsPeakGroup) {
        return groups.add(xcmsPeakGroup);
    }

    public void add(int index, XCMSPeakGroup element) {
        groups.add(index, element);
    }

    public XCMSPeakGroup get(int index) {
        return groups.get(index);
    }

    public Iterator<XCMSPeakGroup> iterator() {
        return groups.iterator();
    }

    public List<XCMSPeakGroup> getGroups() {
        return groups;
    }

    public static XCMSPeakGroups create(XCMSPeaks peaks) {
        TreeMap<Integer, XCMSPeakGroup> map = new TreeMap<>();
        XCMSPeakGroups groups = new XCMSPeakGroups();

        for (XCMSPeak peak : peaks.getPeaks()) {
            Matcher m = RE_XCMS_ISOTOPES.matcher(peak.getIsotopes());
            String clusterId, isotopeNumber, chargeCount, chargeSign;
            int id, zSign, isoNum, z;
            XCMSPeakGroup group;
            if (m.find()) {
                // this line in csv file had info about isotopes
                clusterId = m.group(RE_GRP_CLUSTERID);
                isotopeNumber = m.group(RE_GRP_ISOTOPENUM);
                chargeCount = m.group(RE_GRP_CHARGECNT);
                chargeSign = m.group(RE_GRP_CHARGESIGN);

                id = Integer.parseInt(clusterId);
                zSign = "+".equals(chargeSign) ? 1 : -1;
                isoNum = isotopeNumber.isEmpty() ? 0 : Integer.parseInt(isotopeNumber);
                z = chargeCount.isEmpty() ? 1 : Integer.parseInt(chargeCount);

                peak.setIsotopeNum(isoNum);

                group = map.get(id);
                if (group == null) {
                    group = new XCMSPeakGroup(z * zSign, id);
                    groups.add(group);
                    map.put(id, group);
                }
                group.add(peak);
            } else {
                group = new XCMSPeakGroup(null, null);
                group.add(peak);
                groups.add(group);
            }
        }

        // sort clusters with isotopes by isotope number or mass
        Set<Map.Entry<Integer, XCMSPeakGroup>> entrySet = map.entrySet();
        for (Map.Entry<Integer, XCMSPeakGroup> entry : entrySet) {
            XCMSPeakGroup group = entry.getValue();
            List<XCMSPeak> peaksInGroup = group.getPeaks();
            Collections.sort(peaksInGroup, new Comparator<XCMSPeak>() {
                @Override public int compare(XCMSPeak o1, XCMSPeak o2) {
                    return Double.compare(o1.mzMin, o2.mzMin);
                }
            });
        }
        
        return groups;
    }
}
