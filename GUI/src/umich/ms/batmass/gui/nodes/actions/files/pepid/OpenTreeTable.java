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
package umich.ms.batmass.gui.nodes.actions.files.pepid;

import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import umich.ms.batmass.data.core.lcms.features.data.TreeTableModelData;
import umich.ms.batmass.filesupport.core.spi.nodes.NodeInfo;
import umich.ms.batmass.gui.nodes.actions.files.features.FeaturesSubmenuView;
import umich.ms.batmass.gui.viewers.treetable.TreeTableTopComponent;
import umich.ms.batmass.nbputils.actions.AbstractContextAwareAction;

/**
 *
 * @author Dmitry Avtonomov
 */
//@ActionID(
//        category = "BatMass/Nodes",
//        id = "umich.ms.batmass.gui.nodes.actions.files.pepid.OpenTreeTable"
//)
//@ActionRegistration(
//        displayName = "#CTL_OpenTreeTable",
//        lazy = false
//)
//@ActionReferences({
//    @ActionReference(
//            path = NodeInfo.ACTIONS_LAYER_PATH_BASE + PepIdSubmenuView.LAYER_REL_PATH + FeaturesSubmenuView.LAYER_REL_PATH_SUBFOLDER,
//            position = 510
//    )
//})
@NbBundle.Messages("CTL_OpenTreeTable=Table Old")
@SuppressWarnings({"rawtypes"})
public class OpenTreeTable extends AbstractContextAwareAction<TreeTableModelData> {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/list.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    public OpenTreeTable() {
        this(Utilities.actionsGlobalContext());
    }

    public OpenTreeTable(Lookup context) {
        super(context);
        putValue(Action.NAME, Bundle.CTL_OpenTreeTable());
        putValue(Action.SMALL_ICON, ICON);
    }


    @Override
    public Class<TreeTableModelData> getActivationClass() {
        return TreeTableModelData.class;
    }

    @Override
    public boolean isActivated(Collection<? extends TreeTableModelData> instances) {
        return instances.size() == 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Collection<? extends TreeTableModelData> result = this.getLookupResult();
        if (result.size() != 1) {
            throw new IllegalStateException("This action can only act when there"
                    + "is exactly one UmpireIdTreeTableModelData object in global selection.");
        }
        final TreeTableModelData data = result.iterator().next();


        TreeTableTopComponent tc = new TreeTableTopComponent();
        tc.open();
        tc.setData(data);

    }

}
