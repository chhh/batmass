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
package umich.ms.batmass.filesupport.core.spi.nodes;

import java.util.List;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;

/**
 *
 * @author Dmitry Avtonomov
 */
/**
 * It is recommended to use {@link AbstractFileNodeInfo} or at least consult
 * its source code for default implementation details.
 * <br/>
 * Implementing classes must be registered in the system using:<br/>
 *     {@link NodeInfoRe}
 * <br/>
 * The unique name is preferred to be the same as {@link #getUID() }, but because
 * registration code is allowed to use only static variables, it is recommended
 * to have the UID string as a {@code public static String} variable. With such
 * registrations it will be trivial to remove/replace existing registrations
 * with new ones, plus the registration supports 'position' attribute
 * Use this to provide file-type specific actions and an icon for nodes, that
 * are created by {@link FileDescriptorDataObject} (FDDO). FDDO will lookup
 * FileNodeInfos for the file-type automatically. If nothing found, it will just
 * create a standard DataNode with generic actions.
 * @author Dmitry Avtonomov
 * @see AbstractFileNodeInfo
 */
public interface NodeInfo {
    /** This path is where NodeInfos should be registered. */
    public static final String LAYER_REGISTRATION_PATH = "BatMass/NodeInfos";

    /**
     * Base path in the layer where actions for different file-types are stored.
     * <br/><b>Append this path with one of: {@link #ACTIONS_LAYER_PATH_TYPES},
     * {@link #ACTIONS_LAYER_PATH_CATEGORIES}, {@link #ACTIONS_LAYER_PATH_PROJECTS}
     * then follow javadocs for those fields.
     * </b>
     */
    public static final String ACTIONS_LAYER_PATH_BASE = "BatMass/Nodes/Actions";
//    /**
//     * This path is where Actions specific to file types should be registered.<br/>
//     * <b>IMPORTANT:</b><br/>
//     * <b>Append this path with file-type first and category second!</b>
//     * Only this combination provides a unique mapping to a specific type of file
//     * in a specific context (e.g. the parser knows how to provide data for
//     * viewing the file in a spectrum viewer).
//     */
//    public static final String ACTIONS_LAYER_PATH_TYPES = "Types";
//    /**
//     * This path serves for registration of Actions for whole categories of files.
//     * <br/><b>Append this path with category of the file</b>
//     * If you want to provide some generic Action, not specific to a file-type
//     * in this category, put it only here, don't put it into
//     * {@link #ACTIONS_LAYER_PATH_TYPES}.
//     */
//    public static final String ACTIONS_LAYER_PATH_CATEGORIES = "Categories";
//    /**
//     * This is to provide Project-type specific actions for files.
//     * <br/><b>Append this path with project type first then follow the
//     * same logic as described for {@link #ACTIONS_LAYER_PATH_TYPES}
//     * and {@link #ACTIONS_LAYER_PATH_CATEGORIES}.</b>
//     * <br/>Note, that it doesn't make sense to register anything here for
//     * project type {@link BMProject#TYPE_ANY}, you should just register actions
//     * in {@link #ACTIONS_LAYER_PATH_BASE}.
//     */
//    public static final String ACTIONS_LAYER_PATH_PROJECTS = "Projects";


    /**
     * List of layer.xml paths, that should be used to find actions for this
     * file's node. These actions will be appended to generic file actions
     * already provided by {@link FileDescriptorDataObject}'s node creation
     * mechanism.
     * @param p can be null, in which case project specific paths won't be
     * included in the list
     * @return
     */
    List<String> getActionPaths(Project p);

    /**
     * If multiple FileNodeInfos are registered, then the actions are sorted
     * in the popup menu using this priority. Higher priority action paths
     * come fist, higher priority is designated by lower number.<br/>
     * The default priority is considered to be 0.
     * @return
     */
    int getActionPathsPriority();

    /**
     * The icon to be used for the node. If multiple FileNodeInfos are registered,
     * the icon with the highest {@link #getIconPriority() } is used.<br/>
     * It's OK to return null here, if you only need to provide additional action
     * paths.
     * @return
     */
    ImageIcon getIcon();

    /**
     * If multiple FileNodeInfos are registered, then the icon with the highest
     * priority is used. Lower number means higher priority.<br/>
     * Default priority is considered to be 0.
     * @return
     */
    int getIconPriority();

    /**
     * List of layer.xml paths, that should be used to find
     * {@link CapabilityProvider}s for this file's node.
     * @param p can be null, in which case project specific paths won't be
     * included in the list
     * @return
     */
    List<String> getCapabilityProviderPaths(Project p);
}