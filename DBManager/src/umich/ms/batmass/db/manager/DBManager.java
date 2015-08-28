/*
 * License placeholder
 */
package umich.ms.batmass.db.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import umich.ms.batmass.db.DBConstants;
import umich.ms.batmass.nbputils.OutputWndPrinter;

/**
 *
 * @author dmitriya
 */
@ServiceProvider(service = DBManager.class)
public class DBManager {

    protected static volatile DBManager dbm;
    protected String dbms = "mysql";

    public DBManager() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBManager.class.getName()).log(
                    Level.SEVERE, ex.toString(), ex);
        }
    }

    public static DBManager getDefault() {
        // Singleton pattern using double-checking
        if (dbm == null) {
            synchronized(DBManager.class) {
                if (dbm == null) {
                    dbm = new DBManager();
                }
            }
        }
        return dbm;
    }

    public Connection getConnection() {
        Preferences dbPref = NbPreferences.forModule(DBManager.class);
        String host = dbPref.get("host", null);
        String port = dbPref.get("port", null);
        String dbname = dbPref.get("dbname", null);
        String username = dbPref.get("username", null);
        String password = dbPref.get("password", null);

        Connection con = null;
        try {
            String conStr
                    = "jdbc:" + this.dbms + "://" + host + ":" + port + "/" + dbname + "?"
                    + "user=" + username + "&password=" + password;
            con = DriverManager.getConnection(conStr);
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(
                    Level.SEVERE, ex.toString());
            OutputWndPrinter.printErr(DBConstants.OUTPUT_TOPIC, ex.getMessage());
            NotifyDescriptor.Message notice = new NotifyDescriptor.Message("Error connecting to DB: " + ex.getMessage());
            DialogDisplayer.getDefault().notify(notice);
        }
        return con;
    }
}
