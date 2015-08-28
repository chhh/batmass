/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.nbputils.nodes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

/**
 * A helper class for actions, that are only added to Node context menu to
 * represent sub-menus.<br/>
 * Register a NodeSubmenu in layer as a regular action for your Node, and 
 * override the {@link #getLayerPath() } to return the proper layer path from
 * which actions should be aggregated, which typically should be some sub-folder
 * of the the folder with all actions for that Node.
 * @author Dmitry Avtonomov
 */
public abstract class NodeSubmenu extends AbstractAction implements NodeSubmenuPresenter {
    // This constructor is not needed, our action doesn't need to have a name property
    // we're only using it as a stub for registering a submenu in node's context
    // menu. The name of the submenu is set via JMenu.setText() method in
    // #getPopupPresenter().
    //public NodeSubmenuPresenter() {
    //    super(getDisplayName());
    //}
    
    /**
     * Specify the display name for your submenu.
     * @return display name of the submenu
     */
    public abstract String getDisplayName();
    /**
     * Specify the layer.xml path, from which the actions are to be aggregated.
     * @return layer path
     */
    public abstract String getLayerPath();

    @Override
    public void actionPerformed(ActionEvent e) {
        // this = submenu => do nothing
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return NodeSubmenuUtils.getSubmneuPresenter(this, getLayerPath(), getDisplayName());
    }
}
