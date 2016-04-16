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
