/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.nbputils.refs;

import com.google.common.base.FinalizablePhantomReference;
import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.collect.Sets;
import java.util.Set;

/**
 * A global reference queue, that tracks its users {@link User} by phantom refs.
 * When a user is garbage collected, its phantom ref is removed from the refs set
 * and its onFinalize() method is called.<br/>
 * Subclass {@link User} and implement the onFinalize() method. User subclasses
 * are added to the ref queue as soon as instances are created.
 * @author Dmitry Avtonomov
 */
public class RefQ {
    /** The global ref queue, that monitors  */
    private static final FinalizableReferenceQueue FRQ = new FinalizableReferenceQueue();
    private static final Set<FinalizablePhantomReference<?>> refs = Sets.newConcurrentHashSet();

    
    public static abstract class User extends FinalizablePhantomReference<Object> {

        public User(Object referent) {
            super(referent, FRQ);
        }

        @Override
        public void finalizeReferent() {
            refs.remove(this);
            onFinalize();
        }

        /**
         * Implement what exactly should happen, when the user-object is finalized.
         */
        public abstract void onFinalize();
    }
}
