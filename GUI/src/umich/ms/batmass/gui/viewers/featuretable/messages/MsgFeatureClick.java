/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.featuretable.messages;

import umich.ms.batmass.gui.core.api.data.MzRtRegion;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MsgFeatureClick {
    protected final MzRtRegion region;
    private final Object origin;
    
    public MsgFeatureClick(MzRtRegion region, Object origin) {
        this.region = region;
        this.origin = origin;
    }

    public MzRtRegion getRegion() {
        return region;
    }

    public Object getOrigin() {
        return origin;
    }
    
}
