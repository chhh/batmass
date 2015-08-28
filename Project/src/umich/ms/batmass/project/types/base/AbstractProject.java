/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.project.types.base;

import java.io.Serializable;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class AbstractProject implements Serializable {
    private String name;
    private String shortDescription;
    private String description;
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/project/types/base/icons/BM_project_16.png";
    private static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    public AbstractProject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Icon that will be shown in "Project Explorer" window and "Open Project"
     * dialog.
     * @return
     */
    public ImageIcon getIcon() {
        return ICON;
    };

    /**
     * This text is used in the "New project" dialog, when a user selects
     * the project type.
     * @return
     */
    public abstract String getTextualDescriptionOfType();


}
