/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.services.folderproviders;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectServiceProvider;
import umich.ms.batmass.projects.core.services.spi.ProjectSubfolderProvider;
import umich.ms.batmass.projects.types.metabolomics.MetabolomicsProject;
import umich.ms.batmass.projects.types.proteomics.ProteomicsProject;

/**
 * Provider for the folder, where LCMS files should reside (more specifically,
 * FileDescriptors should be placed there).
 * @author Dmitry Avtonomov
 */
@ProjectServiceProvider(
        service=FolderProviderLCMSFiles.class,
        projectType={
            ProteomicsProject.TYPE,
            MetabolomicsProject.TYPE
        }
)
public class FolderProviderLCMSFiles extends ProjectSubfolderProvider {

    public FolderProviderLCMSFiles(Project project) {
        super(project);
    }

    @Override
    public String getRelativePath() {
        return "lcms_files"; //NOI18N
    }
}
