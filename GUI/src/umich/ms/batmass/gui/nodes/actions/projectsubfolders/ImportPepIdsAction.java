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
package umich.ms.batmass.gui.nodes.actions.projectsubfolders;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import umich.ms.batmass.filesupport.core.actions.importing.ImportFileByCategory;
import umich.ms.batmass.projects.nodes.NodeFactoryPepIds;

/**
 *
 * @author Dmitry Avtonomov
 */
@ActionID(
        category = "ProjectSubnodes",
        id = "umich.ms.batmass.gui.nodes.projectsubfolders.actions.ImportPepIdsAction"
)
@ActionRegistration(
        displayName = "#CTL_ImportPepIdsAction",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "ProjectSubfolders/" + NodeFactoryPepIds.TYPE + "/Actions")
})
@NbBundle.Messages("CTL_ImportPepIdsAction=Import IDs")
public class ImportPepIdsAction extends ImportFileByCategory {

    public ImportPepIdsAction() {
    }

    public ImportPepIdsAction(Lookup context) {
        super(context);
    }
    
    @Override
    public String getActionName() {
        return Bundle.CTL_ImportPepIdsAction();
    }

    @Override
    public String getFileCategory() {
        return "pep_id";
    }

    @Override
    public String getCategoryDisplayName() {
        return "Peptide identifications";
    }

}
