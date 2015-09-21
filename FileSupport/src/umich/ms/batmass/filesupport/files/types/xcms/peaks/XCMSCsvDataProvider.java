/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks;

import java.net.URI;
import org.openide.util.lookup.InstanceContent;
import umich.ms.batmass.filesupport.core.spi.nodes.AbstractCapabilityProvider;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptor;
import umich.ms.batmass.filesupport.files.types.umpireid.data.UmpireIdData;
import umich.ms.batmass.filesupport.files.types.umpireid.data.UmpireIdSource;
import umich.ms.batmass.filesupport.files.types.umpireid.data.UmpireIdTreeTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
public class XCMSCsvDataProvider extends AbstractCapabilityProvider {

    @Override
    public void addCapabilitiesToLookup(InstanceContent ic, FileDescriptor desc) {
        URI uri = desc.getPath().toFile().toURI();
        UmpireIdSource source = new UmpireIdSource(uri);

        UmpireIdData dataUmpireId = new UmpireIdData(source);
        ic.add(dataUmpireId);

        UmpireIdTreeTableModelData dataTreeTableModel = new UmpireIdTreeTableModelData(source);
        ic.add(dataTreeTableModel);
    }

}
