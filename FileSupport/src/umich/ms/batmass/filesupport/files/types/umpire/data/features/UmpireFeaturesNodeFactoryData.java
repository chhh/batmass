/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.data.OutlineNodeFactoryData;

/**
 * Used in OutlineTopComponent.
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesNodeFactoryData extends OutlineNodeFactoryData<Features<UmpireFeature>> {

    public UmpireFeaturesNodeFactoryData(DataSource<Features<UmpireFeature>> source) {
        super(source);
    }

    @Override
    public UmpireFeaturesChildFactory create() {
        Features<UmpireFeature> data = getData();
        if (data == null) {
            throw new IllegalStateException("You must call .load(Object user) method before creating child factory.");
        }
        return new UmpireFeaturesChildFactory(data.getMs1().getList());
    }

    
}
