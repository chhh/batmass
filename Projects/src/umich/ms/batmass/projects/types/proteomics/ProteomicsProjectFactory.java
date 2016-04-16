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
