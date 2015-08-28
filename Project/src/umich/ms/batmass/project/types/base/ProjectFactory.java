/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.project.types.base;

import javax.swing.ImageIcon;

/**
 *
 * @author dmitriya
 */
public interface ProjectFactory {
    public AbstractProject loadProject();
    public Result isProject();
    public boolean createProject();

    public abstract static class Result {
        public abstract ImageIcon getIcon();
    }
}
