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
package umich.ms.batmass.diaumpire.providers;

import javax.swing.ImageIcon;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
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
        fileCategory = UmpireSeTypeResolver.CATEGORY,
        fileType = UmpireSeTypeResolver.TYPE
)
public class UmpireSeTypeResolver extends AbstractFileTypeResolver {
    private static final UmpireSeTypeResolver INSTANCE = new UmpireSeTypeResolver();

    @StaticResource
    public static final String ICON_BASE_PATH = "umich/ms/batmass/diaumpire/resources/features_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);

    public static final String CATEGORY = "features";
    public static final String TYPE = "dia-umpire-se";
    protected static final String EXT = "_PeakCluster.csv";
    protected static final BMFileFilter FILE_FILTER = new UmpireSeFileFilter();
    protected static final String DESCRIPTION = "DIA-Umpire Signal Extraction peak clusters";
    
    public static UmpireSeTypeResolver getInstance() {
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
    
    public static class UmpireSeFileFilter extends BMFileFilter {

        public UmpireSeFileFilter() {
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