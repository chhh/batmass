/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks.providers;

import java.net.URI;
import org.openide.util.Utilities;
import org.openide.util.lookup.InstanceContent;
import umich.ms.batmass.filesupport.core.annotations.NodeCapabilityRegistration;
import umich.ms.batmass.filesupport.core.spi.nodes.AbstractCapabilityProvider;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptor;
import umich.ms.batmass.filesupport.files.types.xcms.peaks.data.XCMSFeaturesTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
@NodeCapabilityRegistration(
        fileCategory = XCMSCsvPeaksTypeResolver.CATEGORY,
        fileType = XCMSCsvPeaksTypeResolver.TYPE
)
public class XCMSCsvDataProvider extends AbstractCapabilityProvider {

    @Override
    public void addCapabilitiesToLookup(InstanceContent ic, FileDescriptor desc) {
        URI uri = Utilities.toURI(desc.getPath().toFile());
        XCMSFeaturesDataSource source = new XCMSFeaturesDataSource(uri);
        
        XCMSFeaturesTableModelData data = new XCMSFeaturesTableModelData(source);
        ic.add(data);
    }

}
