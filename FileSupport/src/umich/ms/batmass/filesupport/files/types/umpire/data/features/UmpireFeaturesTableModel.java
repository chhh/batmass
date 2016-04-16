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

import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesTableModel extends AbstractTableModel {
    
    protected List<UmpireFeature> list;

    public UmpireFeaturesTableModel(List<UmpireFeature> list) {
        this.list = list;
    }


    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "m/z mono";
            case 1:
                return "charge";
            case 2:
                return "m/z lo";
            case 3:
                return "m/z hi";
            case 4:
                return "RT lo";
            case 5:
                return "RT hi";
            case 6:
                return "Identified";
        }
        return "UKNOWN COLUMN";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        switch (columnIndex) {
            case 0:
                return Double.class;
            case 1:
                return Integer.class;
            case 2:
            case 3:
            case 4:
            case 5:
                return Double.class;
            case 6:
                return Boolean.class;
        }

        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        UmpireFeature f = list.get(rowIndex);

        Rectangle2D box = f.getBounds();
        switch (columnIndex) {
            case 0:
                return f.getTraces()[0].getMz();
            case 1:
                return f.getCharge() == UmpireFeature.CHARGE_UNKNOWN ? null : f.getCharge();
            case 2:
                return box.getMinX();
            case 3:
                return box.getMaxX();
            case 4:
                return box.getMinY(); // coordinates start at upper left corner
            case 5:
                return box.getMaxY();
            case 6:
                return f.getIsIdentified();
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not editable");
    }
}
