/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.core.spi.nodes;

import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.filesupport.core.types.descriptor.FileDescriptorDataObject;

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
public interface FileNodeInfo extends NodeInfo {
    
    /**
     * Implementation of this interface will be used to determine if this NodeInfo
     * supports the file, for which it's being applied.
     * @return
     */
    FileTypeResolver getFileTypeResolver();
}
