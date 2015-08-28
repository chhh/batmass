/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api.comm.messages;

import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author Dmitry Avtonomov
 */
@SuppressWarnings("rawtypes")
public class MsgNewMemberSubscribed {
    protected final MBassador bus;

    public MsgNewMemberSubscribed(MBassador bus) {
        this.bus = bus;
    }

    public MBassador getBus() {
        return bus;
    }
}
