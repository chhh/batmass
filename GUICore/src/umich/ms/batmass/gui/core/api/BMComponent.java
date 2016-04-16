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
package umich.ms.batmass.gui.core.api;

import org.openide.util.Lookup;

/**
 * A wrapper class for visual components that might need to interact with each other.
 * @see BMComponentDefault
 * @author Dmitry Avtonomov
 */
public interface BMComponent extends Lookup.Provider {
    /**
     * Intended to be used for identifying the type of component for purposes
     * of inter-component communication.<br/>
     * E.g. a new component signs up for the message bus, and announces itself on
     * the bus.
     * @return 
     */
    String getComponentType();

    void addToLookup(Object o);

    void removeFromLookup(Object o);

    @Override
    public Lookup getLookup();

}
