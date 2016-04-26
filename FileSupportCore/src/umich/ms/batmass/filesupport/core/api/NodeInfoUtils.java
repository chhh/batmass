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
package umich.ms.batmass.filesupport.core.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.Lookup;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.filesupport.core.spi.nodes.AbstractCapabilityProvider;
import umich.ms.batmass.filesupport.core.spi.nodes.CapabilityProvider;
import umich.ms.batmass.filesupport.core.spi.nodes.FileNodeInfo;
import umich.ms.batmass.filesupport.core.spi.nodes.NodeInfo;
import umich.ms.batmass.nbputils.LayerUtils;
import umich.ms.batmass.nbputils.lookup.LookupUtils;
import umich.ms.batmass.projects.core.util.BMProjectUtils;

/**
 *
 * @author Dmitry Avtonomov
 */
@SuppressWarnings("rawtypes")
public class NodeInfoUtils {
    private NodeInfoUtils() {
    }


    /**
     * Find all {@link FileNodeInfo}s that match a particular resolver.
     *
     * @param ftr resolver from the parser, that claimed the file
     *
     * @return an empty list, if nothing was found, otherwise all matching
     * InfoProviders will be returned.
     */
    public static List<FileNodeInfo> findNodeInfos(FileTypeResolver ftr) {
        List<FileNodeInfo> result = new ArrayList<>(5);
        Lookup lkp = LookupUtils.getLookupForPath(NodeInfo.LAYER_REGISTRATION_PATH);
        Collection<? extends FileNodeInfo> nodeInfos = lkp.lookupAll(FileNodeInfo.class);
        for (FileNodeInfo nodeInfo : nodeInfos) {
            if (nodeInfo.getFileTypeResolver().matches(ftr)) {
                result.add(nodeInfo);
            }
        }

        return result;
    }

    /**
     * Generates the conventional paths for node's Actions in the layer.
     * @param projectType can be null, the path will include "any-project-type"
     * @param fileCategory can be null, the path will include "any-file-category"
     * @param fileType can be null, the path will include "any-file-type"
     * @return
     */
    public static String getNodeActionsPath(String projectType, String fileCategory, String fileType) {
        if (projectType == null || projectType.isEmpty()) {
            projectType = BMProjectUtils.getProjectAnyType();
        }
        if (fileCategory == null || fileCategory.isEmpty()) {
            fileCategory = FileTypeResolverUtils.getFileCategoryAny();
        }
        if (fileType == null || fileType.isEmpty()) {
            fileType = FileTypeResolverUtils.getFileTypeAny();
        }
        return LayerUtils.getLayerPathUnsafe(
                NodeInfo.ACTIONS_LAYER_PATH_BASE, projectType, fileType, fileCategory);
    }

    /**
     * Generates the conventional paths for node's Capabilities in the layer.
     * @see CapabilityProvider
     * @see AbstractCapabilityProvider
     * @param projectType can be null, the path will include "any-project-type"
     * @param fileCategory can be null, the path will include "any-file-category"
     * @param fileType can be null, the path will include "any-file-type"
     * @return
     */
    public static String getNodeCapabilitiesPath(String projectType, String fileCategory, String fileType) {
        if (projectType == null || projectType.isEmpty()) {
            projectType = BMProjectUtils.getProjectAnyType();
        }
        if (fileCategory == null || fileCategory.isEmpty()) {
            fileCategory = FileTypeResolverUtils.getFileCategoryAny();
        }
        if (fileType == null || fileType.isEmpty()) {
            fileType = FileTypeResolverUtils.getFileTypeAny();
        }
        return LayerUtils.getLayerPathUnsafe(
                CapabilityProvider.CAPABILITIES_LAYER_PATH_BASE, projectType, fileType, fileCategory);
    }
    
}
