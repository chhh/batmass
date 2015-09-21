/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
