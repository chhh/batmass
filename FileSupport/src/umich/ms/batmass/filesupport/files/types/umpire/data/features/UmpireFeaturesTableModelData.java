/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.data.FeatureTableModelData;

/**
 * It's a {@code Data Container}, that handles loading/unloading {@code Features<UmpireFeature> }.
 * After calling the {@code load()} method the feature data is cached and you can now call
 * the {@code create()} method - several tables might share the same {@code Features<UmpireFeature>} object,
 * but each one should have its own {@code TableModel}, so table models are not cached.
 * Once created, Table Model's lifecycle is managed by the user only.
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesTableModelData extends FeatureTableModelData<UmpireFeature> {

    public UmpireFeaturesTableModelData(DataSource<Features<UmpireFeature>> source) {
        super(source);
    }

    @Override
    public UmpireFeaturesTableModel create() {
        Features<UmpireFeature> data = getData();
        if (data == null) {
            throw new IllegalStateException("You must have loaded the data from the data source before calling create()."
                    + "Use .load(Object user) on this object first.");
        }

        return new UmpireFeaturesTableModel(data.getMs1().getList());
    }
    
}
