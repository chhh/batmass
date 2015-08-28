/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.nbputils.lookup;

import java.util.Collection;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

/**
 * Convenience class, that allows creation of a lookup along with instance
 * content, providing the most commonly used functionality as delegated methods.
 * <br/> You can use this whenever you need to write something like:<br/>
 * {@code IntanceContent ic = new InstanceContent; }<br/>
 * {@code Lookup lkp = new AbstractLookup(ic); }<br/>
 * Can also proxy another lookup, provided in the constructor.
 *
 * @author Dmitry Avtonomov
 */
public class LookupSimple {

    InstanceContent ic;
    Lookup lkp;

    /**
     * The basic constructor - a new lookup backed by a new instance content.
     */
    public LookupSimple() {
        ic = new InstanceContent();
        lkp = new AbstractLookup(ic);
    }

    /**
     * This one is not very useful, the new lookup will be backed by the provided
     * instance content. However you still get the convenience of method delegation.
     * @param ic
     */
    public LookupSimple(InstanceContent ic) {
        this.ic = ic;
        lkp = new AbstractLookup(ic);
    }

    /**
     * Will create its own new lookup with a fresh instance content and merge the
     * with the provided lookup using a {@link ProxyLookup}.
     * @param lkp
     */
    public LookupSimple(Lookup lkp) {
        ic = new InstanceContent();
        this.lkp = new ProxyLookup(lkp, new AbstractLookup(ic));
    }

    public InstanceContent getIc() {
        return ic;
    }

    public Lookup getLkp() {
        return lkp;
    }

    public final void add(Object inst) {
        ic.add(inst);
    }

    public final void remove(Object inst) {
        ic.remove(inst);
    }

    public <T> T lookup(Class<T> clazz) {
        return lkp.lookup(clazz);
    }

    public <T> Lookup.Result<T> lookupResult(Class<T> clazz) {
        return lkp.lookupResult(clazz);
    }

    public <T> Collection<? extends T> lookupAll(Class<T> clazz) {
        return lkp.lookupAll(clazz);
    }
}
