/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.nodes;

import org.netbeans.spi.project.ui.support.NodeFactory;
import umich.ms.batmass.projects.core.annotations.BMProjectSubfolderType;
import umich.ms.batmass.projects.core.nodes.spi.BMNodeFactory;
import umich.ms.batmass.projects.services.folderproviders.FolderProviderPeptideIds;
import umich.ms.batmass.projects.types.proteomics.ProteomicsProject;

/**
 *
 * @author Dmitry Avtonomov
 */
@NodeFactory.Registration(
    position = 300,
    projectType = {
        ProteomicsProject.TYPE,
    }
)
@BMProjectSubfolderType(type = NodeFactoryPepIds.TYPE)
public class NodeFactoryPepIds  extends BMNodeFactory<FolderProviderPeptideIds> {
    public static final String TYPE = "pep_id";
    public static final String DISPLAY_NAME = "Identifications";

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String[] getActionPaths() {
        return super.getActionPaths();
    }

    @Override
    public Class<FolderProviderPeptideIds> getProjectSubfolderProviderClass() {
        return FolderProviderPeptideIds.class;
    }
}
