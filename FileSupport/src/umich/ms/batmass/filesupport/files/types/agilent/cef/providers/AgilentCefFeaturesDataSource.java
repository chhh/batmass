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
package umich.ms.batmass.filesupport.files.types.agilent.cef.providers;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.data.core.api.DefaultDataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.filesupport.files.types.agilent.cef.data.AgilentCefFeature;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentCefFile;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentCompound;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentCompounds;


/**
 *
 * @author Dmitry Avtonomov
 */
public class AgilentCefFeaturesDataSource extends DefaultDataSource<Features<AgilentCefFeature>> {

    public AgilentCefFeaturesDataSource(URI origin) {
        super(origin);
    }

    @Override
    public Features<AgilentCefFeature> load() throws DataLoadingException {
        Features<AgilentCefFeature> features = new Features<>();
        try  {
            Path path = Paths.get(uri).toAbsolutePath();
            AgilentCefFile acf = new AgilentCefFile(path);
            AgilentCompounds acs = acf.create();
            if (acs.size() == 0)
                throw new DataLoadingException("The size of the list of features after parsing cef file was zero.");
            acs.splitCompoundsByAdduct();
            if (acs.size() == 0)
                throw new DataLoadingException("The size of the list of features after splitting by adducts was zero.");
            for (AgilentCompound ac : acs.getCompounds()) {
                AgilentCefFeature feature = AgilentCefFeature.create(ac);
                features.add(feature, 1, null);
            }
            return features;
        } catch (IOException ex) {
            throw new DataLoadingException(ex);
        }
    }

}
