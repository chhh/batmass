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
import umich.ms.batmass.projects.nodes.NodeFactoryLCMSFiles;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "ProjectSubnodes",
        id = "umich.ms.batmass.gui.nodes.projectsubfolders.actions.ImportLCMSFilesAction"
)
@ActionRegistration(
        displayName = "#CTL_ImportLCMSFilesAction",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "ProjectSubfolders/" + NodeFactoryLCMSFiles.TYPE + "/Actions")
})
@NbBundle.Messages("CTL_ImportLCMSFilesAction=Import LC/MS Files")
public class ImportLCMSFilesAction extends ImportFileByCategory {

    public ImportLCMSFilesAction() {
        super();
    }

    public ImportLCMSFilesAction(Lookup context) {
        super(context);
    }

    @Override
    public String getActionName() {
        return Bundle.CTL_ImportLCMSFilesAction();
    }

    @Override
    public String getFileCategory() {
        return "lcms";
    }

    @Override
    public String getCategoryDisplayName() {
        return "LC/MS Files";
    }

}
