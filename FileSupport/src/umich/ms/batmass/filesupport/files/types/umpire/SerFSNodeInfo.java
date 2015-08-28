/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire;

import umich.ms.batmass.filesupport.core.annotations.NodeInfoRegistration;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.filesupport.core.spi.nodes.AbstractFileNodeInfo;

/**
 *
 * @author Dmitry Avtonomov
 */
@NodeInfoRegistration(
        fileCategory = SerFSAsFeaturesTypeResolver.CATEGORY,
        fileType = SerFSAsFeaturesTypeResolver.TYPE
)
public class SerFSNodeInfo extends AbstractFileNodeInfo {
    @Override
    public FileTypeResolver getFileTypeResolver() {
        return SerFSAsFeaturesTypeResolver.getInstance();
    }

}