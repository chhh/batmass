/* 
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umich.ms.batmass.projects.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
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
