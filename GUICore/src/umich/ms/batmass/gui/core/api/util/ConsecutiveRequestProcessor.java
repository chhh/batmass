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
package umich.ms.batmass.gui.core.api.util;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author dmitriya
 */
public abstract class ConsecutiveRequestProcessor {
    private final static RequestProcessor rp =
            new RequestProcessor("GUI_LONG_TASKS", 1, true);
    public static RequestProcessor getDefault() {
        return rp;
    }

    /**
     * Post a job to our separate single-threaded job queue possibly starting a 
     * simple indeterminate progress bar.
     * @param r the Runnable task to be performed
     * @param jobName to be displayed in the progress bar
     * @return Task handle to which you can attach a taskFinished() listener
     */
    public static RequestProcessor.Task post(Runnable r, String jobName) {
        RequestProcessor.Task task = rp.post(r);
        if (jobName != null) {
            final ProgressHandle ph = ProgressHandle.createHandle(jobName);
            task.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(Task task) {
                    ph.finish();
                }
            });
            // start the progresshandle the progress UI will show 500ms after
            ph.start();
            ph.switchToIndeterminate();
        }
        return task;
    }
    
    /**
     * Post a job to our separate single-threaded job queue possibly starting a 
     * simple indeterminate progress bar and allowing to cancel the job.
     * It's up to you to provide an implementation of Cancellable.
     * @param run the Runnable task to be performed
     * @param cancel will be called when the user clicks red cross to cancel the 
     *               task
     * @param jobName to be displayed in the progress bar
     * @return Task handle to which you can attach a taskFinished() listener
     */
    public static RequestProcessor.Task post(Runnable run, Cancellable cancel, String jobName) {
        RequestProcessor.Task task = rp.post(run);
        if (jobName != null) {
            final ProgressHandle ph = ProgressHandle.createHandle(jobName, cancel);
            task.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(Task task) {
                    ph.finish();
                }
            });
            // start the progresshandle the progress UI will show 500ms after
            ph.start();
            ph.switchToIndeterminate();
        }
        return task;
    }

    public static RequestProcessor.Task post(Runnable r) {
        return post(r, null);
    }
}
