/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.type;

import java.awt.Image;
import java.lang.ref.WeakReference;
import java.util.Collection;
import javax.swing.Action;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ProxyLookup;
import umich.ms.batmass.nbputils.nodes.NodeSubmenuUtils;
import umich.ms.batmass.projects.core.services.spi.ProjectActionsPathProvider;
import umich.ms.batmass.projects.core.util.BMProjectUtils;

/**
 * Even though the class is named <i>'abstract'</i>, there is nothing in it now
 * that is worth forcing being overridden by specific implementations.
 * So users can just extend it, or use as is, instantiating directly.
 * @author dmitriya
 */
public class BMProjectLogicalView implements LogicalViewProvider {

    protected final BMProject project;
    private WeakReference<BMProjectNode> rootNodeRef;

    public BMProjectLogicalView(BMProject project) {
        this.project = project;
    }

    public BMProjectNode getRootNode() {
        return rootNodeRef.get();
    }

    @Override
    public Node findPath(Node root, Object target) {
        //leave unimplemented for now
        return null;
    }

    @Override
    public Node createLogicalView() {
        String layerNodesFolder = BMProject.getLayerNodesPath(project.getType());
        Children projectChildren = NodeFactorySupport.createCompositeChildren(project, layerNodesFolder);

        BMProjectNode root = new BMProjectNode(new AbstractNode(projectChildren), project);
        rootNodeRef = new WeakReference<>(root);

        return root;
    }


   /**
     * This is the node you actually see in the Projects window for the project.
     */
    protected final class BMProjectNode extends FilterNode {

        final BMProject project;

        public BMProjectNode(Node node, BMProject project) {
            super(node,
                    new FilterNode.Children(node),
                    //The projects system wants the project in the Node's lookup.
                    //NewAction and friends want the original Node's lookup.
                    //Make a merge of both:
                    new ProxyLookup(
                            project.getLookup(),
                            node.getLookup())
            );
            this.project = project;
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.icon2Image(ProjectUtils.getInformation(project).getIcon());
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public String getDisplayName() {
            return ProjectUtils.getInformation(project).getDisplayName();
        }

        @Override
        public Action[] getActions(boolean context) {
            Collection<? extends ProjectActionsPathProvider> addonPaths =
                    project.getLookup().lookupAll(ProjectActionsPathProvider.class);
            String[] layerPaths = new String[]{
                BMProject.getLayerProjectActionsPath(project.getType()),
                BMProject.getLayerProjectActionsPath(BMProject.TYPE_ANY)
            };
            layerPaths = BMProjectUtils.aggregateLayerPaths(addonPaths, layerPaths);

            Action[] defaultProjectActions = new Action[]{
                // CommonProjectActions.newFileAction(),
                CommonProjectActions.renameProjectAction(),
                CommonProjectActions.deleteProjectAction(),
                CommonProjectActions.closeProjectAction(),
                CommonProjectActions.customizeProjectAction(),
            };

            Action[] actions = NodeSubmenuUtils.createActions(context, defaultProjectActions, layerPaths);

            return actions;
        }
    }

}
