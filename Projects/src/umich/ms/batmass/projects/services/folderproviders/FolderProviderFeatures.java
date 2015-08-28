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
 *
 * @author Dmitry Avtonomov
 */
@ProjectServiceProvider(
        service=FolderProviderFeatures.class,
        projectType={
            ProteomicsProject.TYPE,
            MetabolomicsProject.TYPE
        }
)
public class FolderProviderFeatures extends ProjectSubfolderProvider {

    public FolderProviderFeatures(Project project) {
        super(project);
    }

    @Override
    public String getRelativePath() {
        return "lcms_features"; //NOI18N
    }
}
