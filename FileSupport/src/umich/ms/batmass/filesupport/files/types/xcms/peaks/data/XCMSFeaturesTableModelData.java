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
package umich.ms.batmass.filesupport.files.types.xcms.peaks.data;

import javax.swing.table.TableModel;
import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.data.FeatureTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
public class XCMSFeaturesTableModelData extends FeatureTableModelData<XCMSFeature> {

    public XCMSFeaturesTableModelData(DataSource<Features<XCMSFeature>> source) {
        super(source);
    }

    @Override
    public TableModel create() {
        Features<XCMSFeature> data = getData();
        if (data == null) {
            throw new IllegalStateException("You must have loaded the data from the data source before calling create()."
                    + "Use .load(Object user) on this object first.");
        }
        
        return new XCMSFeaturesTableModel(data.getMs1().getList());
    }

}
