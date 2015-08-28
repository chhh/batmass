/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api.comm.dnd;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Just a marker class for Unlink button. Used by BMToolBar automatic activation
 * and deactivation of buttons to never touch the Unlink button.
 * Unlink button controls its state itself.
 * @author Dmitry Avtonomov
 */
public class UnlinkButton extends JButton {

    public UnlinkButton() {
        init();
    }

    public UnlinkButton(Icon icon) {
        super(icon);
        init();
    }

    public UnlinkButton(String text) {
        super(text);
        init();
    }

    public UnlinkButton(Action a) {
        super(a);
        init();
    }

    public UnlinkButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setEnabled(false);
    }
}
