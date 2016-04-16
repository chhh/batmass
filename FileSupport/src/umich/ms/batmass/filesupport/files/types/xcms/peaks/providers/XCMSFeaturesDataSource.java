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
package umich.ms.batmass.filesupport.files.types.xcms.peaks.providers;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.data.core.api.DefaultDataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.data.XCMSFeature;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.model.XCMSPeakGroup;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.model.XCMSPeakGroups;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.model.XCMSPeaks;

/**
 * @author Dmitry Avtonomov
 */
public class XCMSFeaturesDataSource extends DefaultDataSource<Features<XCMSFeature>> {

    public XCMSFeaturesDataSource(URI origin) {
        super(origin);
    }

    @Override
    public Features<XCMSFeature> load() throws DataLoadingException {
        Features<XCMSFeature> features = new Features<>();
        try {
            Path path = Paths.get(uri).toAbsolutePath();
            XCMSPeaks peaks = XCMSPeaks.create(path);
            XCMSPeakGroups groups = XCMSPeakGroups.create(peaks);
            if (groups == null)
                throw new DataLoadingException("Groups constructed from XCMS peaks could not be constructed (null returned).");
            if (groups.size() == 0)
                throw new DataLoadingException("There were no groups constructed from XCMS peaks that were parsed.");
            
            for (XCMSPeakGroup group : groups.getGroups()) {
                XCMSFeature feature = XCMSFeature.create(group);
                features.add(feature, 1, null);
            }
            return features;
        } catch (IOException ex) {
            throw new DataLoadingException(ex);
        }
    }

}
