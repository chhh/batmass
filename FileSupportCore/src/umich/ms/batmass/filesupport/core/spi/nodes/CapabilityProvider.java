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
