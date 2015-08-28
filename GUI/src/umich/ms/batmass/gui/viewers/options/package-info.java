/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
@OptionsPanelController.ContainerRegistration(
        id = "ViewerOptions",
        categoryName = "#OptionsCategory_Name_ViewerOptions",
        iconBase = "umich/ms/batmass/gui/viewers/options/resources/spectrum_32.png",
        keywords = "#OptionsCategory_Keywords_ViewerOptions",
        keywordsCategory = "ViewerOptions"
)
@NbBundle.Messages(
        value = {
            "OptionsCategory_Name_ViewerOptions=ViewerOptions",
            "OptionsCategory_Keywords_ViewerOptions=viewers"
        }
)
package umich.ms.batmass.gui.viewers.options;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
