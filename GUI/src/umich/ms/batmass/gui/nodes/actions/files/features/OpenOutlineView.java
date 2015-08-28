/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.nodes.actions.files.features;

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
import umich.ms.batmass.filesupport.files.types.umpire.data.features.UmpireFeaturesNodeFactoryData;
import umich.ms.batmass.gui.viewers.outline.OutlineTopComponent;
import umich.ms.batmass.nbputils.actions.AbstractContextAwareAction;

/**
 *
 * @author Dmitry Avtonomov
 */

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "BatMass/Nodes",
        id = "umich.ms.batmass.gui.nodes.actions.files.features.OpenOutlineView"
)
@ActionRegistration(
        displayName = "#CTL_OpenOutlineView",
        lazy = false
)
@ActionReferences({
    @ActionReference(
            path = NodeInfo.ACTIONS_LAYER_PATH_BASE + FeaturesSubmenuView.LAYER_REL_PATH + FeaturesSubmenuView.LAYER_REL_PATH_SUBFOLDER,
            position = 520
    )
})
@NbBundle.Messages("CTL_OpenOutlineView=Outline")
@SuppressWarnings({"rawtypes"})
public class OpenOutlineView extends AbstractContextAwareAction<UmpireFeaturesNodeFactoryData> {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/list.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);


    public OpenOutlineView() {
        this(Utilities.actionsGlobalContext());
    }

    public OpenOutlineView(Lookup context) {
        super(context);
        putValue(Action.NAME, Bundle.CTL_OpenOutlineView());
        putValue(Action.SMALL_ICON, ICON);
    }


    @Override
    public Class<UmpireFeaturesNodeFactoryData> getActivationClass() {
        return UmpireFeaturesNodeFactoryData.class;
    }

    @Override
    public boolean isActivated(Collection<? extends UmpireFeaturesNodeFactoryData> instances) {
        return instances.size() == 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends UmpireFeaturesNodeFactoryData> result = this.getLookupResult();
        if (result.size() != 1) {
            throw new IllegalStateException("This action can only act when there"
                    + "is exactly one FeatureTableModelData object in global selection.");
        }
        final UmpireFeaturesNodeFactoryData data = result.iterator().next();


        OutlineTopComponent tc = new OutlineTopComponent();
        tc.open();
        tc.setData(data);

//        // setData() should not be called from EDT
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();

    }

}
