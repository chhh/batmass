/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.nodes;

import org.netbeans.spi.project.ui.support.NodeFactory;
import umich.ms.batmass.projects.core.annotations.BMProjectSubfolderType;
import umich.ms.batmass.projects.core.nodes.spi.BMNodeFactory;
import umich.ms.batmass.projects.services.folderproviders.FolderProviderSupplementary;
import umich.ms.batmass.projects.types.proteomics.ProteomicsProject;

/**
 *
 * @author Dmitry Avtonomov
 */
@NodeFactory.Registration(
    position = 400,
    projectType = {
        ProteomicsProject.TYPE,
    }
)
@BMProjectSubfolderType(type = NodeFactorySupplementary.TYPE)
public class NodeFactorySupplementary extends BMNodeFactory<FolderProviderSupplementary> {
    public static final String TYPE = "supplementary";
    public static final String DISPLAY_NAME = "Other files";

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String[] getActionPaths() {
        return new String[0];
    }

    @Override
    public Class<FolderProviderSupplementary> getProjectSubfolderProviderClass() {
        return FolderProviderSupplementary.class;
    }
}
