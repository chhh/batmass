/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api.comm.messages;

import net.engio.mbassy.bus.MBassador;

/**
 * Commands you to unsubscribe from the bus.
 * @author Dmitry Avtonomov
 */
@SuppressWarnings("rawtypes")
public class MsgUnsubscribe {
    private final MBassador bus;

    public MsgUnsubscribe(MBassador bus) {
        this.bus = bus;
    }

    public MBassador getBus() {
        return bus;
    }

}
