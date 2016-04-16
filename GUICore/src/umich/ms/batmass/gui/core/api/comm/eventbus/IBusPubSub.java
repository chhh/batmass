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
package umich.ms.batmass.gui.core.api.comm.eventbus;

import java.util.Set;
import net.engio.mbassy.bus.MBassador;

/**
 * Defines the capability to publish to MBassador bus. It is a good idea to implement
 * this interface in a subclass of your class.
 * @see
 * @author Dmitry Avtonomov
 */
@SuppressWarnings({"rawtypes"})
public interface IBusPubSub {
    void addBus(MBassador bus);
    MBassador removeBus(MBassador<Object> bus);
    /**
     * Gets all the buses this Pub/Sub is subscribed to.
     * @return
     */
    Set<MBassador<Object>> getBuses();
}
