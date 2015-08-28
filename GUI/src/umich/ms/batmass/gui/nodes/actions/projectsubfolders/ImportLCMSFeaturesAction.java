/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.nodes.actions.projectsubfolders;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import umich.ms.batmass.filesupport.core.actions.importing.ImportFileByCategory;
import umich.ms.batmass.projects.nodes.NodeFactoryFeatures;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "ProjectSubnodes",
        id = "umich.ms.batmass.gui.nodes.projectsubfolders.actions.ImportLCMSFeaturesAction"
)
@ActionRegistration(
        displayName = "#CTL_ImportLCMSFeaturesAction",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "ProjectSubfolders/" + NodeFactoryFeatures.TYPE + "/Actions")
})
@NbBundle.Messages("CTL_ImportLCMSFeaturesAction=Import LC/MS Features")
public class ImportLCMSFeaturesAction extends ImportFileByCategory {

    public ImportLCMSFeaturesAction() {
        super();
    }

    public ImportLCMSFeaturesAction(Lookup context) {
        super(context);
    }

    @Override
    public String getActionName() {
        return Bundle.CTL_ImportLCMSFeaturesAction();
    }

    @Override
    public String getFileCategory() {
        return "features";
    }

    @Override
    public String getCategoryDisplayName() {
        return "LC/MS Features";
    }

}