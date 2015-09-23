/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
