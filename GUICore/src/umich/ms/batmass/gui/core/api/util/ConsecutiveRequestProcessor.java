/*
 * License placeholder.
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
     * Post a job to our separate single-threaded job queue
     * possibly starting a simple indeterminate progress bar
     * @param r the Runnable task to be performed
     * @param jobName
     * @return Task handle to which you can attach a taskFinished() listener
     */
    public static RequestProcessor.Task post(Runnable r, String jobName) {
        RequestProcessor.Task task = rp.post(r);
//        final RequestProcessor.Task task = rp.create(r);
        if (jobName != null) {
            Cancellable can = new Cancellable() {
                @Override
                public boolean cancel() {
                    Thread.currentThread().interrupt();
                    return true;
                }
            };
//            final ProgressHandle ph = ProgressHandleFactory.createHandle(jobName, task);
            final ProgressHandle ph = ProgressHandleFactory.createHandle(jobName, can);
            task.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(Task task) {
                    ph.finish();
                }
            });
            // start the progresshandle the progress UI will show 500ms after
            ph.start();
            ph.switchToIndeterminate();
//            task.schedule(0);
        }
        return task;
    }

    public static RequestProcessor.Task post(Runnable r) {
        return post(r, null);
    }
}
