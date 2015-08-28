/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
