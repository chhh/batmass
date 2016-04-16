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
package umich.ms.batmass.projects.services.folderproviders;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectServiceProvider;
import umich.ms.batmass.projects.core.services.spi.ProjectSubfolderProvider;
import umich.ms.batmass.projects.core.type.BMProject;

/**
 * This service provides access to a folder where all "original files" generated
 * by internal application runs should be placed, and then linked via .file_descriptor
 * files placed in viewable folders.
 * @author dmitriya
 */
@ProjectServiceProvider(
        service=FolderProviderGeneratedData.class,
        projectType={
            BMProject.TYPE_ANY
        }
)
public class FolderProviderGeneratedData extends ProjectSubfolderProvider {

    public FolderProviderGeneratedData(Project project) {
        super(project);
    }

    @Override
    public String getRelativePath() {
        return "generated_data"; //NOI18N
    }

}