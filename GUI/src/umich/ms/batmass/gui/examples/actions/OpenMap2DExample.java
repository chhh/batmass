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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import umich.ms.batmass.gui.viewers.map2d.Map2DTopComponent;
import umich.ms.batmass.nbputils.SwingHelper;
import umich.ms.datatypes.LCMSData;


/**
 * Example of an old action, which is presumably context sensitive to a single
 * LCMSData object being in the global selection.
 * @author Dmitry Avtonomov
 */
//@ActionID(
//        category = "MSFile",
//        id = "umich.gui.project.actions.msfile.MSFileOpenMap2DViewAction"
//)
//@ActionRegistration(
//        iconBase = "umich/gui/project/actions/msfile/map2d_icon.png",
//        displayName = "#CTL_MSFileOpenMap2DViewAction",
//        asynchronous = true
//)
//@ActionReference(path = "Menu/MS File", position = 3433)
//@Messages("CTL_MSFileOpenMap2DViewAction=Open 2D map")
public final class OpenMap2DExample implements ActionListener {

    private final LCMSData file;

    public OpenMap2DExample(LCMSData context) {
        this.file = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Map2DTopComponent tc = new Map2DTopComponent();
                tc.open();
                tc.setData(file);
            }
        };
        SwingHelper.invokeOnEDT(runnable);
    }
}