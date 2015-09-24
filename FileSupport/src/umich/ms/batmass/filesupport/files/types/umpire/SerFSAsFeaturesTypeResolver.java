/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.FileSystemNotFoundException;
import javax.swing.ImageIcon;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import umich.ms.batmass.filesupport.core.actions.importing.BMFileFilter;
import umich.ms.batmass.filesupport.core.annotations.FileTypeResolverRegistration;
import umich.ms.batmass.filesupport.core.spi.filetypes.AbstractFileTypeResolver;

/**
 *
 * @author Dmitry Avtonomov
 */
@FileTypeResolverRegistration(
        fileCategory = SerFSAsFeaturesTypeResolver.CATEGORY,
        fileType = SerFSAsFeaturesTypeResolver.TYPE
)
public class SerFSAsFeaturesTypeResolver extends AbstractFileTypeResolver {
    private static final SerFSAsFeaturesTypeResolver INSTANCE = new SerFSAsFeaturesTypeResolver();

    @StaticResource
    public static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/resources/features_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);

    public static final String CATEGORY = "features";
    public static final String TYPE = "umpire";
    protected static final String EXT = ".mzXML";
    protected static final BMFileFilter FILE_FILTER = new UmpireSerFsFileFilter();
    protected static final String DESCRIPTION = "DIA Umpire detected features. serFS files in a folder.";
    protected static final String SHORT_DESC = ".serFS in dir";

    public static SerFSAsFeaturesTypeResolver getInstance() {
        return INSTANCE;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ImageIcon getIcon() {
        return ICON;
    }

    @Override
    public String getIconPath() {
        return ICON_BASE_PATH;
    }

    @Override
    public boolean isFileOnly() {
        return false;
    }

    @Override
    public BMFileFilter getFileFilter() {
        return FILE_FILTER;
    }
    
    public static class UmpireSerFsFileFilter extends BMFileFilter {

        public UmpireSerFsFileFilter() {
            super(new IOFileFilter() {

                @Override
                public boolean accept(File dir) {
                    try {
                        //check for serfs files
                        if (dir.isHidden())
                            return false;
                        if (dir.isDirectory()) {
                            String[] list = dir.list(new FilenameFilter() {
                                @Override public boolean accept(File dir, String name) {
                                    return name.toLowerCase().endsWith("peakcluster.serfs");
                                }
                            });
                            if (list != null && list.length >= 1) {
                                return true;
                            }
                        }
                    } catch (FileSystemNotFoundException | IllegalArgumentException ex) {
                        // we don't care if errors occur here, just return false
                        System.err.printf("Bad path given to SerFSFeaturesTypeResolver: %s\n", dir.getAbsolutePath());
                    }
                    return false;
                }

                @Override
                public boolean accept(File dir, String name) {
                    // this only accepts directories
                    return false;
                }
            });
        }

        @Override public String getShortDescription() {
            return SHORT_DESC;
        }

        @Override public String getDescription() {
            return DESCRIPTION;
        }
    }
}
