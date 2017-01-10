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
package umich.ms.batmass.nbputils.notifications;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author qbeukes.blogspot.com, used by metalklesk
 */
public class MessageUtil {

    private MessageUtil() {}

    /**
    * @return The dialog displayer used to show message boxes
    */
    public static DialogDisplayer getDialogDisplayer() {
        return DialogDisplayer.getDefault();
    }

    /**
    * Show a message of the specified type
    *
    * @param message
    * @param messageType As in {@link NotifyDescription} message type constants.
    */
    public static void show(String message, MessageType messageType) {
        getDialogDisplayer().notifyLater(new NotifyDescriptor.Message(message,
        messageType.getNotifyDescriptorType()));
    }

    /**
    * Show an exception message dialog
    *
    * @param message
    * @param exception
    */
    public static void showException(String message, Throwable exception) {
        getDialogDisplayer().notifyLater(new NotifyDescriptor.Exception(exception, message));
    }

    /**
    * Show an information dialog
    * @param message
    */
    public static void info(String message) {
        show(message, MessageType.INFO);
    }

    /**
    * Show an error dialog
    * @param message
    */
    public static void error(String message) {
        show(message, MessageType.ERROR);
    }

    /**
    * Show an error dialog for an exception
    * @param message
    * @param exception
    */
    public static void error(String message, Throwable exception) {
        showException(message, exception);
    }

    /**
    * Show an question dialog
    * @param message
    */
    public static void question(String message) {
        show(message, MessageType.QUESTION);
    }

    /**
    * Show an warning dialog
    * @param message
    */
    public static void warn(String message) {
        show(message, MessageType.WARNING);
    }

    /**
    * Show an plain dialog
    * @param message
    */
    public static void plain(String message) {
        show(message, MessageType.PLAIN);
    }
}
