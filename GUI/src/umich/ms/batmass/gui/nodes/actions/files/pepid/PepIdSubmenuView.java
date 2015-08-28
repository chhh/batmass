/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.nodes.actions.files.pepid;

import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import umich.ms.batmass.filesupport.core.spi.nodes.NodeInfo;
import umich.ms.batmass.nbputils.nodes.NodeSubmenu;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "BatMass/Nodes",
        id = "umich.ms.batmass.gui.nodes.actions.files.pepid.PepIdSubmenuView")
@ActionRegistration(
        displayName = "#CTL_PepIdSubmenuView",
        lazy = false
)
@ActionReferences({
    @ActionReference(
            path = NodeInfo.ACTIONS_LAYER_PATH_BASE + PepIdSubmenuView.LAYER_REL_PATH,
            position = 100
    )
})
@NbBundle.Messages("CTL_PepIdSubmenuView=View")
public class PepIdSubmenuView extends NodeSubmenu {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/eye_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);
    public static final String LAYER_REL_PATH = "/batmass-project-any/batmass-type-any/pep_id";
    public static final String LAYER_REL_PATH_SUBFOLDER = "/view";

    public PepIdSubmenuView() {
        putValue(Action.SMALL_ICON, ICON);
    }


    @Override
    public String getDisplayName() {
        return Bundle.CTL_PepIdSubmenuView();
    }

    @Override
    public String getLayerPath() {
        return NodeInfo.ACTIONS_LAYER_PATH_BASE + LAYER_REL_PATH + LAYER_REL_PATH_SUBFOLDER;
    }
}
