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
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.ContextAwareAction;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Project",
        id = "umich.ms.batmass.projects.actions.CloseSelectedProjectsActionManual"
)
@ActionRegistration(
        displayName = "#CTL_CloseSelectedProjectsActionManual",
        lazy = false
)
//@ActionReferences({
//    @ActionReference(path = "Loaders/folder/any/Actions", position = 10001, separatorBefore = 10000)
//})
@Messages("CTL_CloseSelectedProjectsActionManual=Close selected project(s) manual")
public final class CloseSelectedProjectsActionManual extends AbstractAction implements Presenter.Popup, ContextAwareAction {
//public final class CloseSelectedProjectsActionManual extends AbstractAction implements ContextAwareAction {
    @StaticResource
    private static final String ICON = "umich/ms/batmass/projects/actions/close_16px.png";
    private final Lookup context;


    public CloseSelectedProjectsActionManual() {
        this(Utilities.actionsGlobalContext());
    }

    public CloseSelectedProjectsActionManual(Lookup context) {
        this.context = context;
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON, false));
        putValue(NAME, Bundle.CTL_CloseSelectedProjectsActionManual());
        Collection<? extends Project> projects = context.lookupAll(Project.class);
        this.setEnabled(projects.size() > 0);
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Collection<? extends Project> projects = context.lookupAll(Project.class);
        OpenProjects.getDefault().close(projects.toArray(new Project[projects.size()]));
    }

    @Override
    public JMenuItem getPopupPresenter() {
        if (this.isEnabled())
            return new JMenuItem(this);
        else
            return null;
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new CloseSelectedProjectsActionManual();
    }


}
