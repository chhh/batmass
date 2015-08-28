/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.project;

/**
 *
 * @author dmitriya
 */
public class ProjectManager {
    private static volatile ProjectManager instance;

    public static ProjectManager getDefault() {
        // Singleton pattern using double-checking
        if (instance == null) {
            synchronized(ProjectManager.class) {
                if (instance == null) {
                    instance = new ProjectManager();
                }
            }
        }
        return instance;
    }
}
