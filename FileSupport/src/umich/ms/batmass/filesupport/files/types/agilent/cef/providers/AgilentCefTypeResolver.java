/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.agilent.cef.providers;

import java.io.File;
import javax.swing.ImageIcon;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
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
    protected static final String EXT = ".cef";
    protected static final BMFileFilter FILE_FILTER = new AgilentCefFileFilter();
    protected static final String DESCRIPTION = "Agilent .cef files from MassHunter MFE";
    
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

    @Override
    public BMFileFilter getFileFilter() {
        return FILE_FILTER;
    }

    public static class AgilentCefFileFilter extends BMFileFilter {

        public AgilentCefFileFilter() {
            super(FileFilterUtils.suffixFileFilter(EXT, IOCase.INSENSITIVE));
        }

        @Override
        public String getShortDescription() {
            return EXT;
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
