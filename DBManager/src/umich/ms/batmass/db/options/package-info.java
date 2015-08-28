/*
 * License placeholder
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
