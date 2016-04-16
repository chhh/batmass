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

/**
 * This is to be used by FileTypeResolvers, use ApacheCommons IO FileFilters
 * in the constructor, they provide almost anything you might want. <br/>
 * {@code org.apache.commons.io.filefilter.SuffixFileFilter} is likely the most useful one.
 * @author Dmitry Avtonomov
 */
public abstract class BMFileFilter extends javax.swing.filechooser.FileFilter {
    protected org.apache.commons.io.filefilter.IOFileFilter fileFilter;

    public BMFileFilter(org.apache.commons.io.filefilter.IOFileFilter ff) {
        this.fileFilter = ff;
    }

    public org.apache.commons.io.filefilter.IOFileFilter getFileFilter() {
        return fileFilter;
    }
    
    @Override
    public boolean accept(File f) {
        return fileFilter.accept(f);
    }
    
    /**
     * This should be more like the set of extensions or something like this, that it accepts.
     * @return short string
     */
    public abstract String getShortDescription();
}
