/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.lcms.features.data;

import umich.ms.batmass.data.core.api.DataContainer;
import umich.ms.batmass.data.core.api.DataSource;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class TreeTableModelData<T> extends DataContainer<T> {

    public TreeTableModelData(DataSource<T> source) {
        super(source);
    }

    public abstract BMOutlineModel create();
}
