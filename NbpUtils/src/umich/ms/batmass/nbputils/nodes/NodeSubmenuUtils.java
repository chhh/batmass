/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.nbputils.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.Utilities;

/**
 * Helper methods to ease creation of sub-menus for Nodes.
 * @author Dmitry Avtonomov
 */
public final class NodeSubmenuUtils {

    private NodeSubmenuUtils() {
    }

    /**
     * Retrieves actions from the given paths.
     *
     * @param context Action context sensitivity
     * @param parentActions just call <code>Arrays.asList(super.getActions(context))</code>
     *  on your node to get this.
     * @param paths Action paths
     * @return All found actions from the given paths
     */
    public static Action[] createActions(boolean context, Action[] parentActions, String... paths) {
        if (parentActions == null && paths == null) {
            return new Action[0];
        }
        ArrayList<Action> subActions = new ArrayList<>();
        ArrayList<Action> actions = new ArrayList<>();
        if (paths != null) {
            for (String path : paths) {
                List<? extends Action> actionsForPath = Utilities.actionsForPath(path);
                for (Action a : actionsForPath) {
                    if (a instanceof NodeSubmenu) {
                        List<Action> presenterActions = findSubActions((NodeSubmenu) a);
                        if (!presenterActions.isEmpty()) {
                            subActions.addAll(presenterActions);
                        } else {
                            continue;
                        }
                    }

                    actions.add(a);
                }
                // if there were some actions found in this group (path in layer),
                // put a separator after
                if (actionsForPath.size() > 0) {
                    actions.add(null);
                }
            }
        }

        // Original parent actions are added to the bottom of the menu
        //List<Action> parentActions = Arrays.asList(super.getActions(context));
        if (parentActions != null) {
            actions.addAll(Arrays.asList(parentActions));
        }

        // remove all actions that are already in some submenu
        actions.removeAll(subActions);

        return actions.toArray(new Action[actions.size()]);
    }

    private static List<Action> findSubActions(NodeSubmenu subMenu) {
        List<Action> actions = new ArrayList<>();

        JMenuItem item = subMenu.getPopupPresenter();
        if (item instanceof JMenu) {
            JMenu menu = (JMenu) item;
            for (int i = 0; i < menu.getItemCount(); i++) {
                JMenuItem menuItem = menu.getItem(i);
                if (menuItem == null)
                    continue;
                Action a = menu.getItem(i).getAction();
                if (a == null)
                    continue;
                actions.add(a);

                if (a instanceof NodeSubmenu) {
                    actions.addAll(findSubActions((NodeSubmenu) a));
                }
            }
        }
        return actions;
    }

    /**
     * Most likely you don't want to use this method yourself, just use
     * {@link NodeSubmenu} instead, which is a simple default implementation.
     * <br/><br/>
     * When you want to create a context sub-menu for a node, from actions installed
     * in a layer file sub-folder (e.g. YourNodeType/Actions/SubFolderToBeASubMenu)
     * you can create a simple {@link javax.swing.AbstractAction} implementing
     * {@link NodeSubmenuPresenter} like this: <pre class="nonnormative">
        {@literal @}ActionID(
            category = "LCMSFileDesc",
            id = "umich.ms.batmass.gui.lcmsfileactions.ViewSubmenuPresenter")
        {@literal @}ActionRegistration(
            displayName = "#CTL_ViewSubmenuPresenter",
            lazy = false)
        {@literal @}ActionReference(
            path = "LCMSFileDesc",
            position = 50)
        {@literal @}Messages("CTL_ViewSubmenuPresenter=View")
        public class ViewSubmenuPresenter extends AbstractAction implements Presenter.Popup {

            {@literal @}Override
            public void actionPerformed(ActionEvent e) {// this is a submenu => do nothing}

            {@literal @}Override
            public JMenuItem getPopupPresenter() {
                String displayName = NbBundle.getMessage(ViewSubmenuPresenter.class, "CTL_ViewSubmenuPresenter");
                String layerPath = "LCMSFileDesc/View";
                return NodeSubmenuUtils.getSubmneuPresenter(this, layerPath, displayName);
            }
        }
        </pre>
     * @param presenterAction
     * @param layerPath Utilities.actionsForPath() will be called on that path
     * @param displayName
     * @return
     */
    public static JMenuItem getSubmneuPresenter(Action presenterAction, String layerPath, String displayName) {
        JMenu menu = new JMenu(presenterAction);
        menu.setText(displayName);

        // we could get the same actions from just "LCMSFileDesc/View", no need to rely on
        // NetBeans making .shadow copies of content of "LCMSFileDesc/" to "Actions/"
        List<? extends Action> actionsForPath = Utilities.actionsForPath(layerPath);
        boolean previousItemWasSeparator = false;
        for (int i = 0; i < actionsForPath.size(); i++) {
            Action a = actionsForPath.get(i);
            if (a == null && !previousItemWasSeparator && i != 0 && i != actionsForPath.size()-1) {
                menu.addSeparator();
                previousItemWasSeparator = true;
            } else if (a != null) {
                menu.add(a);
                previousItemWasSeparator = false;
            }
        }

        return menu;
    }
}
