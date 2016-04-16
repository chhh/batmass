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
import umich.ms.batmass.data.core.lcms.features.data.BMOutlineModel;
import umich.ms.batmass.data.core.lcms.features.data.TreeTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesTreeTableModelData extends TreeTableModelData<Features<UmpireFeature>> {

    public UmpireFeaturesTreeTableModelData(DataSource<Features<UmpireFeature>> source) {
        super(source);
    }

    @Override
    public BMOutlineModel create() {
        Features<UmpireFeature> data = getData();
        if (data == null) {
            throw new IllegalStateException("You must have loaded the data from the data source before calling create()."
                    + "Use .load(Object user) on this object first.");
        }

        throw new UnsupportedOperationException("This is not implemented");
   }
}
