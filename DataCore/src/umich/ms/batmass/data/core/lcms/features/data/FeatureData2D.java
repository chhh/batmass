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
