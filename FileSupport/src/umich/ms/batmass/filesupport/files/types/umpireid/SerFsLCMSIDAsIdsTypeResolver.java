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
