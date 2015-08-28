/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.nbputils;

import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Helper functions for working with windows in NetBeans.
 * @author Dmitry Avtonomov
 */
public class WindowSystemUtils {

    /**
     * The old-style ID of the top component for the project explorer.
     * Allegedly the same as {@link #PROJECT_EXPLORER_TC_ID}.
     */
    public static final String PROJECT_LOGICAL_TAB_ID = "projectTabLogical_tc";
    /**
     * The new-style ID of the top component for the project explorer.
     * Allegedly the same as {@link #PROJECT_LOGICAL_TAB_ID}.
     */
    public static final String PROJECT_EXPLORER_TC_ID = "ExplorerViewTopComponent";

    /**
     * The TC where the projects are displayed.
     * @return might be null, if the projects window was closed.
     */
    public TopComponent getProjectsTC() {
        return WindowManager.getDefault().findTopComponent(PROJECT_LOGICAL_TAB_ID);
    }
}
