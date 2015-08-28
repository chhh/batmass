/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.nbputils;

import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Helper class for interacting with the 'Output' window.
 * @author Dmitry Avtonomov
 */
abstract public class OutputWndPrinter {
    
    /**
     * Write an message to the output window, the message will be colored red.
     * @param topic name of the tab in the output window
     * @param useNewWindow if true, forces creation of a new tab, even if one with
     *  the same topic name already exists.
     * @param msg the message to write
     */
    public static void printErr(String topic, boolean useNewWindow, String msg) {
        InputOutput io = IOProvider.getDefault().getIO(topic, useNewWindow);
        try (OutputWriter ow = io.getErr()) {
            ow.println(msg);
        }
    }

    /**
     * Write an message to the output window, the message will be colored red.
     * @param topic the name of the tab in the output window
     * @param msg the message to write
     */
    public static void printErr(String topic, String msg) {
        printErr(topic, false, msg);
    }
    
    /**
     * Write a message to the output window.
     * @param topic name of the tab in the output window
     * @param useNewWindow if true, forces creation of a new tab, even if one with
     *  the same topic name already exists.
     * @param msg the message to write
     */
    public static void printOut(String topic, boolean useNewWindow, String msg) {
        InputOutput io = IOProvider.getDefault().getIO(topic, useNewWindow);
        try (OutputWriter ow = io.getOut()) {
            ow.println(msg);
        }
    }

    /**
     * Write a message to the output window.
     * @param topic the name of the tab in the output window
     * @param msg the message to write
     */
    public static void printOut(String topic, String msg) {
        printOut(topic, false, msg);
    }
}
