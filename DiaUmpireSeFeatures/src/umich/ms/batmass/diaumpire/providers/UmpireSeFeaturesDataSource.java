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
package umich.ms.batmass.diaumpire.providers;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.data.core.api.DefaultDataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.diaumpire.data.UmpireSeFeature;
import umich.ms.batmass.diaumpire.model.UmpireSeIsoCluster;
import umich.ms.batmass.diaumpire.model.UmpireSeIsoClusters;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireSeFeaturesDataSource extends DefaultDataSource<Features<UmpireSeFeature>> {

    public UmpireSeFeaturesDataSource(URI origin) {
        super(origin);
    }

    @Override
    public Features<UmpireSeFeature> load() throws DataLoadingException {
        Features<UmpireSeFeature> features = new Features<>();
        try  {
            Path path = Paths.get(uri).toAbsolutePath();
            UmpireSeIsoClusters clusters = UmpireSeIsoClusters.create(path);
            if (clusters.getClusters().isEmpty())
                throw new DataLoadingException("The size of the list of features after parsing Umpire file was zero.");            
            for (UmpireSeIsoCluster c : clusters.getClusters()) {
                UmpireSeFeature feature = UmpireSeFeature.create(c);
                features.add(feature, 1, null);
            }
            return features;
        } catch (IOException ex) {
            throw new DataLoadingException(ex);
        }
    }

}