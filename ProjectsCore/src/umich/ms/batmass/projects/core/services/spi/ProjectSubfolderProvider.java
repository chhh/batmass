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
