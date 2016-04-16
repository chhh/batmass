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

import java.util.LinkedHashSet;
import java.util.Set;
import net.engio.mbassy.bus.MBassador;

/**
 * Default implementation for a pub/sub support. Stores buses subscribed to in a
 * {@link LinkedHashSet}.
 *
 * @author Dmitry Avtonomov
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class AbstractBusPubSub implements IBusPubSub {

    protected LinkedHashSet<MBassador<Object>> buses;

    public AbstractBusPubSub() {
        this.buses = new LinkedHashSet<>(2);
    }

    @Override
    public void addBus(MBassador bus) {
        buses.add(bus);
    }

    @Override
    public MBassador removeBus(MBassador bus) {
        return buses.remove(bus) ? bus : null;
    }

    @Override
    public Set<MBassador<Object>> getBuses() {
        return buses;
    }

    /**
     * Publish an event.
     * @param msg
     */
    public void publish(Object msg) {
        for(MBassador bus : buses) {
            bus.publish(msg);
        }
    }
}
