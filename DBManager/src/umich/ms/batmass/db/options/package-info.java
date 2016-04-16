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
@OptionsPanelController.ContainerRegistration(
        id = "DBManagerOptions",
        categoryName = "#OptionsCategory_Name_DBManagerOptions",
        iconBase = "umich/ms/batmass/db/options/database_32px.png",
        keywords = "#OptionsCategory_Keywords_DBManagerOptions",
        keywordsCategory = "DBManagerOptions")
@NbBundle.Messages(
        value = {
            "OptionsCategory_Name_DBManagerOptions=Databases",
            "OptionsCategory_Keywords_DBManagerOptions=database manager, db manager, db, database, databases"
        })
package umich.ms.batmass.db.options;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
