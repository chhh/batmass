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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import umich.ms.batmass.projects.core.services.spi.ProjectSubfolderProvider;

/**
 *
 * @author dmitriya
 */
public abstract class BMNodeList implements NodeList<FileObject> {

    private final WeakReference<Project> projRef;
    private final WeakReference<ProjectSubfolderProvider> folderProividerRef;
    private FileObject rootDir = null;

    public BMNodeList(Project p, Class<? extends ProjectSubfolderProvider> fpClass) {
        this.projRef = new WeakReference<>(p);
        ProjectSubfolderProvider folderProvider = p.getLookup().lookup(fpClass);
        this.folderProividerRef = new WeakReference<>(folderProvider);
        rootDir = folderProvider.getFolder();
    }

    @Override
    public List<FileObject> keys() {
        ArrayList<FileObject> list = new ArrayList<>();
        list.add(rootDir);
        return list;
    }

    /**
     * Default error node creation implementation.
     * @param key
     * @return null if everything is ok, or an error node otherwise
     */
    public Node beforeNodeCreationCheck(FileObject key) {
        // if the root dir could not be created by folder provider,
        // return a dummy node with an error message
        if (key == null) {
            AbstractNode node = new AbstractNode(Children.LEAF);
            Project prj = projRef.get();
            ProjectSubfolderProvider fp = folderProividerRef.get();
            String msg = "FolderProvider couldn't create directory";
            if (prj != null && fp != null) {
                msg = String.format("Couldn't craete [%s] directory in [%s] project",
                        fp.getRelativePath(), prj.getProjectDirectory().getPath());
            }
            node.setDisplayName(msg);
            return node;
        }
        return null;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void addNotify() {
    }

    @Override
    public void removeNotify() {
    }
}
