/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.types.proteomics;

import javax.swing.ImageIcon;
import org.netbeans.spi.project.ProjectFactory;
import org.openide.util.lookup.ServiceProvider;
import umich.ms.batmass.projects.core.type.BMProject;
import umich.ms.batmass.projects.core.type.BMProjectFactory;

/**
 * A generic base class for all project types that use some MS files as their base,
 * e.g. metabolomics, proteomics, etc all use mzXML/RAW/mzML... files as the main
 * input source. Those project types should extend this one, providing some
 * project-specific actions.
 * @author dmitriya
 */
@ServiceProvider(service=ProjectFactory.class)
public class ProteomicsProjectFactory extends BMProjectFactory {

    @Override
    public ImageIcon getIcon() {
        return ProteomicsProject.ICON;
    }

    @Override
    public String getProjectDir() {
        return ProteomicsProject.PROJECT_DIR;
    }

    @Override
    public String getProjectPropfile() {
        return ProteomicsProject.PROJECT_PROPFILE;
    }

    @Override
    public Class<? extends BMProject> getProjectClass() {
        return ProteomicsProject.class;
    }

    @Override
    public String getProjectTypeDescription() {
        return "LC/MS based project type for Proteomics";
    }

    @Override
    public String getProjectTypeName() {
        return "Proteomics Project";
    }
}
