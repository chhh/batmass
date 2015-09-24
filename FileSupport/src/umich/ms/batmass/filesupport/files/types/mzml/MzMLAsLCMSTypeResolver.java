/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.mzml;

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
    protected static final String EXT = ".mzML";
    protected static final String SHORT_DESC = ".mzML";
    protected static final String DESCRIPTION = "mzML files";
    protected static final BMFileFilter FILE_FILTER = new BMSuffixFileFilter(EXT, SHORT_DESC, DESCRIPTION);

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
}
