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
package umich.ms.batmass.diaumpire.data;

import java.util.List;
import umich.ms.batmass.diaumpire.model.UmpireSeIsoCluster;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.components.featuretable.AbstractFeatureTableModel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireSeTableModel extends AbstractFeatureTableModel {
    List<UmpireSeFeature> features;
    
    protected String[] colNames = {
        /* 0 */ "Mono m/z",
        /* 1 */ "Charge",
        /* 2 */ "RT lo",
        /* 3 */ "RT hi",
        /* 4 */ "Scan lo",
        /* 5 */ "Scan hi",
        /* 6 */ "Total intensity",
        /* 7 */ "Max intensity",
    };
    protected Class[] colTypes = {
        /* 0 */ Double.class,
        /* 1 */ Integer.class,
        /* 2 */ Double.class,
        /* 3 */ Double.class,
        /* 4 */ Integer.class,
        /* 5 */ Integer.class,
        /* 6 */ Double.class,
        /* 7 */ Double.class,
    };

    public UmpireSeTableModel(List<UmpireSeFeature> features) {
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
        UmpireSeFeature f = features.get(rowIndex);
        UmpireSeIsoCluster c = f.getCluster();
        
        switch (columnIndex) {
            case 0:
                return c.getMz()[0];
            case 1:
                return c.getCharge();
            case 2:
                return c.getRtLo();
            case 3:
                return c.getRtHi();
            case 4:
                return c.getScanNumLo();
            case 5:
                return c.getScanNumHi();
            case 6:
                return c.getPeakArea();
            case 7:
                return c.getPeakHeight();
        }
        return null;
    }

    @Override
    public MzRtRegion rowToRegion(int row) {
        if (row < 0 || row >= features.size())
            throw new IllegalStateException(String.format("Conversion from illegal row index was requested, no such row index: [%s]", row));
        UmpireSeFeature f = features.get(row);
        UmpireSeIsoCluster c = f.getCluster();
        if (f == null) {
            throw new IllegalStateException(String.format("Should not happen, row index was ok, but the feature at this id was null."));
        }
        
        // find the boundaries
        double mzLo = Double.POSITIVE_INFINITY;
        double mzHi = Double.NEGATIVE_INFINITY;
        double[] masses = c.getMz();
        for (int i = 0; i < c.getNumPeaks(); i++) {
            double m = masses[i];
            if (m < mzLo)
                mzLo = m;
            if (m > mzHi)
                mzHi = m;
        }
        double rtLo = c.getRtLo();
        double rtHi = c.getRtHi();
        
        // add half a dalton to the sides and 30 seconds each way in RT direction
        return new MzRtRegion(mzLo-0.5, mzHi+0.5, rtLo-0.5, rtHi+0.5);
    }
}