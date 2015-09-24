/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.core.actions.importing;

import java.io.File;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * This is to be used by FileTypeResolvers, use ApacheCommons IO FileFilters
 * in the constructor, they provide almost anything you might want. <br/>
 * {@code org.apache.commons.io.filefilter.SuffixFileFilter} is likely the most useful one.
 * @author Dmitry Avtonomov
 */
public abstract class BMFileFilter extends javax.swing.filechooser.FileFilter {
    protected IOFileFilter fileFilter;

    public BMFileFilter(IOFileFilter ff) {
        this.fileFilter = ff;
    }

    public IOFileFilter getFileFilter() {
        return fileFilter;
    }
    
    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        return fileFilter.accept(f);
    }
    
    /**
     * This should be more like the set of extensions or something like this, that it accepts.
     * @return short string
     */
    public abstract String getShortDescription();
}
