/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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