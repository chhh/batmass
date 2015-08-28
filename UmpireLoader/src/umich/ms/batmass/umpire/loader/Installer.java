/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.umpire.loader;

import org.apache.log4j.Level;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        Utility.ConsoleLogger.SetConsoleLogger(Level.ALL);
    }

}
