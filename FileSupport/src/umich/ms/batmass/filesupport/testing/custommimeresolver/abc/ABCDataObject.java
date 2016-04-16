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
package umich.ms.batmass.filesupport.testing.custommimeresolver.abc;

import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.NbBundle.Messages;

@Messages({
    "LBL_ABC_LOADER=Files of ABC"
})
//@MIMEResolver.ExtensionRegistration(
//        displayName = "#LBL_ABC_LOADER",
//        mimeType = "application/x-asd",
//        extension = {"asd"}
//)
@DataObject.Registration(
        mimeType = "application/x-abc",
        iconBase = "umich/ms/batmass/filesupport/testing/custommimeresolver/abc/map2d_icon.png",
        displayName = "#LBL_ABC_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/x-abc/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/application/x-abc/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/application/x-abc/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/application/x-abc/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/application/x-abc/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class ABCDataObject extends MultiDataObject {

    public ABCDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("application/x-asd", false);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

}
