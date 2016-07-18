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
package umich.ms.batmass.gui.viewers.map2d.actions;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import static umich.ms.batmass.gui.viewers.map2d.actions.GoToAction.ACCELERATOR;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DPanel;
import umich.ms.batmass.nbputils.actions.ActionUtils;

/**
 *
 * @author Dmitry Avtonomov
 */
@Messages({
    "HomeMapAction.tooltip=Unzoom completely",
    "HomeMapAction.name=Home"})
public class HomeMapAction extends AbstractAction {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/viewers/map2d/icons/icon_home.png";
    private static final String ACTION_NAME = "Home";
    public static final String ACTION_ID = "HOME_MAP_ACTION";
    public static final KeyStroke ACCELERATOR = Utilities.stringToKey("AS-Z");

    protected WeakReference<Map2DPanel> mapPanelRef;

    public HomeMapAction(Map2DPanel map2DPanel) {
        super(ACTION_NAME);
        this.mapPanelRef = new WeakReference<>(map2DPanel);
        
        ImageIcon icon = ImageUtilities.loadImageIcon(ICON_PATH, false);
        putValue(Action.LARGE_ICON_KEY, icon);
        putValue(Action.SMALL_ICON, icon);
        
        putValue(Action.ACCELERATOR_KEY, ACCELERATOR);
        
        StringBuilder tt = new StringBuilder();
        tt.append(NbBundle.getMessage(HomeMapAction.class, "HomeMapAction.tooltip")); // NOI18N
        tt.append("( ").append(ActionUtils.fromNbKeyStrokeToHuman(Utilities.keyToString(ACCELERATOR))).append(" )");
        putValue(Action.SHORT_DESCRIPTION, tt.toString());
        
    }
    
    public String getName() {
        return NbBundle.getMessage(HomeMapAction.class, "HomeMapAction.name"); // NOI18N
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final Map2DPanel panel = mapPanelRef.get();
        if (panel == null)
            throw new IllegalStateException("Update action could not get a map panel"
                    + " from the map component");
        
        panel.zoomOut(true, null, true, true);
    }
    
}
