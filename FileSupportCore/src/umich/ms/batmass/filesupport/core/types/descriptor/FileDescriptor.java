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
package umich.ms.batmass.filesupport.core.types.descriptor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import umich.ms.batmass.nbputils.PathUtils;

/**
 *
 * @author dmitriya
 */
public class FileDescriptor extends Descriptor {
    /** File extension that will be given to this file descriptor */
    public static final String EXT = "file_desc";
    /** Descriptor type, just future-proofing */
    private static final String TYPE = FileDescriptor.class.getSimpleName();

    
    
    /** Path to the original file this descriptor links to. */
    private final Path path;
    public static final String PROP_PATH = "path";
    
    
    //=======   PROPERTIES   =============================================
    public static final String UNKNOWN_TYPE = "UNKNOWN_TYPE";
    public static final String UNKNOWN_CATEGORY = "UNKNOWN_CATEGORY";
    
    /** The size of the original file, can be used to check if it's been modified. */    
    private final long size;
    public static final String PROP_SIZE = "size";
    
    /** Paths of child descriptors. */
    private List<Path> children;
    public static final String PROP_CHILDREN = "children";
    
//    /**
//     * The name of the parser, that originally claimed this file during importing.
//     * This is needed, because there might be several different importing
//     * functions, which will search for different types of parsers. E.g. check
//     * {@code ImportLCMSFilesAction (umich.ms.batmass.filesupport.actions.importing)
//     * in FileSupport(BatMass) module} looks for {@link FileParser}s only, but there
//     * might be another action, which will load, say, mzXML files using a different
//     * type of parser. This information needs to be stored, and this is the place
//     * where it's stored.
//     */
//    private String parserName;
//    public static final String PROP_PARSER_NAME = "parser_name";
    
    /**
     * The type of the file, as is returned by the {@link FileTypeResolver}
     * returned by the parser, that claimed this file.
     */
    private String fileType = UNKNOWN_TYPE;
    public static final String PROP_FILE_TYPE = "file_type";
    
    /**
     * The category of the file, as is returned by the {@link FileTypeResolver}
     * returned by the parser, that claimed this file.
     */
    private String fileCategory = UNKNOWN_CATEGORY;
    public static final String PROP_FILE_CATEGORY = "file_category";


    public FileDescriptor(Path path, long size) {
        this.path = path;
        this.size = size;
        this.children = new ArrayList<>();
        this.UID = calcUID(path.getFileName().toString());
    }

    /**
     * This constructor should only be used, if you already have a UID for the
     * descriptor, e.g. when de-serializing the descriptor from disk.
     * @param path
     * @param size
     * @param children
     * @param parserName
     * @param UID 
     */
    public FileDescriptor(Path path, long size, List<Path> children, String UID) {
        this.path = path;
        this.size = size;
        this.children = children == null ? new ArrayList<Path>() : children;
        this.UID = UID;
    }
    
    
    /**
     * Creates the name for the file descriptor that is to be made for
     * {@code file} in the {@code destinationFolder} directory.
     * @param file the file, for which the Descriptor is to be created
     * @return 
     */
    public static String chooseNameForDescriptor(File file) {
        String nameExt = file.getName();
        String filename = PathUtils.filename(nameExt, EXT);
        return filename;
    }
    

    /**
     * Path to the original file this descriptor links to.
     * @return
     */
    public Path getPath() {
        return path;
    }

    /**
     * The size of the original file, can be used to check if it's been modified.
     * @return
     */
    public long getSize() {
        return size;
    }

    /**
     * The type of the descriptor. This can be used for cases when the same file
     * type can be used by multiple parsers. The default strategy to be used is
     * to use the simple class name (i.e. not the fully qualified name, but just
     * the name of the class).
     * @return the type
     */
    @Override
    public String getDescriptorType() {
        return TYPE;
    }

    /**
     * Paths of child descriptors.
     * @return
     */
    public List<Path> getChildren() {
        return children;
    }

    /**
     * Paths of child descriptors.
     * @param children can't be null
     */
    public void setChildren(List<Path> children) {
        if (children == null)
            throw new IllegalArgumentException("Children list can't be null.");
        this.children = children;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
    }

    /**
     * Recreates a FileDescriptor from a properties based file.
     * @param file The .file_desc file. It's up to you to check it's existence.
     * @return 
     * @throws org.apache.commons.configuration.ConfigurationException 
     */
    public static FileDescriptor readFromFile(File file) throws ConfigurationException {
        PropertiesConfiguration conf = new PropertiesConfiguration();
        conf.load(file);
        String uid = conf.getString(PROP_UID);
        String path = conf.getString(PROP_PATH);
        long size = conf.getLong(PROP_SIZE);
//        String parserType = conf.getString(PROP_PARSER_NAME);
        String[] children = conf.getStringArray(PROP_CHILDREN);
        String fileCategory = conf.getString(PROP_FILE_CATEGORY);
        String fileType = conf.getString(PROP_FILE_TYPE);
        ArrayList<Path> childPathList = new ArrayList<>(children.length);
        for (String childPath : children) {
            childPathList.add(Paths.get(childPath));
        }
        FileDescriptor desc = new FileDescriptor(Paths.get(path), size,
                childPathList, uid);
        desc.setFileCategory(fileCategory);
        desc.setFileType(fileType);
        return desc;
    }
    
    /**
     * Saves a FileDescriptor to a properties based file.
     * @param file File to save the descriptor to
     * @param desc Descriptor to be saved
     * @throws org.apache.commons.configuration.ConfigurationException
     */
    public static void writeToFile(File file, FileDescriptor desc) throws ConfigurationException {
        PropertiesConfiguration conf = new PropertiesConfiguration(file);
        conf.setProperty(PROP_UID, desc.getUID());
        conf.setProperty(PROP_PATH, desc.getPath().toString());
        conf.setProperty(PROP_SIZE, desc.getSize());
        conf.setProperty(PROP_FILE_CATEGORY, desc.getFileCategory());
        conf.setProperty(PROP_FILE_TYPE, desc.getFileType());
        List<Path> children = desc.getChildren();
        String[] paths = new String[children.size()];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = children.get(i).toString();
        }
        conf.setProperty(PROP_CHILDREN, paths);
        conf.save();
    }
}
