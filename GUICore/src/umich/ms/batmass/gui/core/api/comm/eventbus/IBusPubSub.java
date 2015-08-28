/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
