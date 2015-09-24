/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.core.actions.importing;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;

/**
 *
 * @author Dmitry Avtonomov
 */
public class BMSuffixFileFilter extends BMFileFilter {
    protected String shortDesc;
    protected String desc;
    protected String ext;

    public BMSuffixFileFilter(String ext, String shortDesc, String desc) {
        super(FileFilterUtils.suffixFileFilter(ext, IOCase.INSENSITIVE));
        this.ext = ext;
        this.desc = desc;
        this.shortDesc = shortDesc;
    }

    @Override
    public String getShortDescription() {
        return shortDesc;
    }

    @Override
    public String getDescription() {
        return desc;
    }

}
