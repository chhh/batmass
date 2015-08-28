/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.util;

import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import umich.ms.batmass.gui.core.api.util.ConsecutiveRequestProcessor;
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
                    Exceptions.printStackTrace(ex);
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
