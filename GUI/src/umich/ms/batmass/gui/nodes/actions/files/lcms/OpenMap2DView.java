/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.nodes.actions.files.lcms;

import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import umich.ms.batmass.filesupport.core.spi.nodes.NodeInfo;
import umich.ms.batmass.gui.viewers.map2d.Map2DTopComponent;
import umich.ms.batmass.nbputils.SwingHelper;
import umich.ms.datatypes.LCMSData;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "BatMass/Nodes",
        id = "umich.ms.batmass.gui.nodes.actions.files.lcms.OpenMap2DView"
)
@ActionRegistration(
        displayName = "#CTL_OpenMap2DView",
        lazy = false
)
@ActionReferences({
    @ActionReference(
            path = NodeInfo.ACTIONS_LAYER_PATH_BASE + "/batmass-project-any/batmass-type-any/lcms/view",
            position = 400
    )
})
@NbBundle.Messages("CTL_OpenMap2DView=2D Map")
public class OpenMap2DView extends AbstractAction
        implements LookupListener, ContextAwareAction {

    private Lookup context;
    private volatile Lookup.Result<LCMSData> lkpResult;
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/view_map2d_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    public OpenMap2DView() {
        this(Utilities.actionsGlobalContext());
    }

    public OpenMap2DView(Lookup context) {
        this.context = context;
        putValue(Action.NAME, Bundle.CTL_OpenMap2DView());
        putValue(Action.SMALL_ICON, ICON);
    }

    protected void init() {
        assert SwingUtilities.isEventDispatchThread() : "This shall be called only from AWT thread (EDT)";

        Lookup.Result<LCMSData> tmp = lkpResult;
        if (tmp == null) {
            synchronized (this) {
                tmp = lkpResult;
                if (tmp == null) {
                    // The thing we want to listen for the presence or absence of
                    // in the global selection
                    tmp = context.lookupResult(LCMSData.class);
                    lkpResult = tmp;
                    tmp.addLookupListener(this);
                    resultChanged(null);
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        init();
        return lkpResult.allInstances().size() == 1;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpResult.allInstances().isEmpty());
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new OpenMap2DView(actionContext);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends LCMSData> result = lkpResult.allInstances();
        if (result.size() != 1) {
            throw new IllegalStateException("This action can only act when there"
                    + "is exactly one LCMSData object in global selection.");
        }
        final LCMSData data = result.iterator().next();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Map2DTopComponent tc = new Map2DTopComponent();
                tc.open();
                tc.setData(data);
            }
        };
        SwingHelper.invokeOnEDT(runnable);
    }


}
