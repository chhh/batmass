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
package umich.ms.batmass.filesupport.core.actions.importing;

import java.io.File;
import java.util.List;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;
import umich.ms.batmass.filesupport.core.api.FileTypeResolverUtils;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;

/**
 *
 * @author Dmitry Avtonomov
 */
public class BMFileView extends FileView {
    String fileCategory;
    List<FileTypeResolver> resolvers;

    public BMFileView(String fileCategory) {
        this.fileCategory = fileCategory;
        resolvers = FileTypeResolverUtils.getTypeResolvers(fileCategory);
    }


    @Override
    public Boolean isTraversable(File f) {
        return super.isTraversable(f);
    }

    @Override
    public Icon getIcon(File f) {
        String fNameLoCase = f.getAbsolutePath().toLowerCase(Locale.ENGLISH);
        if (f.isHidden())
            return super.getIcon(f);
        for (FileTypeResolver resolver : resolvers) {
            if (f.isDirectory() && resolver.isFileOnly())
                continue;
            if (resolver.getFileFilter().accept(f)) {
                return resolver.getIcon();
            }
        }

        return super.getIcon(f);
    }

    @Override
    public String getTypeDescription(File f) {
        return super.getTypeDescription(f);
    }

    @Override
    public String getDescription(File f) {
        return super.getDescription(f);
    }

    @Override
    public String getName(File f) {
        return super.getName(f);
    }

}
