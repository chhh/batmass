/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.types.metabolomics;

import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import umich.ms.batmass.projects.core.annotations.BMProjectType;
import umich.ms.batmass.projects.core.type.BMProject;
import umich.ms.batmass.projects.core.type.BMProjectLogicalView;

/**
 *
 * @author dmitriya
 */
@BMProjectType(projectType = "MetabolomicsProject")
public class MetabolomicsProject extends BMProject {

    public static final String PROJECT_DIR = "batmassproject";
    public static final String PROJECT_PROPFILE = "metabolomics.properties";
    public static final @StaticResource String ICON_PATH = "umich/ms/batmass/projects/types/metabolomics/icons/Me_project_16.png";
    protected static final ImageIcon ICON = new ImageIcon(ImageUtilities.loadImage(ICON_PATH));
    public static final String TYPE = "MetabolomicsProject";

    public MetabolomicsProject(FileObject projectDir, ProjectState projectState) {
        super(projectDir, projectState);
        addToLookup(new BMProjectLogicalView(this));
    }

    @Override
    public ImageIcon getIcon() {
        return ICON;
    }

    @Override
    public String getProjectPropsDirectoryPath() {
        return PROJECT_DIR;
    }

    @Override
    public String getProjectPropsFileName() {
        return PROJECT_PROPFILE;
    }
}
