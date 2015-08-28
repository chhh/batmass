/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.examples.actions;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.CookieAction;
import umich.ms.datatypes.LCMSData;

/**
 *
 * @author dmitriya
 */

//@ActionID(
//    category = "LCMSFileDesc/View",
//    id = "umich.ms.batmass.gui.lcmsfileactions.CookieBasedAction")
//@ActionRegistration(
//    displayName = "#CTL_CookieBasedAction",
////    iconBase = "umich/ms/batmass/gui/lcmsfileactions/view_chromatogram_icon.png",
//    lazy = false
//)
//@ActionReference(
//    path = "LCMSFileDesc/View",
//    position=200,
//    separatorBefore = 199,
//    separatorAfter = 201)
@Messages("CTL_CookieBasedAction=Cookie based Action!")
public class CookieBasedAction extends CookieAction {

    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class<?>[]{LCMSData.class};
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        String msg = "I'm the cookie action!";
        NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(d);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(this.getClass(), "CTL_CookieBasedAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", true);
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    protected String iconResource() {
        return "umich/ms/batmass/gui/lcmsfileactions/view_chromatogram_icon.png";
    }

//    @Override
//    protected boolean enable(Node[] activatedNodes) {
//
//    }
}
