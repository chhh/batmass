/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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