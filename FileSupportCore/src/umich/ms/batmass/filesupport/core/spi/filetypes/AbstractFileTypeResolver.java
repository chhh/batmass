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
package umich.ms.batmass.filesupport.core.spi.filetypes;

import java.util.Locale;
import java.util.Objects;

/**
 * Simple base class for implementations of FileTypeResolver.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractFileTypeResolver implements FileTypeResolver {
    
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
