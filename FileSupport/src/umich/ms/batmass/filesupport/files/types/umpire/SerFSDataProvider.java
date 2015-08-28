/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire;

import java.net.URI;
import org.openide.util.lookup.InstanceContent;
import umich.ms.batmass.data.core.lcms.features.data.FeatureData2D;
import umich.ms.batmass.filesupport.core.annotations.NodeCapabilityRegistration;
import umich.ms.batmass.filesupport.core.spi.nodes.AbstractCapabilityProvider;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptor;
import umich.ms.batmass.filesupport.files.types.umpire.data.features.UmpireFeature;
import umich.ms.batmass.filesupport.files.types.umpire.data.features.UmpireFeaturesNodeFactoryData;
import umich.ms.batmass.filesupport.files.types.umpire.data.features.UmpireFeaturesSource;
import umich.ms.batmass.filesupport.files.types.umpire.data.features.UmpireFeaturesTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
@NodeCapabilityRegistration(
        fileCategory = SerFSAsFeaturesTypeResolver.CATEGORY,
        fileType = SerFSAsFeaturesTypeResolver.TYPE
)
public class SerFSDataProvider extends AbstractCapabilityProvider {

    @Override
    public void addCapabilitiesToLookup(InstanceContent ic, FileDescriptor desc) {

        URI uri = desc.getPath().toFile().toURI();
        UmpireFeaturesSource source = new UmpireFeaturesSource(uri);

        FeatureData2D<UmpireFeature> featureData = FeatureData2D.create(source);
        ic.add(featureData);

        UmpireFeaturesTableModelData tableModelData = new UmpireFeaturesTableModelData(source);
        ic.add(tableModelData);

        UmpireFeaturesNodeFactoryData nodeFactoryData = new UmpireFeaturesNodeFactoryData(source);
        ic.add(nodeFactoryData);
    }
}
