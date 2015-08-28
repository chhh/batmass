/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.nbputils.lookup;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author dmitriya
 */
public class CentralLookup extends AbstractLookup {

    private InstanceContent content = null;
    private static CentralLookup def = new CentralLookup();
    
    public CentralLookup(InstanceContent content) {
        super(content);
        this.content = content;
    }
    
    public CentralLookup() {
        this(new InstanceContent());
    }
    
    public static CentralLookup getDefault() {
        return def;
    }
    
    public void add(Object instance) {
        content.add(instance);
    }
    
    public void remove(Object instance) {
        content.remove(instance);
    }

    /**
     * Adds an item to the lookup and immediately removes it,
     * can be used as notification mechanism, so that you didn't forget to
     * remove your notice from the lookup later.
     * @param notice
     */
    public void poke(Object notice) {
        content.add(notice);
        content.remove(notice);
    }
}
