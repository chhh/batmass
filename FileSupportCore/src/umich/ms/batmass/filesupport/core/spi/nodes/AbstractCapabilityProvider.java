/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.spi.nodes;

import umich.ms.batmass.filesupport.core.spi.nodes.CapabilityProvider;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class AbstractCapabilityProvider implements CapabilityProvider {

    @Override
    public String getUID() {
        return this.getClass().getCanonicalName();
    }
    
}
