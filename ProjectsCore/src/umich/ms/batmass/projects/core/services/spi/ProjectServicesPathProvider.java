/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.services.spi;

import umich.ms.batmass.projects.core.type.BMProject;
import umich.ms.batmass.projects.core.util.BMProjectUtils;

/**
 * Just a convenience service that is only meant for Projects to look up
 * additional paths for their ProjectServiceProviders.
 * Example:
 * <pre>
 *
 * {@literal @}ProjectServiceProvider(
 *     service=ProjectServicesPathProvider.class,
 *     projectType=MetabolomicsProject.TYPE)
 * </pre>
 * @see BMProject#getType()
 * @see BMProjectUtils#getProjectType(java.lang.Class) 
 * @author dmitriya
 */
public interface ProjectServicesPathProvider extends LayerPathProvider {

}
