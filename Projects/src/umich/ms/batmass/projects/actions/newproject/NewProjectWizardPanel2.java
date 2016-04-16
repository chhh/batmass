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
package umich.ms.batmass.projects.actions.newproject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class NewProjectWizardPanel2 implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {
    public static final String PROP_PRROJECT_NAME = "project_name";
    public static final String PROP_PRROJECT_LOCATION = "project_location";


    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private NewProjectVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public NewProjectVisualPanel2 getComponent() {
        if (component == null) {
            component = new NewProjectVisualPanel2();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        wiz.putProperty(PROP_PRROJECT_LOCATION, getComponent().getTxtLocation().getText());
        wiz.putProperty(PROP_PRROJECT_NAME, getComponent().getTxtName().getText());
    }

    @Override
    public void validate() throws WizardValidationException {
        String location = getComponent().getTxtLocation().getText();
        location = location.trim();

        if (location.length() == 0) {
            throw new WizardValidationException(null, "Please provide a path to an existing directory", null);
        }
        File f = new File(location);
        if (!f.exists()) {
            throw new WizardValidationException(null, "The specified directory does not exist", null);
        }
        if (!f.isDirectory()) {
            throw new WizardValidationException(null, "The specified path is not a directory", null);
        }
        String name = getComponent().getTxtName().getText();
        if (name.trim().length() == 0) {
            throw new WizardValidationException(null, "Project name must contain at least some non-whitespace characters", null);
        }
    }

}
