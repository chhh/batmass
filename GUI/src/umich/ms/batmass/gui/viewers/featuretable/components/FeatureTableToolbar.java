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
package umich.ms.batmass.gui.viewers.featuretable.components;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import umich.ms.batmass.gui.core.api.comm.dnd.DnDButton;
import umich.ms.batmass.gui.core.api.comm.eventbus.ViewerLinkSupport;
import umich.ms.batmass.gui.core.api.swing.BMToolBar;

/**
 *
 * @author Dmitry Avtonomov
 */
public class FeatureTableToolbar extends BMToolBar {

    protected ViewerLinkSupport linkSupport;
    protected DnDButton btnLinkDnD;
    protected JButton btnUnlink;

    protected static final int toolbarBtnHSpacing = 3;

    @SuppressWarnings({"unchecked"})
    public FeatureTableToolbar(ViewerLinkSupport linkSupport) {
        this.linkSupport = linkSupport;

        // Creating the toolbar
        setBorder(new EmptyBorder(0, toolbarBtnHSpacing, toolbarBtnHSpacing, toolbarBtnHSpacing));
        setFloatable(false);
        // make sure the toolbar can't get focus by itself
        setFocusable(false);
        setRollover(true);

        // Link button
        btnLinkDnD = linkSupport.getBtnLinkDnD();
        add(btnLinkDnD);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));


        // Unlink button
        btnUnlink = linkSupport.getBtnUnlink();
        add(btnUnlink);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));

        setActivated(false);
    }


}
