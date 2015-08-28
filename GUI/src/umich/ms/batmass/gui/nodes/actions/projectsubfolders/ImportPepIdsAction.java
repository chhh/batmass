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
import umich.ms.batmass.projects.nodes.NodeFactoryPepIds;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "ProjectSubnodes",
        id = "umich.ms.batmass.gui.nodes.projectsubfolders.actions.ImportPepIdsAction"
)
@ActionRegistration(
        displayName = "#CTL_ImportPepIdsAction",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "ProjectSubfolders/" + NodeFactoryPepIds.TYPE + "/Actions")
})
@NbBundle.Messages("CTL_ImportPepIdsAction=Import IDs")
public class ImportPepIdsAction extends ImportFileByCategory {

    public ImportPepIdsAction() {
    }

    public ImportPepIdsAction(Lookup context) {
        super(context);
    }
    
    @Override
    public String getActionName() {
        return Bundle.CTL_ImportPepIdsAction();
    }

    @Override
    public String getFileCategory() {
        return "pep_id";
    }

    @Override
    public String getCategoryDisplayName() {
        return "Peptide identifications";
    }

}
