/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.project.types.generic;

import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import umich.ms.batmass.project.types.base.AbstractProject;

/**
 *
 * @author dmitriya
 */
@Messages("DESC_BatMassProjectType=A generic LC/MS based project")
public class BatMassProject extends AbstractProject {
    @StaticResource
    public static final String ICON_PATH = "umich/ms/batmass/project/types/generic/icons/BM_project_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    public BatMassProject(String name) {
        super(name);
    }

    @Override
    public ImageIcon getIcon() {
        return ICON;
    }

    @Override
    public String getTextualDescriptionOfType() {
        return Bundle.DESC_BatMassProjectType();
    }
}
