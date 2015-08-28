/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.lcms.features.data;

import org.openide.nodes.ChildFactory;
import umich.ms.batmass.data.core.api.DataContainer;
import umich.ms.batmass.data.core.api.DataSource;

/**
 * A marker class that should be extended to provide
 * @author Dmitry Avtonomov
 * @param <T>
 */
public abstract class OutlineNodeFactoryData<T> extends DataContainer<T> {

    public OutlineNodeFactoryData(DataSource<T> source) {
        super(source);
    }

    public abstract ChildFactory<?> create();
}
