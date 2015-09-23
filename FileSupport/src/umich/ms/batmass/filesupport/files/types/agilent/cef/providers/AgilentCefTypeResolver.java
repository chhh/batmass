/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.agilent.cef.providers;

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
        fileCategory = AgilentCefTypeResolver.CATEGORY,
        fileType = AgilentCefTypeResolver.TYPE
)
public class AgilentCefTypeResolver extends AbstractFileTypeResolver {
    private static final AgilentCefTypeResolver INSTANCE = new AgilentCefTypeResolver();

    @StaticResource
    public static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/resources/features_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);

    public static final String CATEGORY = "features";
    public static final String TYPE = "agilent-cef";
    protected static final String[] SUPPORTED_EXTS = {".cef"};
    protected static final String[] SUPPORTED_EXTS_LOWER_CASE;
    static {
        SUPPORTED_EXTS_LOWER_CASE = new String[SUPPORTED_EXTS.length];
        for (int i = 0; i < SUPPORTED_EXTS.length; i++) {
            SUPPORTED_EXTS_LOWER_CASE[i] = SUPPORTED_EXTS[i].toLowerCase(Locale.ENGLISH);
        }
    }

    public static AgilentCefTypeResolver getInstance() {
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
        if (!isPathLowerCase)
            path = path.toLowerCase();
        for (String ext : SUPPORTED_EXTS_LOWER_CASE) {
            if (path.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String[] getSupportedExtensions() {
        return SUPPORTED_EXTS;
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
        return true;
    }
}
