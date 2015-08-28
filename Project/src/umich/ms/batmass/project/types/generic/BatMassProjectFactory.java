/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.project.types.generic;

import javax.swing.ImageIcon;
import umich.ms.batmass.project.types.base.AbstractProject;
import umich.ms.batmass.project.types.base.ProjectFactory;

/**
 *
 * @author dmitriya
 */
public class BatMassProjectFactory implements ProjectFactory {

    @Override
    public AbstractProject loadProject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Result isProject() {
        return new Result() {
            @Override
            public ImageIcon getIcon() {return BatMassProject.ICON;}
        };
    }

    @Override
    public boolean createProject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
