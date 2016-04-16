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
