/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.types.metabolomics.services;

import umich.ms.batmass.projects.core.services.spi.ProjectSubfolderActionPathsProvider;
import umich.ms.batmass.projects.services.folderproviders.FolderProviderLCMSFiles;

/**
 * TODO: WARNINIG: ACHTUNG: Delete this class! Was here for testing.
 * @author Dmitry Avtonomov
 */
//@ProjectServiceProvider(
//        service = ProjectSubfolderActionPathsProvider.class,
//        projectType = {MetabolomicsProject.TYPE}
//)
public class LCMSFilesFolderActionsExtensionProvider implements ProjectSubfolderActionPathsProvider {

    @Override
    public Class<?> getClassType() {
        return FolderProviderLCMSFiles.class;
    }

    @Override
    public String[] getPaths() {
        return new String[]{"Some/Fake/Path1", "Some/Fake/Path2",};
    }
    
}
