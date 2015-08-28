/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.services.folderproviders;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectServiceProvider;
import umich.ms.batmass.projects.core.services.spi.ProjectSubfolderProvider;
import umich.ms.batmass.projects.types.proteomics.ProteomicsProject;

/**
 *
 * @author dmitriya
 */
@ProjectServiceProvider(
        service=FolderProviderSupplementary.class,
        projectType={
            ProteomicsProject.TYPE
        }
)
public class FolderProviderSupplementary extends ProjectSubfolderProvider {

    public FolderProviderSupplementary(Project project) {
        super(project);
    }

    @Override
    public String getRelativePath() {
        return "supplementary"; //NOI18N
    }

}
