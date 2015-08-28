/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.type.defaultoperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.openide.filesystems.FileObject;
import umich.ms.batmass.projects.core.type.BMProject;

/**
 *
 * @author dmitriya
 */
public class BMProjectDeleteOperation implements DeleteOperationImplementation {
    private final BMProject project;

    public BMProjectDeleteOperation(BMProject project) {
        this.project = project;
    }

    @Override
    public List<FileObject> getMetadataFiles() {
        List<FileObject> files = new ArrayList<>();
        FileObject[] children = project.getProjectDirectory().getChildren();
        for (FileObject fileObject : children) {
            addFile(project.getProjectDirectory(), fileObject.getNameExt(), files);
        }
        return files;
    }
    private void addFile(FileObject projectDirectory, String fileName, List<FileObject> result) {
       FileObject file = projectDirectory.getFileObject(fileName);
       if (file != null) {
          result.add(file);
       }
    }

    @Override
    public List<FileObject> getDataFiles() {
        return new ArrayList<>();
    }
    @Override
    public void notifyDeleting() throws IOException {
    }
    @Override
    public void notifyDeleted() throws IOException {
    }
}
