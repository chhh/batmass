/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.services.spi;

import java.lang.ref.WeakReference;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import umich.ms.batmass.projects.core.type.BMProject;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class ProjectSubfolderProvider {
    private final WeakReference<BMProject> project;

    public ProjectSubfolderProvider(Project project) {
        if (!(project instanceof BMProject)) {
            throw new IllegalStateException("You can't register a FolderProvider on a Project which is not a sub-class of BMProject");
        }
        this.project = new WeakReference<>((BMProject)project);
    }

    /**
     * Get the folder from project's main directory.
     * It will be created if not yet exists.<br/>
     * Some bad errors might be thrown if the project has been closed already.
     * @return FileObject for the folder
     */
    //TODO: Some bad errors might be thrown if the project has been closed already.
    public FileObject getFolder() {
        return project.get().getProjectUtilityFolder(getRelativePath(), true);
    }

    /**
     * Relative path to the folder, e.g. "lcms_files".
     * @return 
     */
    public abstract String getRelativePath();
}
