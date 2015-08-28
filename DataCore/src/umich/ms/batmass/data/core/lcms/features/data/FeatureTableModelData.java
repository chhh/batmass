/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.lcms.features.data;

import javax.swing.table.TableModel;
import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.ILCMSFeature2D;

/**
 * It's a {@code Data Container}, that handles loading/unloading {@code Features<? extends LCMSFeature2D> }.
 * After calling the {@code load()} method the feature data is cached and you can now call
 * the {@code create()} method - several tables might share the same {@code Features<?>} object,
 * but each one should have its own {@code TableModel}, so table models are not cached.
 * Once created, Table Model's lifecycle is managed by the user only.
 * @author Dmitry Avtonomov
 * @param <T>
 */
public abstract class FeatureTableModelData<T extends ILCMSFeature2D<?>> extends FeatureData2D<T> {

    public FeatureTableModelData(DataSource<Features<T>> source) {
        super(source);
    }

    public abstract TableModel create();
}
