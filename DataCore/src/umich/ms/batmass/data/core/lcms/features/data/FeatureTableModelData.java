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
