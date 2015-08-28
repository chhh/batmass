/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.services.spi;

/**
 * Provides a mapping from a class name to a path in the layer.xml.
 * Intended use: to register class-specific actions. You register your actions
 * in a folder of your choosing, and provide an implementation of this
 * interface in project's lookup.
 * @author dmitriya
 */
public interface LayerMappedPathProvider extends LayerPathProvider {
    /**
     * Class type for which the mapping is to be applied. Think of it as a capability.
     * @return
     */
    public Class<?> getClassType();
}
