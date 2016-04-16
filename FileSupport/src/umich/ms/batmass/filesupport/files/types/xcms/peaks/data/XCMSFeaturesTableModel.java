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
package umich.ms.batmass.filesupport.files.types.xcms.peaks.data;

import java.util.List;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.model.XCMSPeak;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.model.XCMSPeakGroup;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.components.featuretable.AbstractFeatureTableModel;

/**
 *
 * @author Dmitry Avtonomov
 */
class XCMSFeaturesTableModel extends AbstractFeatureTableModel {

   List<XCMSFeature> features;
    
    protected String[] colNames = {
        /* 0 */ "Mono m/z",
        /* 1 */ "m/z min",
        /* 2 */ "m/z max",
        /* 3 */ "Num isotopes",
        /* 4 */ "RT",
        /* 5 */ "RT min",
        /* 6 */ "RT max",
        /* 7 */ "Total intensity",
        /* 8 */ "Max intensity",
        /* 9 */ "pcgroup"
    };
    protected Class[] colTypes = {
        /* 0 */ Double.class,
        /* 1 */ Double.class,
        /* 2 */ Double.class,
        /* 3 */ Integer.class,
        /* 4 */ Double.class,
        /* 5 */ Double.class,
        /* 6 */ Double.class,
        /* 7 */ Double.class,
        /* 8 */ Double.class,
        /* 9 */ Integer.class,
    };
    
    public XCMSFeaturesTableModel(List<XCMSFeature> features) {
        this.features = features;
    }

    @Override
    public int getRowCount() {
        return features.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return colNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return colTypes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        XCMSFeature f = features.get(rowIndex);
        XCMSPeakGroup g = f.getGroup();
        XCMSPeak p = g.get(0);
        switch (columnIndex) {
            case 0:
                return p.getMz();
            case 1:
                return p.getMzMin();
            case 2:
                return g.get(g.size()-1).getMzMax();
            case 3:
                return g.size();
            case 4:
                return p.getRt();
            case 5:
                return p.getRtMin();
            case 6:
                return p.getRtMax();
            case 7:
                return p.getInto();
            case 8:
                return p.getMaxo();
            case 9:
                return p.getPcgroup();
        }
        return null;
    }

    @Override
    public MzRtRegion rowToRegion(int row) {
        if (row < 0 || row >= features.size())
            throw new IllegalStateException(String.format("Conversion from illegal row index was requested, no such row index: [%s]", row));
        XCMSFeature f = features.get(row);
        if (f == null) {
            throw new IllegalStateException(String.format("Should not happen, row index was ok, but the feature at this id was null."));
        }
        List<XCMSPeak> peaks = f.getGroup().getPeaks();
        double mzLo = Double.POSITIVE_INFINITY;
        double mzHi = Double.NEGATIVE_INFINITY;
        double rtLo = Double.POSITIVE_INFINITY;
        double rtHi = Double.NEGATIVE_INFINITY;
        for (XCMSPeak peak : peaks) {
            if (peak.getMzMin() < mzLo)
                mzLo = peak.getMzMin();
            if (peak.getMzMax() > mzHi)
                mzHi = peak.getMzMax();
            if (peak.getRtMin() < rtLo)
                rtLo = peak.getRtMin();
            if (peak.getRtMax() > rtHi)
                rtHi = peak.getRtMax();
        }
        return new MzRtRegion(mzLo-0.5, mzHi+0.5, rtLo-0.5, rtHi+0.5);
    }
    
    
}
