/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.nodes;

import org.netbeans.spi.project.ui.support.NodeFactory;
import umich.ms.batmass.projects.core.annotations.BMProjectSubfolderType;
import umich.ms.batmass.projects.core.nodes.spi.BMNodeFactory;
import umich.ms.batmass.projects.services.folderproviders.FolderProviderLCMSFiles;
import umich.ms.batmass.projects.types.metabolomics.MetabolomicsProject;
import umich.ms.batmass.projects.types.proteomics.ProteomicsProject;

/**
 *
 * @author Dmitry Avtonomov
 */
@NodeFactory.Registration(
    position = 100,
    projectType = {
        ProteomicsProject.TYPE,
        MetabolomicsProject.TYPE,
    }
)
@BMProjectSubfolderType(type = NodeFactoryLCMSFiles.TYPE)
public class NodeFactoryLCMSFiles extends BMNodeFactory<FolderProviderLCMSFiles> {
    public static final String TYPE = "lcms";
    public static final String DISPLAY_NAME = "LC/MS files";

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String[] getActionPaths() {
        return super.getActionPaths();
    }

    @Override
    public Class<FolderProviderLCMSFiles> getProjectSubfolderProviderClass() {
        return FolderProviderLCMSFiles.class;
    }
}
