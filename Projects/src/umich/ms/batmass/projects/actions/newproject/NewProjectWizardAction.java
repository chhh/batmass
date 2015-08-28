/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.actions.newproject;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.apache.commons.configuration.ConfigurationException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import umich.ms.batmass.projects.core.type.BMProjectFactory;

 @ActionID(category="File", id="umich.ms.batmass.projects.actions.newproject.NewProjectWizardAction")
 @ActionRegistration(
         displayName="New project...",
         iconBase = "umich/ms/batmass/projects/actions/plus_16px.png")
// @ActionReference(path="Menu/File", position=10)
 @ActionReferences({
     @ActionReference(path="Menu/File", position=10),
     @ActionReference(path="ProjectsTabActions", position=10),
     @ActionReference(path="Toolbars/File", position=10)
 })
public final class NewProjectWizardAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new NewProjectWizardPanel1());
        panels.add(new NewProjectWizardPanel2());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));

        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Create a new project");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            BMProjectFactory pf = (BMProjectFactory)wiz.getProperty(NewProjectWizardPanel1.PROP_PROJECT_FACTORY);
            String name = (String)wiz.getProperty(NewProjectWizardPanel2.PROP_PRROJECT_NAME);
            String location = (String)wiz.getProperty(NewProjectWizardPanel2.PROP_PRROJECT_LOCATION);
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(name + " @ " + location + "\ntype: " + pf.getClass().getSimpleName()));
            try {
                // now we have all input data to create a project
                // BMProjectFactory should know how to create a proper file structure
                pf.createProjectDirStructure(Paths.get(location), name);

                // and open the project, if it was created
                Path newProjectPath = Paths.get(location);
                FileObject projectToBeOpened = FileUtil.toFileObject(newProjectPath.toFile());
                Project project = ProjectManager.getDefault().findProject(projectToBeOpened);
                Project[] projectsToOpen = {project};
                OpenProjects.getDefault().open(projectsToOpen, false, true);

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ConfigurationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
