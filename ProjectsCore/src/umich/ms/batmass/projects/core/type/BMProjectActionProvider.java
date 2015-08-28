/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.type;

import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.util.Lookup;

/**
 * An {@link ActionProvider} to handle standard project commands like
 * Build and Clean.
 */
public class BMProjectActionProvider implements ActionProvider {
    private final BMProject project;

    public BMProjectActionProvider(final BMProject project) {
        this.project = project;
    }

    @Override
    public String[] getSupportedActions() {
        return new String[] {
            ActionProvider.COMMAND_RENAME,
            ActionProvider.COMMAND_DELETE
        };
    }

    @Override
    public void invokeAction(String string, Lookup lkp) throws IllegalArgumentException {
        if (string.equalsIgnoreCase(ActionProvider.COMMAND_RENAME)) {
            Lookup lookup = project.getLookup();
            ProjectInformation pi = lookup.lookup(ProjectInformation.class);
            DefaultProjectOperations.performDefaultRenameOperation(project, pi.getDisplayName());
            return;
        }
        if (string.equalsIgnoreCase(ActionProvider.COMMAND_DELETE)) {
            DefaultProjectOperations.performDefaultDeleteOperation(project);
            return;
        }
    }

    @Override
    public boolean isActionEnabled(String command, Lookup lookup) throws IllegalArgumentException {
        switch (command) {
            case ActionProvider.COMMAND_RENAME:
                return true;
            case ActionProvider.COMMAND_DELETE:
                return true;
            default:
                return false;
        }
    }
}
