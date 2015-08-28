/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.data.BMOutlineModel;
import umich.ms.batmass.data.core.lcms.features.data.TreeTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesTreeTableModelData extends TreeTableModelData<Features<UmpireFeature>> {

    public UmpireFeaturesTreeTableModelData(DataSource<Features<UmpireFeature>> source) {
        super(source);
    }

    @Override
    public BMOutlineModel create() {
        Features<UmpireFeature> data = getData();
        if (data == null) {
            throw new IllegalStateException("You must have loaded the data from the data source before calling create()."
                    + "Use .load(Object user) on this object first.");
        }

        throw new UnsupportedOperationException("This is not implemented");
   }
}
