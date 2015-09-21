/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
            if (resolver.accepts(fNameLoCase, true)) {
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
