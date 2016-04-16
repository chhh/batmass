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
package umich.ms.batmass.gui.core.api.swing;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import umich.ms.batmass.gui.core.api.comm.dnd.UnlinkButton;

/**
 *
 * @author Dmitry Avtonomov
 */
public class BMToolBar extends JToolBar {
    protected boolean activated = false;

    public BMToolBar() {
    }

    public BMToolBar(int orientation) {
        super(orientation);
    }

    public BMToolBar(String name) {
        super(name);
    }

    public BMToolBar(String name, int orientation) {
        super(name, orientation);
    }


    public boolean isActivated() {
        return activated;
    }

    /**
     * Changes the enabled state of all
     * @param activated
     */
    public void setActivated(final boolean activated) {
        this.activated = activated;

        // make sure no children of the toolbar can be focused
        for (int i=0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            if (comp instanceof UnlinkButton)
                continue;
            if (JComboBox.class.isAssignableFrom(comp.getClass())) {
                // combo-boxes should be focusable, so you could use the keybord
                comp.setFocusable(true);
            } else {
                comp.setFocusable(false);
            }
            comp.setEnabled(activated);
        }
    }
}
