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
package umich.ms.batmass.gui.viewers.map2d.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.apache.commons.configuration.ConfigurationException;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@OptionsPanelController.SubRegistration(
        location = "ViewerOptions",
        displayName = "#AdvancedOption_DisplayName_Map2DOptions",
        keywords = "#AdvancedOption_Keywords_Map2DOptions",
        keywordsCategory = "ViewerOptions/Map2DOptions"
)
@Messages({
    "AdvancedOption_DisplayName_Map2DOptions=2D Map",
    "AdvancedOption_Keywords_Map2DOptions=2d map"
})
public final class Map2DOptionsOptionsPanelController extends OptionsPanelController {

    private Map2DOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        try {
            getPanel().load();
        } catch (ConfigurationException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    getPanel().store();
                    changed = false;
                } catch (ConfigurationException | IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private Map2DOptionsPanel getPanel() {
        if (panel == null) {
            panel = new Map2DOptionsPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
