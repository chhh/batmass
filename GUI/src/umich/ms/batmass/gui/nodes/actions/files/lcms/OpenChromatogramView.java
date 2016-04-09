/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.nodes.actions.files.lcms;

import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import umich.ms.batmass.filesupport.core.spi.nodes.NodeInfo;
import umich.ms.batmass.gui.viewers.chromatogram.ChromatogramViewTopComponent;
import umich.ms.batmass.nbputils.actions.AbstractContextAwareAction;
import umich.ms.datatypes.LCMSData;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "BatMass/Nodes",
        id = "umich.ms.batmass.gui.nodes.actions.files.lcms.OpenChromatogramView"
)
@ActionRegistration(
        displayName = "#CTL_OpenChromatogramView",
        lazy = false
)
@ActionReferences({
    @ActionReference(
            path = NodeInfo.ACTIONS_LAYER_PATH_BASE + "/batmass-project-any/batmass-type-any/lcms/view",
            position = 300
    )
})
@NbBundle.Messages("CTL_OpenChromatogramView=Chromatogram")
public class OpenChromatogramView extends AbstractContextAwareAction<LCMSData> {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/view_chromatogram_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    public OpenChromatogramView() {
        this(Utilities.actionsGlobalContext());
    }

    public OpenChromatogramView(Lookup context) {
        super(context);
        putValue(Action.NAME, Bundle.CTL_OpenChromatogramView());
        putValue(Action.SMALL_ICON, ICON);
    }


    @Override
    public Class<LCMSData> getActivationClass() {
        return LCMSData.class;
    }

    @Override
    public boolean isActivated(Collection<? extends LCMSData> instances) {
        return instances.size() == 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends LCMSData> result = this.getLookupResult();
        if (result.size() != 1) {
            throw new IllegalStateException("This action can only act when there"
                    + "is exactly one LCMSData object in global selection.");
        }
        final LCMSData data = result.iterator().next();
        final ChromatogramViewTopComponent tc = new ChromatogramViewTopComponent();
        tc.open();


        // setData() should not be called from EDT
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tc.setData(data);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        
    }

}