/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.spi.filetypes;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

/**
 * Simple base class for implementations of FileTypeResolver.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractFileTypeResolver implements FileTypeResolver {
    
    protected boolean accepts(String[] acceptedExtsLoCase, String fileName, boolean isFileNameLoCase) {
        if (!isFileNameLoCase) {
            fileName = fileName.toLowerCase(Locale.ENGLISH);
        }
        for (String ext : acceptedExtsLoCase) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    protected boolean accepts(String[] acceptedExtsLoCase, File file) {
        if (file.isDirectory()) {
            return false;
        }
        String fileName = file.getName().toLowerCase(Locale.ENGLISH);
        for (String ext : acceptedExtsLoCase) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean matches(FileTypeResolver other) {
        if (!this.getCategory().equals(CATEGORY_ANY) && !other.getCategory().equals(CATEGORY_ANY)) {
            if (!this.getCategory().equals(other.getCategory())) {
                return false;
            }
        }
        
        if (!this.getType().equals(TYPE_ANY) && !other.getType().equals(TYPE_ANY)) {
            if (!this.getType().equals(other.getType())) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.getCategory());
        hash = 23 * hash + Objects.hashCode(this.getType());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileTypeResolver other = (FileTypeResolver) obj;
        if (!Objects.equals(this.getCategory(), other.getCategory())) {
            return false;
        }
        if (!Objects.equals(this.getType(), other.getType())) {
            return false;
        }
        return true;
    }

    protected static String[] toLowerCaseExts(final String[] exts) {
        String[] loCase = new String[exts.length];
        for (int i = 0; i < exts.length; i++) {
            loCase[i] = exts[i].toLowerCase(Locale.ENGLISH);
        }
        return loCase;
    }
}
