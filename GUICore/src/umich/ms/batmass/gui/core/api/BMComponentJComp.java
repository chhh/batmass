/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api;

import javax.swing.JComponent;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Simply a JComponent with a lookup and a component type name.
 * @author Dmitry Avtonomov
 */
public class BMComponentJComp extends JComponent implements BMComponent {
protected InstanceContent ic;
    protected Lookup lkp;

    public BMComponentJComp() {
        super();
        ic = new InstanceContent();
        lkp = new AbstractLookup(ic);
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    /**
     * Just adds an object to the instance content of this TC.
     * @param o to be added
     */
    @Override
    public void addToLookup(Object o) {
        ic.add(o);
    }

    /**
     * Just removes the object from instance content of this TC. No indication
     * of whether the object was present there in the first place is provided,
     * so if you want to be sure, query the lookup first.
     * @param o
     */
    @Override
    public void removeFromLookup(Object o) {
        ic.remove(o);
    }

    /**
     * The default implementation simply returns the canonical name.
     * @return
     */
    @Override
    public String getComponentType() {
        return this.getClass().getCanonicalName();
    };
}
