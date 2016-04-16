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