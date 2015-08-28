/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.data.FeatureData2D;

/**
 * Typed data container for raw MS1 features coming from Umpire.
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesData extends FeatureData2D<UmpireFeature>{

    public UmpireFeaturesData(DataSource<Features<UmpireFeature>> source) {
        super(source);
    }
}
