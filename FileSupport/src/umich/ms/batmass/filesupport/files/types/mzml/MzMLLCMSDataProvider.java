/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.mzml;

import java.nio.file.Path;
import org.openide.util.lookup.InstanceContent;
import umich.ms.batmass.filesupport.core.annotations.NodeCapabilityRegistration;
import umich.ms.batmass.filesupport.core.spi.nodes.AbstractCapabilityProvider;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptor;
import umich.ms.datatypes.LCMSData;
import umich.ms.datatypes.scancollection.impl.ScanCollectionDefault;
import umich.ms.fileio.filetypes.mzml.MZMLFile;

/**
 * Puts an {@link LCMSData} object into node's lookup.
 * @author Dmitry Avtonomov
 */
@NodeCapabilityRegistration(
        fileCategory = MzMLAsLCMSTypeResolver.CATEGORY,
        fileType = MzMLAsLCMSTypeResolver.TYPE
)
public class MzMLLCMSDataProvider extends AbstractCapabilityProvider {

    @Override
    public void addCapabilitiesToLookup(InstanceContent ic, FileDescriptor desc) {
        Path path = desc.getPath().toAbsolutePath();
        MZMLFile lcmsDataSource = new MZMLFile(path.toString());
        ScanCollectionDefault scans = new ScanCollectionDefault();
        LCMSData lcmsData = new LCMSData(lcmsDataSource, scans);
        ic.add(lcmsData);
    }
}