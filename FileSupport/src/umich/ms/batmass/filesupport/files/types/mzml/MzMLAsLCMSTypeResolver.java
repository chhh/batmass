/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.mzml;

import java.util.Locale;
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
        fileCategory = MzMLAsLCMSTypeResolver.CATEGORY,
        fileType = MzMLAsLCMSTypeResolver.TYPE
)
public class MzMLAsLCMSTypeResolver extends AbstractFileTypeResolver {
    private static final MzMLAsLCMSTypeResolver INSTANCE = new MzMLAsLCMSTypeResolver();

    @StaticResource
    public static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/resources/spectrum_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);

    public static final String CATEGORY = "lcms";
    public static final String TYPE = "mzml";
    protected static final String[] SUPPORTED_EXTS = {"mzML"};
    protected static final String[] SUPPORTED_EXTS_LOWER_CASE;

    static {
        SUPPORTED_EXTS_LOWER_CASE = new String[SUPPORTED_EXTS.length];
        for (int i = 0; i < SUPPORTED_EXTS.length; i++) {
            SUPPORTED_EXTS_LOWER_CASE[i] = SUPPORTED_EXTS[i].toLowerCase(Locale.ENGLISH);
        }
    }

    public static MzMLAsLCMSTypeResolver getInstance() {
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
    public boolean accepts(String path, boolean isPathLowerCase) {
        return this.accepts(SUPPORTED_EXTS_LOWER_CASE, path, isPathLowerCase);
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
    public ImageIcon getIcon() {
        return ICON;
    }

    @Override
    public String getIconPath() {
        return ICON_BASE_PATH;
    }

    @Override
    public boolean isFileOnly() {
        return true;
    }
}
