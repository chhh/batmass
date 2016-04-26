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
package umich.ms.batmass.projects.core.nodes.support;

import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import umich.ms.batmass.nbputils.nodes.NodeSubmenuUtils;
import umich.ms.batmass.projects.core.services.spi.ProjectSubfolderProvider;

/**
 * Intended to be wrapped around a single folder in the real file-system.
 * The root folder can't be deleted or moved. To be used in conjunction with
 * {@link ProjectSubfolderProvider}s registered in a project's lookup.
 * @author Dmitry Avtonomov
 */
public class SingleFolderNodeList extends BMNodeList {
    protected String rootDisplayName;
    protected String[] actionPaths;
    static Integer counter = 0;
    /**
     * Get the action paths in your {@link org.netbeans.spi.project.ui.support.NodeFactory}
     * @param p
     * @param fpClass class of the folder-provider
     * @param rootDisplayName display name for the project sub-folder
     * @param actionPaths order of paths is relevant
     */
    public SingleFolderNodeList(Project p, Class<? extends ProjectSubfolderProvider> fpClass, String rootDisplayName, String[] actionPaths) {
        super(p, fpClass);
        this.rootDisplayName = rootDisplayName;
        this.actionPaths = actionPaths;
    }

    @Override
    public Node node(FileObject key) {
        Node errorNode = beforeNodeCreationCheck(key);
        if (errorNode != null) {
            return errorNode;
        }

        DataFolder folder = DataFolder.findFolder(key);
        DataFolder.FolderNode folderNode = folder.new FolderNode(folder.createNodeChildren(DataFilter.ALL));
        ProxyFolderNodeRoot folderNodeRoot = new ProxyFolderNodeRoot(folderNode, actionPaths, rootDisplayName);

        return folderNodeRoot;
    }

    /**
     * Our custom representation for a folder.
     */
    protected static class ProxyFolderNode extends FilterNode {
        protected String[] actionPaths;
        
        public ProxyFolderNode(Node original, String[] actionPaths) {
            super(original, new ProxyFolderNodeChildren(original, actionPaths));
        }
        
        public ProxyFolderNode(Node original, FilterNode.Children children, String[] actionPaths) {
            super(original, children);
            this.actionPaths = actionPaths;
        }

        @Override
        public Action[] getActions(boolean context) {

            Action[] origActions = super.getActions(context);
            Action[] allActions = NodeSubmenuUtils.createActions(context, origActions, actionPaths);
            
            return allActions;
        }
    }

    /**
     * This is the root node for LC/MS files under a project. It can not be
     * deleted moved or renamed, otherwise it behaves like all folders inside it.
     * @see LCMSFilesFolderNode
     */
    protected static class ProxyFolderNodeRoot extends ProxyFolderNode {

        public ProxyFolderNodeRoot(Node original, String[] actionPaths, String displayName) {
            super(original, new ProxyFolderNodeChildren(original, actionPaths), actionPaths);
            setDisplayName(displayName);
        }

        @Override
        public boolean canCut() { return false; }

        @Override
        public boolean canCopy() { return false; }

        @Override
        public boolean canDestroy() { return false; }

        @Override
        public boolean canRename() { return false; }
    }

    /**
     * Children implementation, that replaces all child folder nodes with
     * our custom FilterNode wrappers.
     */
    protected static class ProxyFolderNodeChildren extends FilterNode.Children {
        protected String[] actionPaths;

        public ProxyFolderNodeChildren(Node or, String[] actionPaths) {
            super(or);
            this.actionPaths = actionPaths;
        }
        
        /**
         * Removed that, because it creates "plus signs" next to files in folders 
         * in the project explorer view.
         * Blindly copied from: http://wiki.netbeans.org/DevFaqNodesDecorating<br/>
         * Don't know if this provides any benefits.
         * @param original
         * @return 
         */
//        @Override
//        protected Node copyNode (Node original){
//            return new ProxyFolderNode(original, actionPaths);
//        }

        @Override
        protected Node[] createNodes(Node key) {
            // if this is a folder, then we'll wrap our own FilterNode around it,
            // to provide proper actions
            FileObject fileObj = key.getLookup().lookup(FileObject.class);
            if (fileObj != null && fileObj.isFolder()) {
                Node[] childNode = {new ProxyFolderNode(key, new ProxyFolderNodeChildren(key, actionPaths), actionPaths)};
                return childNode;
            }
            return super.createNodes(key);
        }
    }
}
