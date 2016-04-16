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

import javax.swing.tree.TreeModel;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RowModel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesTreeTableModel {

    protected TreeModel treeModel;
    protected RowModel rowModel;
    protected OutlineModel outlineModel;

    public UmpireFeaturesTreeTableModel(TreeModel treeModel, RowModel rowModel) {
        this.treeModel = treeModel;
        this.rowModel = rowModel;
        outlineModel = DefaultOutlineModel.createOutlineModel(treeModel, rowModel);
    }

    public OutlineModel getOutlineModel() {
        return outlineModel;
    }
}
