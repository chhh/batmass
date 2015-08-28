/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.types.metabolomics.services;

import umich.ms.batmass.projects.core.services.spi.ProjectServicesPathProvider;

/**
 * TODO: WARNINIG: ACHTUNG: Delete this class! Was here for testing.
 * @author dmitriya
 */
//@ProjectServiceProvider(
//        service=ProjectServicesPathProvider.class,
//        projectType={MetabolomicsProject.TYPE}
//)
public class GenericServicesPathProvider implements ProjectServicesPathProvider {

    @Override
    public String[] getPaths() {
        return new String[]{"Projects/extensions/Lookup"};
    }

}
