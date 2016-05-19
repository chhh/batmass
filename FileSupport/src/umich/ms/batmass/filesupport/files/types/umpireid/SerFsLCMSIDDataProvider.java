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
package umich.ms.batmass.filesupport.files.types.umpireid;

import java.net.URI;
import org.openide.util.Utilities;
import org.openide.util.lookup.InstanceContent;
import umich.ms.batmass.filesupport.core.annotations.NodeCapabilityRegistration;
import umich.ms.batmass.filesupport.core.spi.nodes.AbstractCapabilityProvider;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptor;
import umich.ms.batmass.filesupport.files.types.umpireid.data.UmpireIdData;
import umich.ms.batmass.filesupport.files.types.umpireid.data.UmpireIdSource;
import umich.ms.batmass.filesupport.files.types.umpireid.data.UmpireIdTreeTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
//@NodeCapabilityRegistration(
//        fileCategory = SerFsLCMSIDAsIdsTypeResolver.CATEGORY,
//        fileType = SerFsLCMSIDAsIdsTypeResolver.TYPE
//)
public class SerFsLCMSIDDataProvider { //extends AbstractCapabilityProvider {

//    @Override
//    public void addCapabilitiesToLookup(InstanceContent ic, FileDescriptor desc) {
//
//        URI uri = Utilities.toURI(desc.getPath().toFile());
//        UmpireIdSource source = new UmpireIdSource(uri);
//
//        UmpireIdData dataUmpireId = new UmpireIdData(source);
//        ic.add(dataUmpireId);
//
//        UmpireIdTreeTableModelData dataTreeTableModel = new UmpireIdTreeTableModelData(source);
//        ic.add(dataTreeTableModel);
//    }

}
