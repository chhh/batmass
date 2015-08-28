/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.map2d.messages;

import umich.ms.batmass.gui.viewers.map2d.components.Map2DZoomLevel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MsgZoom2D {
    private final Object origin;
    private final Map2DZoomLevel zoomLvl;

    public MsgZoom2D(Object origin, Map2DZoomLevel zoomLvl) {
        this.origin = origin;
        this.zoomLvl = zoomLvl;
    }

    public Object getOrigin() {
        return origin;
    }

    public Map2DZoomLevel getZoomLvl() {
        return zoomLvl;
    }
}
