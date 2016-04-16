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
package umich.ms.batmass.gui.core.components.featuretable;

import javax.swing.table.AbstractTableModel;

/**
 * Just a convenience class, so that you hopefully didn't forget that you can provide 
 * an improved table model, so that Map2D could be zoomed to your particular feature.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractFeatureTableModel extends AbstractTableModel implements FeatureTableModel {

    public AbstractFeatureTableModel() {
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not editable.");
    }
    
    
}
