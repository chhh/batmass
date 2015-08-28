/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.spi.nodes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import umich.ms.batmass.filesupport.core.api.NodeInfoUtils;
import umich.ms.batmass.projects.core.type.BMProject;
import umich.ms.batmass.projects.core.util.BMProjectUtils;

/**
 * Defines the default paths from which actions are retrieved for Nodes.
 * The logic is as follows:
 *  - all nodes get actions for "any-project"/"any-type"/"any-category"
 *
 *  - all nodes get actions for "any-project"/"any-type"/"their-category"
 *  - all nodes get actions for "any-project"/"their-type"/"any-category"
 *  - all nodes get actions for "any-project"/"their-type"/"their-category"
 * 
 *  - all nodes get actions for "their-project"/"any-type"/"any-category"
 * 
 *  - all nodes get actions for "their-project"/"their-type"/"any-category"
 *  - all nodes get actions for "their-project"/"any-type"/"their-category"
 *  - all nodes get actions for "their-project"/"their-type"/"their-category"
 *
 * @author Dmitry Avtonomov
 */
public abstract class AbstractFileNodeInfo implements FileNodeInfo {

    @Override
    public int getActionPathsPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getIconPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public List<String> getActionPaths(Project p) {
        ArrayList<String> list;
        if (p == null) {
            list = new ArrayList<>(4);
        } else {
            list = new ArrayList<>(8);
        }

        String fileCategory = getFileTypeResolver().getCategory();
        String fileType = getFileTypeResolver().getType();

        // project specific actions
        if (p != null) {
            BMProject bmp = BMProjectUtils.toBMProject(p);
            String projectType = BMProjectUtils.getProjectType(bmp.getClass());
            list.add(NodeInfoUtils.getNodeActionsPath(projectType, null, null));
            list.add(NodeInfoUtils.getNodeActionsPath(projectType, null, fileType));
            list.add(NodeInfoUtils.getNodeActionsPath(projectType, fileCategory, null));
            list.add(NodeInfoUtils.getNodeActionsPath(projectType, fileCategory, fileType));
        }

        // type specific actions
        list.add(NodeInfoUtils.getNodeActionsPath(null, null, null));
        list.add(NodeInfoUtils.getNodeActionsPath(null, null, fileType));
        list.add(NodeInfoUtils.getNodeActionsPath(null, fileCategory, null));
        list.add(NodeInfoUtils.getNodeActionsPath(null, fileCategory, fileType));

        return list;
    }

    @Override
    public List<String> getCapabilityProviderPaths(Project p) {
        ArrayList<String> list;
        if (p == null) {
            list = new ArrayList<>(4);
        } else {
            list = new ArrayList<>(8);
        }

        String fileCategory = getFileTypeResolver().getCategory();
        String fileType = getFileTypeResolver().getType();

        // project specific capabilities
        if (p != null) {
            BMProject bmp = BMProjectUtils.toBMProject(p);
            String projectType = BMProjectUtils.getProjectType(bmp.getClass());
            list.add(NodeInfoUtils.getNodeCapabilitiesPath(projectType, null, null));
            list.add(NodeInfoUtils.getNodeCapabilitiesPath(projectType, null, fileType));
            list.add(NodeInfoUtils.getNodeCapabilitiesPath(projectType, fileCategory, null));
            list.add(NodeInfoUtils.getNodeCapabilitiesPath(projectType, fileCategory, fileType));
        }

        // type specific capabilities
        list.add(NodeInfoUtils.getNodeCapabilitiesPath(null, null, null));
        list.add(NodeInfoUtils.getNodeCapabilitiesPath(null, null, fileType));
        list.add(NodeInfoUtils.getNodeCapabilitiesPath(null, fileCategory, null));
        list.add(NodeInfoUtils.getNodeCapabilitiesPath(null, fileCategory, fileType));

        return list;
    }

    @Override
    public ImageIcon getIcon() {
        return getFileTypeResolver().getIcon();
    }


}
