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
 * @author Dmitry Avtonomov
 */
@ProjectServiceProvider(
        service=FolderProviderPeptideIds.class,
        projectType={
            ProteomicsProject.TYPE
        }
)
public class FolderProviderPeptideIds extends ProjectSubfolderProvider {

    public FolderProviderPeptideIds(Project project) {
        super(project);
    }

    @Override
    public String getRelativePath() {
        return "pep_ids";
    }

}
