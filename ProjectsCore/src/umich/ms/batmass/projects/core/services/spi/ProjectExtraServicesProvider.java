/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.services.spi;

/**
 *
 * @author dmitriya
 */
@LayerPathProvider.Registration(paths = {"SomePath/Subfolder/Extensions"})
public class ProjectExtraServicesProvider implements LayerPathProvider {

    @Override
    public String[] getPaths() {
        return new String[]{"SomeExtras/Subfolder/Extensions/Hoho"};
    }

}
