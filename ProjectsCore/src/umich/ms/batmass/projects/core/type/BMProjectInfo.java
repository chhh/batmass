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
package umich.ms.batmass.projects.core.type;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import org.apache.commons.configuration.Configuration;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;

/**
 * Implementation of project system's ProjectInformation class.
 */
public class BMProjectInfo implements ProjectInformation {
    private final BMProject project;

    public BMProjectInfo(final BMProject project) {
        this.project = project;
    }

    @Override
    public Icon getIcon() {
        return project.getIcon();
    }

    @Override
    public String getName() {
        return project.getProjectDirectory().getName();
    }

    @Override
    public String getDisplayName() {
        Configuration conf = project.getConfig();
        String projName = conf.getString("project.name");
        return projName == null ? getName() : projName;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        //do nothing, won't change
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        //do nothing, won't change
    }

    @Override
    public Project getProject() {
        return project;
    }

}
