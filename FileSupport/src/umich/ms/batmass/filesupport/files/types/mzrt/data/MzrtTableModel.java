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
package umich.ms.batmass.filesupport.files.types.mzrt.data;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVRecord;
import umich.ms.batmass.filesupport.files.types.mzrt.model.MzrtBox;
import umich.ms.batmass.filesupport.files.types.mzrt.model.MzrtFile;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.components.featuretable.AbstractFeatureTableModel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MzrtTableModel extends AbstractFeatureTableModel {
    private MzrtFeatures f;
    private String[] colNames;

    public MzrtTableModel(MzrtFeatures f) {
        this.f = f;
        MzrtFile file = f.getFile();
        Map<String, Integer> header = file.getHeader();
        colNames = new String[header.size()];
        for (Map.Entry<String, Integer> entry : header.entrySet()) {
            colNames[entry.getValue()] = entry.getKey();
        }
    }
    
    @Override
    public int getRowCount() {
        return f.getMs1().getList().size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        List<CSVRecord> records = f.getFile().getRecords();
        //MzrtFeature feat = f.getMs1().getList().get(rowIndex);
        if (rowIndex < 0 || rowIndex >= records.size())
            return null;
        
        MzrtFile file = f.getFile();
        int[] idxs = file.getIndexesMzRtColorOpacity();
        String str = f.getFile().getRecords().get(rowIndex).get(columnIndex);
        
        for (int i = 0; i < 4; i++) {
            if (columnIndex == idxs[i])
                return Double.parseDouble(str);
        }
        if (columnIndex == idxs[4])
            return Color.decode(str);
        
        if (columnIndex == idxs[5])
            return Float.parseFloat(str);
        
        return str;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return colNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        MzrtFile file = f.getFile();
        int[] idxs = file.getIndexesMzRtColorOpacity();
        for (int i = 0; i < 4; i++) {
            if (columnIndex == idxs[i])
                return Double.class;
        }
        
        if (columnIndex == idxs[4])
            return Color.class;
        
        if (columnIndex == idxs[5])
            return Float.class;
        
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public MzRtRegion rowToRegion(int row) {
        List<MzrtFeature> list = f.getMs1().getList();
        if (row < 0 || row >= list.size())
            throw new IllegalStateException(String.format("Conversion from illegal row index was requested, no such row index: [%s]", row));
        MzrtFeature f = list.get(row);
        if (f == null) {
            throw new IllegalStateException(String.format("Should not happen, row index was ok, but the feature at this id was null."));
        }
        MzrtBox[] traces = f.getTraces();
        double mzLo = Double.POSITIVE_INFINITY;
        double mzHi = Double.NEGATIVE_INFINITY;
        double rtLo = Double.POSITIVE_INFINITY;
        double rtHi = Double.NEGATIVE_INFINITY;
        for (MzrtBox trace : traces) {
            if (trace.getMzLo() < mzLo)
                mzLo = trace.getMzLo();
            if (trace.getMzHi() > mzHi)
                mzHi = trace.getMzHi();
            if (trace.getRtLo() < rtLo)
                rtLo = trace.getRtLo();
            if (trace.getRtHi() > rtHi)
                rtHi = trace.getRtHi();
        }
        
        return new MzRtRegion(mzLo - 0.5, mzHi + 0.5, rtLo - 0.5, rtHi + 0.5);
    }
    
}
