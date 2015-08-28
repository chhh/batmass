/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "umich.ms.batmass.projects.actions.CreateNewProjectAction"
)
@ActionRegistration(
        iconBase = "umich/ms/batmass/projects/actions/plus_16px.png",
        displayName = "#CTL_CreateNewProjectAction"
)
//@ActionReference(path = "Menu/File", position = 0)
@Messages("CTL_CreateNewProjectAction=New project (no wizard)")
/**
 * Not used anywhere and does nothing
 */
public final class CreateNewProjectAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        FileChooserBuilder chooser = new FileChooserBuilder(this.getClass());
    }
}
