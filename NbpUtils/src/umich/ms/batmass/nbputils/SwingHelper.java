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
package umich.ms.batmass.nbputils;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class SwingHelper {
    private SwingHelper() {};
    
    /**
     * Invoke some code on Event Dispatch Thread.<br/>
     * Just wrap your code into a Runnable and pass to this function
     * to make sure everything is run on EDT.
     * @param r 
     */
    public static final void invokeOnEDT(Runnable r) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(r);
        } else {
            r.run();
        }
    }

    /**
     * Will block until the work has been done on EDT. In case of errors shows a dialog.
     * @param r
     */
    public static final void invokeOnEDTSynch(Runnable r) {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            r.run();
        }
    }

    /**
     * Will block until the work has been done on EDT and then returns result.
     * In case of any errors, pops up error windows and returns null.
     * @param <T>
     * @param task
     * @return null in case of an error or after a 30 sec timeout
     */
    public static final <T> T invokeOnEDTSynch(FutureTask<T> task) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(task);
        } else {
            task.run();
        }
        try {
            return task.get(30, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }


    /**
     * Finds the index of a component in its parent container by iterating over
     * all children.
     * @param component the component must have a parent
     * @return -1 if component is null or has no parent
     */
    public static final int getComponentIndex(Component component) {
        if (component != null && component.getParent() != null) {
            Container c = component.getParent();
            for (int i = 0; i < c.getComponentCount(); i++) {
                if (c.getComponent(i) == component) {
                    return i;
                }
            }
        }
        return -1;
    }
}