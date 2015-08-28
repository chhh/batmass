/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.examples.actions;

import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import umich.ms.batmass.nbputils.nodes.NodeSubmenu;

/**
 *
 * @author dmitriya
 */
//@ActionID(
//    category = "LCMSFileDesc",
//    id = "umich.ms.batmass.gui.lcmsfileactions.ViewSubmenuPresenter")
//@ActionRegistration(
//    displayName = "#CTL_ViewSubmenuPresenter",
//    lazy = false)
//@ActionReference(
//    path = "BatMass/Actions/Files/Categories/LCMSFiles",
//    position = 50)
@Messages("CTL_ViewSubmenuPresenter=View")
public class ViewSubmenuPresenter extends NodeSubmenu {
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(ViewSubmenuPresenter.class, "CTL_ViewSubmenuPresenter");
    }

    @Override
    public String getLayerPath() {
        return "LCMSFileDesc/View";
    }
}
