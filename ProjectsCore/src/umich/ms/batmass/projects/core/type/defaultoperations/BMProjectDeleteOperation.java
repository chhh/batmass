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
