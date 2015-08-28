/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.AbstractAction;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Project",
        id = "umich.ms.batmass.projects.actions.CloseSelectedProjectsAction"
)
@ActionRegistration(
        iconBase = "umich/ms/batmass/projects/actions/close_16px.png",
        displayName = "#CTL_CloseSelectedProjectsAction"
)
//@ActionReferences({
//    @ActionReference(path = "Loaders/folder/any/Actions", position = 9001, separatorBefore = 9000)
//})
@Messages("CTL_CloseSelectedProjectsAction=Close selected project(s)")
//public final class CloseSelectedProjectsAction implements ActionListener {
public final class CloseSelectedProjectsAction extends AbstractAction implements ActionListener {

    private final List<Project> context;

    public CloseSelectedProjectsAction(List<Project> context) {
        this.context = context;
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        OpenProjects.getDefault().close(context.toArray(new Project[context.size()]));
    }
}
