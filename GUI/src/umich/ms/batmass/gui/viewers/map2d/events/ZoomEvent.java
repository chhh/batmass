/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.viewers.map2d.events;

import umich.ms.batmass.gui.viewers.map2d.components.Map2DZoomLevel;



/**
 *
 * @author dmitriya
 */
public class ZoomEvent {
    public enum TYPE {
        ZOOM_START,
        ZOOM_END
    }
    Map2DZoomLevel zoomLevel;
    ZoomEvent.TYPE zoomEventType;

    public ZoomEvent(Map2DZoomLevel zoomLevel, TYPE zoomEventType) {
        this.zoomLevel = zoomLevel;
        this.zoomEventType = zoomEventType;
    }

    public Map2DZoomLevel getZoomLevel() {
        return zoomLevel;
    }

    public TYPE getZoomEventType() {
        return zoomEventType;
    }
    
    public boolean isStart() {
        return zoomEventType == TYPE.ZOOM_START;
    }
    
    public boolean isFinish() {
        return zoomEventType == TYPE.ZOOM_END;
    }
}
