/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.spi.nodes;

import org.openide.util.lookup.InstanceContent;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptor;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptorDataObject;

/**
 *
 * @author Dmitry Avtonomov
 */
public interface CapabilityProvider {
    /**
     * Base path in the layer where actions for different file-types are stored.
     */
    public static final String CAPABILITIES_LAYER_PATH_BASE = "BatMass/Nodes/Capabilities";

    /**
     * A unique name, identifying this provider.
     * @return
     */
    String getUID();

    /**
     * Intended use: when a parser is retrieved by it's name in some generic
     * context, it should be able to expose its capabilities to the outer world.
     * That's what happens inside {@link FileDescriptorDataObject}, when a
     * descriptor is de-serialized from disk, the original parser is found in
     * the registry, then the parser's capabilities are added to the lookup
     * of the DataObject(and the corresponding node).
     * @param ic the instance content to which capabilities should be added
     * @param desc a {@link FileDescriptor}, based on which we should provide capabilities
     */
    void addCapabilitiesToLookup(InstanceContent ic, FileDescriptor desc);
}
