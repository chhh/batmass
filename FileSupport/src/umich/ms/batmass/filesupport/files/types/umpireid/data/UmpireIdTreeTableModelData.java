/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class UmpireIdTreeTableModelData extends TreeTableModelData<UmpireIds> {

    public UmpireIdTreeTableModelData(DataSource<UmpireIds> source) {
        super(source);
    }

    @Override
    public BMOutlineModel create() {
        UmpireIds data = this.getData();
        if (data == null) {
            throw new IllegalStateException("You must have loaded the data into the container"
                    + " before calling create() to get OutlineModel");
        }
        
        UmpireIdTreeModel treeModel = new UmpireIdTreeModel(data);
        UmpireIdRowModel rowModel = new UmpireIdRowModel();
        String firstColName = "Peptide ion";
        UmpireIdOutlineModel outlineModel = new UmpireIdOutlineModel(treeModel, rowModel, true, firstColName);

        return outlineModel;
    }
}
