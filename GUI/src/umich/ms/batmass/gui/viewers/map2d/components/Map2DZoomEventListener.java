/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.viewers.map2d.components;

import java.util.EventListener;
import umich.ms.batmass.gui.viewers.map2d.events.ZoomEvent;

/**
 *
 * @author dmitriya
 */
public interface Map2DZoomEventListener extends EventListener {
    
    /**
     * Called when zoom-in/out begins or finishes.
     * {@link Map2DZoomLevel} in the event is the initial (starting) zoom level
     * when the event is fired for "beginning zoom" and the final level when
     * "finished zoom".
     * 
     * @param e
     */
    public void handleZoomEvent(ZoomEvent e);
}
