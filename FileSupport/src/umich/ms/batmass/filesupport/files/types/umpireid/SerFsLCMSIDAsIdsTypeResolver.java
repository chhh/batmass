/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpireid;

import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import umich.ms.batmass.filesupport.core.annotations.FileTypeResolverRegistration;
import umich.ms.batmass.filesupport.core.spi.filetypes.AbstractFileTypeResolver;

/**
 *
 * @author Dmitry Avtonomov
 */
@FileTypeResolverRegistration(
        fileCategory = SerFsLCMSIDAsIdsTypeResolver.CATEGORY,
        fileType = SerFsLCMSIDAsIdsTypeResolver.TYPE
)
public class SerFsLCMSIDAsIdsTypeResolver extends AbstractFileTypeResolver {
    public static final String CATEGORY = "pep_id";
    public static final String TYPE = "umpire_id";
    private static final SerFsLCMSIDAsIdsTypeResolver INSTANCE = new SerFsLCMSIDAsIdsTypeResolver();

    @StaticResource
    public static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/resources/id_icon_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);


    protected static final String[] SUPPORTED_EXTS = {"serFS"};
    protected static final String FILE_NAME_ENDING_LO_CASE = "_lcmsid.serfs";
    protected static final String[] SUPPORTED_EXTS_LOWER_CASE;
    static {
        SUPPORTED_EXTS_LOWER_CASE = toLowerCaseExts(SUPPORTED_EXTS);
    }

    public static SerFsLCMSIDAsIdsTypeResolver getInstance() {
        return INSTANCE;
    }

    @Override
    public String[] getSupportedExtensions() {
        return SUPPORTED_EXTS;
    }

//    @Override
//    public boolean getSupportsFolders() {
//        return false;
//    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getIconPath() {
        return ICON_BASE_PATH;
    }

    @Override
    public ImageIcon getIcon() {
        return ICON;
    }

    @Override
    public boolean accepts(String path, boolean isPathLowerCase) {
        if (!isPathLowerCase) {
            path = path.toLowerCase();
        }
        return path.endsWith(FILE_NAME_ENDING_LO_CASE);
    }
}
