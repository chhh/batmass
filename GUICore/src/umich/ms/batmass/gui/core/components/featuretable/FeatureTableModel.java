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

import javax.swing.table.TableModel;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;

/**
 *
 * @author Dmitry Avtonomov
 */
public interface FeatureTableModel extends TableModel {
    /**
     * This is used to convert a row from the table to an mz-rt region on the 2D map,
     * so that it can be zoomed in automatically on double-click.
     * @param row
     * @return null, if you don't want to have this functionality implemented
     */
    MzRtRegion rowToRegion(int row);
}
