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
package umich.ms.batmass.filesupport.files.types.umpireid.data;

import umich.ms.batmass.filesupport.files.types.umpireid.data.tabledomain.UmpireIdOutlineModel;
import umich.ms.batmass.filesupport.files.types.umpireid.data.tabledomain.UmpireIdTreeModel;
import umich.ms.batmass.filesupport.files.types.umpireid.data.tabledomain.UmpireIdRowModel;
import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpireIds;
import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.data.BMOutlineModel;
import umich.ms.batmass.data.core.lcms.features.data.TreeTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireIdTreeTableModelData { //extends TreeTableModelData<UmpireIds> {
//
//    public UmpireIdTreeTableModelData(DataSource<UmpireIds> source) {
//        super(source);
//    }
//
//    @Override
//    public BMOutlineModel create() {
//        UmpireIds data = this.getData();
//        if (data == null) {
//            throw new IllegalStateException("You must have loaded the data into the container"
//                    + " before calling create() to get OutlineModel");
//        }
//        
//        UmpireIdTreeModel treeModel = new UmpireIdTreeModel(data);
//        UmpireIdRowModel rowModel = new UmpireIdRowModel();
//        String firstColName = "Peptide ion";
//        UmpireIdOutlineModel outlineModel = new UmpireIdOutlineModel(treeModel, rowModel, true, firstColName);
//
//        return outlineModel;
//    }
}
