/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.management;

import umich.ms.batmass.data.core.api.AbstractUnloadable;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.datatypes.LCMSData;

/**
 * Put instances of this into {@link BMTopComponent}'s lookup using its
 * {@link BMTopComponent#addToLookup(java.lang.Object) } method, then when
 * the component will be closing, it will automagically free the resources.
 * @author Dmitry Avtonomov
 */
public class UnloadableLCMSData extends AbstractUnloadable<LCMSData> {

    public UnloadableLCMSData(LCMSData data) {
        super(data);
    }
    
    @Override
    public void unload() {
        data.releaseMemory();
    }
}
