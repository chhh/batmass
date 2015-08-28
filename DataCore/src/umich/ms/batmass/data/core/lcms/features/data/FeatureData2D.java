/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.lcms.features.data;

import umich.ms.batmass.data.core.api.DataContainer;
import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.ILCMSFeature2D;

/**
 * To be used with Feature Table viewer.
 * @author Dmitry Avtonomov
 * @param <T> type of features stored in this container
 */
public class FeatureData2D<T extends ILCMSFeature2D<?>> extends DataContainer<Features<T>> {

    protected FeatureData2D(DataSource<Features<T>> source) {
        super(source);
    }

    public static <T extends ILCMSFeature2D<?>> FeatureData2D<T> create(DataSource<Features<T>> source) {
        return new FeatureData2D<>(source);
    }
}
