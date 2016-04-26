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
package umich.ms.batmass.db.options;

import java.util.ResourceBundle;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import umich.ms.batmass.db.manager.DBManager;

/**
 * This class is only here to set up default values for DB Options panel.
 * If NbPreferences.forModule(DBManager.class) does not contain some preferences
 * stored in it, then the default values are taken from the {@link umich.ms.batmass.db.options.Bundle}.
 * Not the neatest solution, but it works.
 *
 * The {@link #restored()} method is called automatically upon application start
 * because this class is registered in Module Manifest as:
 *     OpenIDE-Module-Install: umich/ms/batmass/db/options/OptionsInstaller.class
 *
 * @author Dmitry Avtonomov
 */
public class OptionsInstaller extends ModuleInstall {

    @Override
    public void restored() {
        ResourceBundle bundle = NbBundle.getBundle(this.getClass());
        if (bundle == null) {
            return;
        }

        String host = NbPreferences.forModule(DBManager.class).get("host", null);
        if (host == null) {
            NbPreferences.forModule(DBManager.class).put("host", bundle.getString("DBMySQLOptionsPanel.serverHostNameTextField.text"));
        }
        String port = NbPreferences.forModule(DBManager.class).get("port", null);
        if (port == null) {
            NbPreferences.forModule(DBManager.class).put("port", bundle.getString("DBMySQLOptionsPanel.serverPortTextField.text"));
        }
        String dbname = NbPreferences.forModule(DBManager.class).get("dbname", null);
        if (dbname == null) {
            NbPreferences.forModule(DBManager.class).put("dbname", bundle.getString("DBMySQLOptionsPanel.dbNameTextField.text"));
        }
        String username = NbPreferences.forModule(DBManager.class).get("username", null);
        if (username == null) {
            NbPreferences.forModule(DBManager.class).put("username", bundle.getString("DBMySQLOptionsPanel.dbUserNameTextField.text"));
        }
        String password = NbPreferences.forModule(DBManager.class).get("password", null);
        if (password == null) {
            NbPreferences.forModule(DBManager.class).put("password", bundle.getString("DBMySQLOptionsPanel.dbUserPasswordPasswordField.text"));
        }
    }
}
