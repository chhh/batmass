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
import umich.ms.batmass.data.core.lcms.features.data.FeatureTableModelData;
import umich.ms.batmass.filesupport.core.spi.nodes.NodeInfo;
import umich.ms.batmass.gui.viewers.featuretable.FeatureTableTopComponent;
import umich.ms.batmass.nbputils.actions.AbstractContextAwareAction;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "BatMass/Nodes",
        id = "umich.ms.batmass.gui.nodes.actions.files.features.OpenFeature2DTable"
)
@ActionRegistration(
        displayName = "#CTL_OpenFeature2DTable",
        lazy = false
)
@ActionReferences({
    @ActionReference(
            path = NodeInfo.ACTIONS_LAYER_PATH_BASE + FeaturesSubmenuView.LAYER_REL_PATH + FeaturesSubmenuView.LAYER_REL_PATH_SUBFOLDER,
            position = 510
    )
})
@NbBundle.Messages("CTL_OpenFeature2DTable=Table")
@SuppressWarnings({"rawtypes"})
public class OpenFeature2DTable extends AbstractContextAwareAction<FeatureTableModelData> {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/list.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);


    public OpenFeature2DTable() {
        this(Utilities.actionsGlobalContext());
    }

    public OpenFeature2DTable(Lookup context) {
        super(context);
        putValue(Action.NAME, Bundle.CTL_OpenFeature2DTable());
        putValue(Action.SMALL_ICON, ICON);
    }


    @Override
    public Class<FeatureTableModelData> getActivationClass() {
        return FeatureTableModelData.class;
    }

    @Override
    public boolean isActivated(Collection<? extends FeatureTableModelData> instances) {
        return instances.size() == 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends FeatureTableModelData> result = this.getLookupResult();
        if (result.size() != 1) {
            throw new IllegalStateException("This action can only act when there"
                    + "is exactly one FeatureTableModelData object in global selection.");
        }
        final FeatureTableModelData data = result.iterator().next();


        final FeatureTableTopComponent tc = new FeatureTableTopComponent();
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
