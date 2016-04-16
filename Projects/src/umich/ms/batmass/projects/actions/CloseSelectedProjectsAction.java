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
import java.util.List;
import javax.swing.AbstractAction;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.awt.ActionID;
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
