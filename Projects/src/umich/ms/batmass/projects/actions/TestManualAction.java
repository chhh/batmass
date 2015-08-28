/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import umich.ms.batmass.projects.types.proteomics.ProteomicsProject;

/**
 * TODO: WARNINIG: ACHTUNG: This class was here for testing, ActionReference commented out now.
 * @author dmitriya
 */
@ActionID(
        category = "Project",
        id = "umich.ms.batmass.projects.actions.TestManualAction"
)
@ActionRegistration(
        displayName = "#CTL_TestManualAction",
        lazy = false
)
//@ActionReferences({
//    @ActionReference(path = "Projects/" + ProteomicsProject.TYPE + "/Actions", position = 10001, separatorBefore = 10000)
//})
@NbBundle.Messages("CTL_TestManualAction=Display prop file")
public class TestManualAction extends AbstractAction implements LookupListener, ContextAwareAction {
    private Lookup context;
    Lookup.Result<ProteomicsProject> lkpInfo;
 
    public TestManualAction() {
        this(Utilities.actionsGlobalContext());
    }
 
    private TestManualAction(Lookup context) {
        putValue(Action.NAME, NbBundle.getMessage(TestManualAction.class, "CTL_TestManualAction"));
        this.context = context;
    }
 
    void init() {
        assert SwingUtilities.isEventDispatchThread() : "This shall be called only from AWT thread (EDT)";
 
        // No need for man
        if (lkpInfo != null) {
            return;
        }
 
        // The thing we want to listen for the presence or absence of in the global selection
        lkpInfo = context.lookupResult(ProteomicsProject.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }
 
    @Override
    public boolean isEnabled() {
        init();
        if (lkpInfo.allInstances().size() > 1) {
            return true;
        }
        return false;
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        init();
        for (ProteomicsProject project : lkpInfo.allInstances()) {
            // use it somehow...
            String propPath = project.getProjectPropertiesFile().getPath();
            ProjectInformation info = ProjectUtils.getInformation(project);
            String projName = info.getDisplayName();
            StringBuilder sb = new StringBuilder();
            sb.append("The prop file for project: ");
            sb.append(projName);
            sb.append("\n");
            sb.append("Is at: ");
            sb.append(propPath);
            JOptionPane.showMessageDialog(null, sb.toString());
        }
    }
 
    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }
 
    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new TestManualAction(context);
    }
}