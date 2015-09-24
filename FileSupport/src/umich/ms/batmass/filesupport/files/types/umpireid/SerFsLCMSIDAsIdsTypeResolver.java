package umich.ms.batmass.filesupport.files.types.umpireid;

import javax.swing.ImageIcon;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import umich.ms.batmass.filesupport.core.actions.importing.BMFileFilter;
import umich.ms.batmass.filesupport.core.actions.importing.BMSuffixFileFilter;
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
    protected static final String EXT = "_lcmsid.serfs";
    protected static final String DESCRIPTION = "DIA Umpire detected features. serFS files in a folder.";
    protected static final String SHORT_DESC = ".serFS in dir";
    protected static final BMFileFilter FILE_FILTER = new BMSuffixFileFilter(EXT, SHORT_DESC, DESCRIPTION);
    
    public static SerFsLCMSIDAsIdsTypeResolver getInstance() {
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
    public String getIconPath() {
        return ICON_BASE_PATH;
    }

    @Override
    public ImageIcon getIcon() {
        return ICON;
    }

    @Override
    public boolean isFileOnly() {
        return true;
    }

    @Override
    public BMFileFilter getFileFilter() {
        return FILE_FILTER;
    }
}
