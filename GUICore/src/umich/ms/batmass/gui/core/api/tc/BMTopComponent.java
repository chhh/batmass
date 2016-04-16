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
package umich.ms.batmass.gui.core.api.tc;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import umich.ms.batmass.data.core.api.Unloadable;
import umich.ms.batmass.gui.core.api.util.ConsecutiveRequestProcessor;
import umich.ms.batmass.nbputils.OutputWndPrinter;
import umich.ms.batmass.nbputils.SwingHelper;


/**
 *
 * @author Dmitry Avtonomov
 */
public class BMTopComponent extends TopComponent {
    protected InstanceContent ic;
    protected Lookup lkp;
    protected ChildLookupProxy childLkpProxy;

    /** The delay before cleanup is run. */
    protected static final int CLEANUP_DELAY = 3;
    protected static final TimeUnit CLEANUP_UNIT = TimeUnit.SECONDS;

    /**
     * Will create a new lookup and merge it with the default lookup implementation
     * in standard TopComponents.
     */
    public BMTopComponent() {
        //super();
        ic = new InstanceContent();
        Lookup lookupNew = new AbstractLookup(ic);
        //Lookup lookupOrig = super.getLookup();
        childLkpProxy = new ChildLookupProxy();
        Lookup proxy = Lookups.proxy(childLkpProxy);
        lkp = new ProxyLookup(lookupNew, proxy);

        associateLookup(lkp);
    }

//    @Override
//    public Lookup getLookup() {
//        return lkp;
//    }

    /**
     * Just adds an object to the instance content of this TC.
     * @param o to be added
     */
    public void addToLookup(Object o) {
        ic.add(o);
    }

    /**
     * Just removes the object from instance content of this TC. No indication
     * of whether the object was present there in the first place is provided,
     * so if you want to be sure, query the lookup first.
     * @param o
     */
    public void removeFromLookup(Object o) {
        ic.remove(o);
    }

    /**
     * Remove all objects of provided type from the lookup.
     * @param clazz type of objects to remove
     */
    public void removeDataFromLookup(Class<?> clazz) {
        Collection<? extends Object> data = getLookup().lookupAll(clazz);
        if (data.size() > 0) {
            for (Object m : data) {
                removeFromLookup(m);
            }
        }
    }

    /**
     * Use this method when you need to proxy the lookup of children of this TC.
     * @param lookup
     */
    public void setProxiedLookup(Lookup lookup) {
        childLkpProxy.setLkp(lookup);
    }

    @Override
    protected void componentClosed() {
        super.componentClosed();

        final FutureTask<Boolean> checkIfOpened;
        checkIfOpened = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean opened = BMTopComponent.this.isOpened();
                boolean visible = BMTopComponent.this.isVisible();

                OutputWndPrinter.printOut("BatMass System", "================== TC: " + BMTopComponent.this.getDisplayName() + String.format(
                        " opned: %s, visible: %s", Boolean.toString(opened), Boolean.toString(visible)));

                return opened;
            }
        });

        // we need to delay cleanup a little for the cases when the TC was merely closed
        // for a split second only to be reopened again in another mode
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                OutputWndPrinter.printOut("BatMass System", "================== cleanup initiated for TC: " + BMTopComponent.this.getDisplayName());

                Boolean isOpened = SwingHelper.invokeOnEDTSynch(checkIfOpened);
                isOpened = isOpened == null ? false : isOpened;
                
                if (isOpened) {
                    OutputWndPrinter.printOut("BatMass System", "================== cleanup cancelled (open or visible) for TC: " + BMTopComponent.this.getDisplayName());
                    return;
                }

                OutputWndPrinter.printOut("BatMass System", "================== cleanup running (open or visible) for TC: " + BMTopComponent.this.getDisplayName());
                // When the component is closing, will scan own lookup for Unloadable
                // implementations, all other TopComponents'
                // lookup for presence of Unloadable
                Lookup lookup = getLookup();
                Collection<? extends Unloadable> unloadablesThis = lookup.lookupAll(Unloadable.class);
                if (unloadablesThis.size() > 0) {
                    // if there was something unloadable, let's check if other viewers are using this resource
                    Set<TopComponent> openedTCs = WindowManager.getDefault().getRegistry().getOpened();
                    for (Unloadable uThis : unloadablesThis) {
                        boolean isUsedSomewhereElse = false;
                        for (TopComponent tc : openedTCs) {
                            if (tc == BMTopComponent.this) {
                                // we don't want to be comparing to ourself
                                continue;
                            }
                            Collection<? extends Unloadable> unloadablesOther = tc.getLookup().lookupAll(Unloadable.class);
                            if (unloadablesOther.size() > 0) {
                                for (Unloadable uOther : unloadablesOther) {
                                    if (uThis.isSameResource(uOther)) {
                                        isUsedSomewhereElse = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!isUsedSomewhereElse) {
                            uThis.unload();
                        }
                    }
                }
            }
        };

        ConsecutiveRequestProcessor.getDefault().schedule(runnable, CLEANUP_DELAY, CLEANUP_UNIT);
    }


    /**
     * A fake lookup that is to be used in Lookups.proxy in the constructor of the
     * top component when it's first created and doesn't yet contain child
     * components. Later on, when some children are created, it can be used to
     * switch to providing their lookup instead.
     */
    protected class ChildLookupProxy implements Lookup.Provider {
        private Lookup lkp;

        public ChildLookupProxy() {
            lkp = new AbstractLookup(new InstanceContent());
        }

        @Override
        public Lookup getLookup() {
            return lkp;
        }

        public Lookup getLkp() {
            return lkp;
        }

        public void setLkp(Lookup lkp) {
            this.lkp = lkp;
        }
    }
}
