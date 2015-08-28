/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.map2d.messages;


import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.datatypes.scan.IScan;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MsgZoom1D {
    private final Object origin;
    private final MzRtRegion region;
    private final IScan scan;

    public MsgZoom1D(Object origin, MzRtRegion region, IScan scan) {
        this.origin = origin;
        this.region = region;
        this.scan = scan;
    }

    public Object getOrigin() {
        return origin;
    }

    public MzRtRegion getRegion() {
        return region;
    }

    public IScan getScan() {
        return scan;
    }
}
