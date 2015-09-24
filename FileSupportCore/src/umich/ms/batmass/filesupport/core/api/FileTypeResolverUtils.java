/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.util.Lookup;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.nbputils.LayerUtils;
import umich.ms.batmass.nbputils.lookup.LookupUtils;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class FileTypeResolverUtils {

    /**
     * A string representing any file type in the system.
     * @return
     */
    public static String getFileTypeAny() {
        return FileTypeResolver.TYPE_ANY;
    }

    /**
     * A string representing any file category.
     * @return
     */
    public static String getFileCategoryAny() {
        return FileTypeResolver.CATEGORY_ANY;
    }
    private FileTypeResolverUtils(){};
    
    
    /**
     * Find a {@link FileTypeResolver} supporting a given file. Category is not
     * taken into account.
     *
     * @param fileName your filename or file path, it will be fed to the
     * providers', that matched the {@code nodeInfoClass} to check if the file
     * is supported.
     *
     * @return an empty list, if nothing was found
     */
    public static List<FileTypeResolver> findTypeResolvers(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return Collections.emptyList();
        }
        String fileNameLoCase = fileName.toLowerCase();

        String layerPath = LayerUtils.getLayerPath(FileTypeResolver.LAYER_REGISTRATION_PATH);
        Lookup lkp = LookupUtils.getLookupForPath(layerPath);
        List<FileTypeResolver> found = new ArrayList<>();
        Collection<? extends FileTypeResolver> resolvers = lkp.lookupAll(FileTypeResolver.class);
        File file = new File(fileName);
        for (FileTypeResolver resolver : resolvers) {
            if (resolver.getFileFilter().accept(file)) {
                found.add(resolver);
            }    
        }
        return found;
    }

    /**
     * To be used in file-importing dialogs. There you need to find all resolvers
     * claiming a specific file category.
     * @param filePath
     * @param fileCategory
     * @return an empty list if nothing is found
     */
    public static List<FileTypeResolver> findTypeResolvers(String filePath, String fileCategory) {
        if (filePath == null || filePath.isEmpty()) {
            return Collections.emptyList();
        }
        String fileNameLoCase = filePath.toLowerCase();

        String layerPath = LayerUtils.getLayerPath(
                FileTypeResolver.LAYER_REGISTRATION_PATH, fileCategory);
        Lookup lkp = LookupUtils.getLookupForPath(layerPath);
        List<FileTypeResolver> found = new ArrayList<>();
        Collection<? extends FileTypeResolver> resolvers = lkp.lookupAll(FileTypeResolver.class);
        File file = new File(filePath);
        for (FileTypeResolver resolver : resolvers) {
            if (resolver.getFileFilter().accept(file)) {
                found.add(resolver);
            }
        }
        return found;
    }

    /**
     * To be used in file-importing dialogs. There you need to find all resolvers
     * claiming a specific file category.<br/>
     * @param fileCategory
     * @return an empty list if nothing is found
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<FileTypeResolver> getTypeResolvers(String fileCategory) {
        if (fileCategory == null || fileCategory.isEmpty()) {
            return Collections.emptyList();
        }
        String layerPath = LayerUtils.getLayerPath(
                FileTypeResolver.LAYER_REGISTRATION_PATH, fileCategory);
        Lookup lkp = LookupUtils.getLookupForPath(layerPath);

        
        Collection<? extends FileTypeResolver> resolvers = lkp.lookupAll(FileTypeResolver.class);
        List<FileTypeResolver> found;
        if (resolvers instanceof List) {
            found = (List<FileTypeResolver>) resolvers;
        } else {
            found = new ArrayList(resolvers);
        }

        return found;
    }

    /**
     * To be used in file-importing dialogs. There you need to find all resolvers
     * claiming a specific file category.<br/>
     * @param fileCategory
     * @param fileType
     * @return an empty list if nothing is found
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<FileTypeResolver> getTypeResolvers(String fileType, String fileCategory) {
        if (fileCategory == null || fileCategory.isEmpty() || fileType == null || fileType.isEmpty()) {
            return Collections.emptyList();
        }
        String layerPath = LayerUtils.getLayerPath(
                FileTypeResolver.LAYER_REGISTRATION_PATH, fileType, fileCategory);
        Lookup lkp = LookupUtils.getLookupForPath(layerPath);


        Collection<? extends FileTypeResolver> resolvers = lkp.lookupAll(FileTypeResolver.class);
        List<FileTypeResolver> found;
        if (resolvers instanceof List) {
            found = (List<FileTypeResolver>) resolvers;
        } else {
            found = new ArrayList(resolvers);
        }

        return found;
    }    
}
