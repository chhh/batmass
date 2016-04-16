/* 
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
