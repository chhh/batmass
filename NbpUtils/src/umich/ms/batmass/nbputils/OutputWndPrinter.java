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
