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
package umich.ms.batmass.gui.examples.actions;

import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import umich.ms.batmass.nbputils.nodes.NodeSubmenu;

/**
 *
 * @author dmitriya
 */
//@ActionID(
//    category = "LCMSFileDesc",
//    id = "umich.ms.batmass.gui.lcmsfileactions.ViewSubmenuPresenter")
//@ActionRegistration(
//    displayName = "#CTL_ViewSubmenuPresenter",
//    lazy = false)
//@ActionReference(
//    path = "BatMass/Actions/Files/Categories/LCMSFiles",
//    position = 50)
@Messages("CTL_ViewSubmenuPresenter=View")
public class ViewSubmenuPresenter extends NodeSubmenu {
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(ViewSubmenuPresenter.class, "CTL_ViewSubmenuPresenter");
    }

    @Override
    public String getLayerPath() {
        return "LCMSFileDesc/View";
    }
}
