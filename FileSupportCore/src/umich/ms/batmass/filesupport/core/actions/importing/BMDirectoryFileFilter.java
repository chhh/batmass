/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.actions.importing;

/**
 *
 * @author dmitriya
 */
public class BMDirectoryFileFilter extends BMFileFilter {

    public BMDirectoryFileFilter() {
        super(org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY);
    }

    @Override
    public String getShortDescription() {
        return "Dirs";
    }

    @Override
    public String getDescription() {
        return "Any directory";
    }

}
