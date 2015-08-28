/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
