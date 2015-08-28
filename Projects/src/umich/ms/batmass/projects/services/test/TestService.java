/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.services.test;

import javax.swing.JOptionPane;

/**
 * TODO: WARNINIG: ACHTUNG: Delete this class! Was here for testing.
 * @author dmitriya
 */
public abstract class TestService {
    static {
        JOptionPane.showMessageDialog(null, "===> static loading " + TestService.class.getCanonicalName());
    }

    public abstract String m();
}
