/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.core.type.defaultoperations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.MoveOrRenameOperationImplementation;
import org.netbeans.spi.project.support.ProjectOperations;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import umich.ms.batmass.projects.core.type.BMProject;

/**
 *
 * @author dmitriya
 */
public class BMProjectMoveOrRenameoperation implements MoveOrRenameOperationImplementation {

    private final BMProject project;

    public BMProjectMoveOrRenameoperation(BMProject project) {
        this.project = project;
    }

    @Override
    public void notifyRenaming() throws IOException {

    }

    @Override
    public void notifyRenamed(String nueName) throws IOException {
        NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine("new name is", "In CustomerProjectMoveOrRenameOperation.notifyRenamed()");
        input.setInputText(nueName);
        Object result = DialogDisplayer.getDefault().notify(input);

        try {
            if (this == null) {
                throw new IllegalArgumentException("Project is null");
            }
            
            if (!ProjectOperations.isMoveOperationSupported(project)) {
                throw new IllegalArgumentException("Attempt to rename project that does not support move.");
            }

            PropertiesConfiguration config = project.getConfig();
            config.setProperty("project.name", nueName);
            config.save();

        } catch (ConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void notifyMoving() throws IOException {

    }

    @Override
    public void notifyMoved(Project original, File originalPath, String nueName) throws IOException {

    }

    @Override
    public List<FileObject> getMetadataFiles() {
        return new ArrayList<>();
    }

    @Override
    public List<FileObject> getDataFiles() {
        return new ArrayList<>();
    }
}
