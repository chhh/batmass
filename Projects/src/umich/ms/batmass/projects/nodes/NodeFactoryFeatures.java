/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.nodes;

import org.netbeans.spi.project.ui.support.NodeFactory;
import umich.ms.batmass.projects.core.annotations.BMProjectSubfolderType;
import umich.ms.batmass.projects.core.nodes.spi.BMNodeFactory;
import umich.ms.batmass.projects.services.folderproviders.FolderProviderFeatures;
import umich.ms.batmass.projects.types.metabolomics.MetabolomicsProject;
import umich.ms.batmass.projects.types.proteomics.ProteomicsProject;

/**
 *
 * @author Dmitry Avtonomov
 */
@NodeFactory.Registration(
    position = 200,
    projectType = {
        ProteomicsProject.TYPE,
        MetabolomicsProject.TYPE,
    }
)
@BMProjectSubfolderType(type = NodeFactoryFeatures.TYPE)
public class NodeFactoryFeatures extends BMNodeFactory<FolderProviderFeatures> {
    public static final String TYPE = "features";
    public static final String DISPLAY_NAME = "LC/MS features";

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String[] getActionPaths() {
        return super.getActionPaths();
    }

    @Override
    public Class<FolderProviderFeatures> getProjectSubfolderProviderClass() {
        return FolderProviderFeatures.class;
    }
}