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
import umich.ms.batmass.gui.viewers.spectrum.SpectraViewerTopComponent;
import umich.ms.batmass.nbputils.actions.AbstractContextAwareAction;
import umich.ms.datatypes.LCMSData;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "BatMass/Nodes",
        id = "umich.ms.batmass.gui.nodes.actions.files.lcms.OpenSpectrumView"
)
@ActionRegistration(
        displayName = "#CTL_OpenSpectrumView",
        lazy = false
)
@ActionReferences({
    @ActionReference(
            path = NodeInfo.ACTIONS_LAYER_PATH_BASE + "/batmass-project-any/batmass-type-any/lcms/view",
            position = 200
    )
})
@NbBundle.Messages("CTL_OpenSpectrumView=Spectrum")
public class OpenSpectrumView extends AbstractContextAwareAction<LCMSData> {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/view_spectrum_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    public OpenSpectrumView() {
        this(Utilities.actionsGlobalContext());
    }

    public OpenSpectrumView(Lookup context) {
        super(context);
        putValue(Action.NAME, Bundle.CTL_OpenSpectrumView());
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
        final SpectraViewerTopComponent tc = new SpectraViewerTopComponent();
        tc.open(); // this action is synchronous, it's safe to do WDT stuff


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
