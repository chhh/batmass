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
package umich.ms.batmass.filesupport.files.types.agilent.cef.data;

import java.util.List;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSTrace;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentCompound;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentMSPeak;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.IonId;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.components.featuretable.AbstractFeatureTableModel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class AgilentCefTableModel extends AbstractFeatureTableModel {
    List<AgilentCefFeature> features;

    protected String[] colNames = {
        /* 0 */ "Mono m/z",
        /* 1 */ "Num isotopes",
        /* 2 */ "Charge",
        /* 3 */ "RT",
        /* 4 */ "RT min",
        /* 5 */ "RT max",
        /* 6 */ "Total intensity",
        /* 7 */ "Max intensity",
        /* 8 */ "Adduct",
        /* 9 */ "Z Carrier"
    };
    protected Class[] colTypes = {
        /* 0 */ Double.class,
        /* 1 */ Integer.class,
        /* 2 */ Integer.class,
        /* 3 */ Double.class,
        /* 4 */ Double.class,
        /* 5 */ Double.class,
        /* 6 */ Integer.class,
        /* 7 */ Integer.class,
        /* 8 */ String.class,
        /* 9 */ String.class
    };

    public AgilentCefTableModel(List<AgilentCefFeature> features) {
        this.features = features;
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        AgilentCefFeature f = features.get(rowIndex);
        AgilentCompound ac = f.getCompund();
        List<AgilentMSPeak> peaks = ac.getPeaks();
        if (peaks.isEmpty()) {
            return null;
        }
        AgilentMSPeak p = peaks.get(0);
        switch (columnIndex) {
            case 0:
                return p.getMz();
            case 1:
                return ac.getPeaks().size();
            case 2:
                return p.getZ();
            case 3:
                return ac.getRt();
            case 4:
                return ac.getRtLo();
            case 5:
                return ac.getRtHi();
            case 6:
                return ac.getAbTot();
            case 7:
                return ac.getAbMax();
            case 8:
                IonId id = p.getIonId();
                return id == null ? "" : id.getAdduct();
            case 9:
                id = p.getIonId();
                return id == null ? "" : id.getzCarrier();
        }
        return null;
    }

    @Override
    public MzRtRegion rowToRegion(int row) {
        if (row < 0 || row >= features.size())
            throw new IllegalStateException(String.format("Conversion from illegal row index was requested, no such row index: [%s]", row));
        AgilentCefFeature f = features.get(row);
        if (f == null) {
            throw new IllegalStateException(String.format("Should not happen, row index was ok, but the feature at this id was null."));
        }

        double mzLo = Double.POSITIVE_INFINITY;
        double mzHi = Double.NEGATIVE_INFINITY;
        double rtLo = Double.POSITIVE_INFINITY;
        double rtHi = Double.NEGATIVE_INFINITY;
        AbstractLCMSTrace[] traces = f.getTraces();
        for (AbstractLCMSTrace trace : traces) {
            if (trace.getMz() - trace.getMzSpread() < mzLo)
                mzLo = trace.getMz() - trace.getMzSpread();
            if (trace.getMz() + trace.getMzSpread() > mzHi)
                mzHi = trace.getMz() + trace.getMzSpread();
            if (trace.getRtLo() < rtLo)
                rtLo = trace.getRtLo();
            if (trace.getRtHi() > rtHi)
                rtHi = trace.getRtHi();
        }

        return new MzRtRegion(mzLo - 0.5, mzHi + 0.5, rtLo - 0.5, rtHi + 0.5);
    }
}
