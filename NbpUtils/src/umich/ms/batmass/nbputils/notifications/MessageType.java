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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import umich.ms.batmass.nbputils.PathUtils;

/**
 *
 * @author qbeukes.blogspot.com, used by metalklesk
 */
public enum MessageType {
    PLAIN   (NotifyDescriptor.PLAIN_MESSAGE,       null),
    INFO    (NotifyDescriptor.INFORMATION_MESSAGE, "info_round_16px.png"),
    QUESTION(NotifyDescriptor.QUESTION_MESSAGE,    "question_balloon_16px.png"),
    ERROR   (NotifyDescriptor.ERROR_MESSAGE,       "error_round_shadow_16px.png"),
    WARNING (NotifyDescriptor.WARNING_MESSAGE,     "warning_triangle_yellow_16px.png");

    private final int notifyDescriptorType;
    
    /** it's here just to check the correctness of the path */
    @StaticResource
    private static final String ICON_TEST_PATH = "umich/ms/batmass/nbputils/notifications/icons/info_round_16px.png";

    private final ImageIcon icon;

    private MessageType(int notifyDescriptorType, String resourceName) {
        this.notifyDescriptorType = notifyDescriptorType;
        if (resourceName == null) {
            icon = new ImageIcon();
        } else {
            icon = loadIcon(resourceName);
        }
    }

    private static ImageIcon loadIcon(String iconFileName) {
        String packageAsPath = PathUtils.getPackageAsPath(MessageType.class);
        ImageIcon imgIcon = ImageUtilities.loadImageIcon(packageAsPath + "/icons/" + iconFileName, false);
        return imgIcon;
    }

    int getNotifyDescriptorType() {
        return notifyDescriptorType;
    }

    Icon getIcon() {
        return icon;
    }
}