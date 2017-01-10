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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.ErrorManager;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;

/**
 * @deprecated uses almost deprecated NetBeans APIs.
 * This should be deprecated.
 * @author qbeukes.blogspot.com, used by metalklesk
 */
@Deprecated
public class NotifyUtil {

    private NotifyUtil() {}

    /**
    * Show message with the specified type and action listener
     * @param title
     * @param message
     * @param type
     * @param actionListener
     * @param clear
    */
    public static void show(String title, String message, MessageType type, ActionListener actionListener, boolean clear) {
        Notification n = NotificationDisplayer.getDefault().notify(title, type.getIcon(), message, actionListener);
        if(clear == true)
            n.clear();
    }

    /**
    * Show message with the specified type and a default action which displays the
    * message using {@link MessageUtil} with the same message type
     * @param title
     * @param message
     * @param type
     * @param clear
    */
    public static void show(String title, final String message, final MessageType type, boolean clear) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageUtil.show(message, type);
            }
        };

        show(title, message, type, actionListener, clear);
    }

    /**
    * Show an information notification
     * @param title
    * @param message
     * @param clear
    */
    public static void info(String title, String message, boolean clear) {
        show(title, message, MessageType.INFO, clear);
    }

    /**
    * Show an error notification
     * @param title
    * @param message
     * @param clear
    */
    public static void error(String title, String message, boolean clear) {
        show(title, message, MessageType.ERROR, clear);
    }

    /**
    * Show an error notification for an exception
     * @param title
    * @param message
    * @param exception
     * @param clear
    */
    public static void error(String title, final String message, final Throwable exception , boolean clear) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ErrorManager.getDefault().notify(exception);
                //MessageUtil.showException(message, exception);
            }
        };

        show(title, message, MessageType.ERROR, actionListener, clear);
    }

    /**
    * Show an warning notification
     * @param title
    * @param message
     * @param clear
    */
    public static void warn(String title, String message, boolean clear) {
        show(title, message, MessageType.WARNING, clear);
    }

    /**
    * Show an plain notification
     * @param title
    * @param message
     * @param clear
    */
    public static void plain(String title, String message, boolean clear) {
        show(title, message, MessageType.PLAIN, clear);
    }

}