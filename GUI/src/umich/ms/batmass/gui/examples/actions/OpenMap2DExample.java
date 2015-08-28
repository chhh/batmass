/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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