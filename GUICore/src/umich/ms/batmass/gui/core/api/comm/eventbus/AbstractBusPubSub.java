/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
