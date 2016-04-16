/* 
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umich.ms.batmass.nbputils.actions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 * Hides away boilerplate code, required to create context-aware actions with
 * complex activation rules. This one only supports a single When this action is registered in the layer,
 * and then instantiated, the no-arguments constructor is used, which means
 * it will use actionsGlobalContext for it's activation context.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractContextAwareAction<T> extends AbstractAction
        implements LookupListener, ContextAwareAction {


    private Lookup context;
    private volatile Lookup.Result<T> lkpResult;

    public abstract Class<T> getActivationClass();
    public abstract boolean isActivated(Collection<? extends T> instances);


    public AbstractContextAwareAction() {
        this(Utilities.actionsGlobalContext());
    }

    public AbstractContextAwareAction(Lookup context) {
        this.context = context;
    }

    protected void init() {
        assert SwingUtilities.isEventDispatchThread() : "This shall be called only from AWT thread (EDT)";

        Lookup.Result<T> tmp = lkpResult;
        if (tmp == null) {
            synchronized (this) {
                tmp = lkpResult;
                if (tmp == null) {
                    // The thing we want to listen for the presence or absence of
                    // in the global selection
                    tmp = context.lookupResult(getActivationClass());
                    lkpResult = tmp;
                    tmp.addLookupListener(this);
                    resultChanged(null);
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        init();
        return isActivated(lkpResult.allInstances());
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(isActivated(lkpResult.allInstances()));
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        // TODO: WARNING: ACHTUNG: this is a very dirty hack, allowing for easier
        // inheritance, people won't need to provide
        @SuppressWarnings("rawtypes")
        Constructor<? extends AbstractContextAwareAction> constructor;
        try {
            constructor = this.getClass().getConstructor(Lookup.class);
            return constructor.newInstance(actionContext);
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            String msg = "In a subclass of AbstractContextAwareAction, you MUST provide"
                    + " a constructor taking a Lookup as its single parameter."
                    + "If you're a user of the application, please notify the "
                    + "developer.";
            Exceptions.printStackTrace(new IllegalStateException(msg, ex));
        }
        //return new this(actionContext);
        return null;
    }

    public Collection<? extends T> getLookupResult() {
        return lkpResult.allInstances();
    }
    
}
