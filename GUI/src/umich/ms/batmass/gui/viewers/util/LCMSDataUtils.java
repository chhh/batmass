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
package umich.ms.batmass.gui.viewers.util;

import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import umich.ms.batmass.gui.core.api.util.ConsecutiveRequestProcessor;
import umich.ms.batmass.nbputils.notifications.NotifyUtil;
import umich.ms.datatypes.LCMSData;
import umich.ms.datatypes.LCMSDataSubset;
import umich.ms.fileio.exceptions.FileParsingException;

/**
 *
 * @author Dmitry Avtonomov
 */
public class LCMSDataUtils {
    private LCMSDataUtils() {};


    /**
     * Checks if the subset has already been loaded into scan collection, if not
     * it will load it, displaying a progress bar in the bottom right corner.
     * sequentially
     * @param data you can get this from the lookup of any node within 'lcms'
     * file-category
     * @param subset the subset to be loaded
     * @param isBlocking if true, blocks until parsing is finished
     * @return null if the requested portion of the data has been loaded already,
     * RequestProcessor.Task otherwise. You can attach a {@link TaskListener} to
     * it to get notified, when the task is finished.
     */
    public static RequestProcessor.Task loadData(
            final LCMSData data,
            final LCMSDataSubset subset,
            boolean isBlocking) {
        return loadData(data, subset, null, isBlocking);
    }

    /**
     * Checks if the subset has already been loaded into scan collection, if not
     * it will load it, displaying a progress bar in the bottom right corner.
     * sequentially
     * @param data you can get this from the lookup of any node within 'lcms'
     * file-category
     * @param subset the subset to be loaded
     * @param user an object, using the data, e.g. an instance of a viewer. LCMSData
     * will then keep track of all users, and if a user is GCed before it has unloaded
     * all its used subsets, it will automatically unload those.
     * @param isBlocking if true, blocks until parsing is finished
     * @return null if the requested portion of the data has been loaded already,
     * RequestProcessor.Task otherwise. You can attach a {@link TaskListener} to
     * it to get notified, when the task is finished.
     */
    public static RequestProcessor.Task loadData(
            final LCMSData data,
            final LCMSDataSubset subset,
            final Object user,
            boolean isBlocking) {
        Runnable loadDataRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    data.load(subset, user);
                } catch (FileParsingException ex) {
                    NotifyUtil.error("Error loading data", "Could not load LC/MS data", ex, false);
                    //Exceptions.printStackTrace(ex);
                }
            }
        };
        RequestProcessor.Task loadTask = ConsecutiveRequestProcessor
                .post(loadDataRunnable, "Parsing: " + data.getSource().getName());
        if (isBlocking) {
            loadTask.waitFinished();
        }
        return loadTask;
    }
}
