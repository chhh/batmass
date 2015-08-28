/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.outline.components;

import org.openide.explorer.view.OutlineView;
import umich.ms.batmass.gui.core.api.comm.eventbus.AbstractBusPubSub;

/**
 *
 * @author Dmitry Avtonomov
 */
public class OutlineTable extends OutlineView {

    protected BusHandler busHandler;

    public OutlineTable() {
        constructor();
    }

    public OutlineTable(String nodesColumnLabel) {
        super(nodesColumnLabel);
        constructor();
    }


    private void constructor() {
        busHandler = new BusHandler();
        getOutline().setPopupUsedFromTheCorner(true);
    }

    public BusHandler getBusHandler() {
        return busHandler;
    }

    public class BusHandler extends AbstractBusPubSub {

    }
}
