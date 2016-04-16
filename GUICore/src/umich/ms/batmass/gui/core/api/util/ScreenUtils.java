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



import java.awt.*;

/**
 * Author: Dmitry Avtonomov (dmitriya)
 * Email: dmitriy.avtonomov@gmail.com
 * Date: 2/13/13
 * Time: 12:21 PM
 */


public final class ScreenUtils {
    // Suppress default constructor for noninstantiability
    private ScreenUtils() {
        throw new AssertionError();
    }

    /**
     * Calculates the available on-screen space inside a window/panel/component.
     *
     * @param container The window (JFrame)/JPanel/JComponent for which the available real estate should be calculated.
     *            If null is provided, calcs screen size minus the insets (like windows toolbar)
     * @return rectangle with origin (x,y) and window (width, height)
     */
    static public Rectangle getScreenBounds(Container container) {
        Rectangle sb;
        Dimension sd;
        Insets si = getScreenInsets(container);

        if (container == null) {
            sd = Toolkit.getDefaultToolkit().getScreenSize();
            sb = new Rectangle(new Point(0,0), sd);
        } else {
            sb = container.getBounds();
        }

        sb.x += si.left;
        sb.width -= (si.left + si.right);
        sb.y += si.top;
        sb.height -= (si.top + si.bottom);
        return sb;
    }

    /**
     * Gets the insets (like toolbars, borders, etc) for the current window.
     * You probably will never need this one.
     *
     * @param container The window (JFrame)/JPanel/JComponent for which to get the insets. If null, your desktop is assumed.
     * @return ScreenInsets (top, bottom, left, right..)
     */
    static public Insets getScreenInsets(Container container) {
        Insets si;

        if (container == null) {
            si = Toolkit.getDefaultToolkit().getScreenInsets(new Frame().getGraphicsConfiguration());
        } else {
            si = container.getInsets();
        }
        
        return si;
    }
}