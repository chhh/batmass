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
