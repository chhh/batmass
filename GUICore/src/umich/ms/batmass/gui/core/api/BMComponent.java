/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api;

import org.openide.util.Lookup;

/**
 * A wrapper class for visual components that might need to interact with each other.
 * @see BMComponentDefault
 * @author Dmitry Avtonomov
 */
public interface BMComponent extends Lookup.Provider {
    /**
     * Intended to be used for identifying the type of component for purposes
     * of inter-component communication.<br/>
     * E.g. a new component signs up for the message bus, and announces itself on
     * the bus.
     * @return 
     */
    String getComponentType();

    void addToLookup(Object o);

    void removeFromLookup(Object o);

    @Override
    public Lookup getLookup();

}
