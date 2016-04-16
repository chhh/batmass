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
package umich.ms.batmass.filesupport.core.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.ConfigurationException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptor;
import umich.ms.batmass.nbputils.PathUtils;

/**
 * This class creates a file-descriptor file in the project's sub-folder.
 * @author dmitriya
 */
public abstract class ExternalFileImporter {

    /**
     * Should create a descriptor file in the project under a sub-folder.
     * E.g. <code>$PROJ_DIR/msfiles/example.mzxml.file_desc</code> for
     * <code>C:/example.mzxml</code>.<br/>
     * (NOTE: for the exact extension used for the descriptors, see
     * {@link FileDescriptor#EXT}).<br/>
     * Use ProjectSubfolderProvider to get a concrete sub-folder in the project,
     * never search for a folder in a project manually.
     * @param origFile java.io.File for the original file being imported
     * @param destinationFolder a directory in $PROJECT_DIR into which to import the file
     * @param fileType can be null, but then the import wont be too useful
     * @param fileCategory can be null, but then the import wont be too useful
     * @return the newly created descriptor file or
     * @throws java.nio.file.FileAlreadyExistsException
     * @throws org.apache.commons.configuration.ConfigurationException
     */
    public static FileObject importFile(File origFile, DataFolder destinationFolder,
            String fileType, String fileCategory)
            throws FileAlreadyExistsException, IOException, ConfigurationException {
        // first create java.io.File in that DataFolder, and check if the actual file exists;
        FileObject origFo = FileUtil.toFileObject(origFile);
        long origFileSize = origFo.getSize();

        String origFileName = origFo.getNameExt();
        String descFileExt = (FileDescriptor.EXT).toLowerCase();
        String descFileName = PathUtils.filename(origFileName, descFileExt);
        
        
        FileObject descFo = destinationFolder.getPrimaryFile().getFileObject(origFileName, descFileExt);
        // if the descriptor does not yet exist for this orignial file, then create it,
        // if the file is already there, just skip it
        //
        // TODO: we should ask the user, if he wants to delete existing file
        //       and reimport a new copy, deleting all the associated processing
        //       data and the likes.
        if (descFo != null) {
            throw new FileAlreadyExistsException(
                    origFile.getAbsolutePath(),                 // imported file
                    FileUtil.toFile(descFo).getAbsolutePath(),  // the descriptor we wanted to craete
                    "The file has already been imported into that directory"); // reason for error
        }

        // the folder into which we're importing
        File folderFile = FileUtil.toFile(destinationFolder.getPrimaryFile());
        // full path of the file, into which we'll store the descriptor
        Path descFilePath = Paths.get(folderFile.getAbsolutePath(), descFileName);

        // create and save the descriptor
        FileDescriptor desc = new FileDescriptor(origFile.toPath().toAbsolutePath(), origFileSize);
        if (fileType != null) {
            desc.setFileType(fileType);
        }
        if (fileCategory != null) {
            desc.setFileCategory(fileCategory);
        }
        FileDescriptor.writeToFile(descFilePath.toFile(), desc);
        
        
        // craete the FileObject and hope that ProjectExplorer sees it
        descFo = FileUtil.createData(descFilePath.toFile());
        return descFo;
    }
    
}
