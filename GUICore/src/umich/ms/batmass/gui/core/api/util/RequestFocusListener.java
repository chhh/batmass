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
package umich.ms.batmass.gui.core.api.util;

import javax.swing.JComponent;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author dmitriya
 */
public class RequestFocusListener implements AncestorListener {

    private boolean removeListener;

    /*
     *  Convenience constructor. The listener is only used once and then it is
     *  removed from the component.
     */
    public RequestFocusListener() {
        this(true);
    }

    /*
     *  Constructor that controls whether this listener can be used once or
     *  multiple times.
     *
     *  @param removeListener when true this listener is only invoked once
     *                        otherwise it can be invoked multiple times.
     */
    public RequestFocusListener(boolean removeListener) {
        this.removeListener = removeListener;
    }

    @Override
    public void ancestorAdded(AncestorEvent e) {
        JComponent component = e.getComponent();
        component.requestFocusInWindow();

        if (removeListener) {
            component.removeAncestorListener(this);
        }
    }

    @Override
    public void ancestorMoved(AncestorEvent e) {
    }

    @Override
    public void ancestorRemoved(AncestorEvent e) {
    }
}
