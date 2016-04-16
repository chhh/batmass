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
package umich.ms.batmass.filesupport.files.types.mzml;

import umich.ms.batmass.filesupport.core.annotations.NodeInfoRegistration;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.filesupport.core.spi.nodes.AbstractFileNodeInfo;

/**
 *
 * @author Dmitry Avtonomov
 */
@NodeInfoRegistration(
        fileCategory = MzMLAsLCMSTypeResolver.CATEGORY,
        fileType = MzMLAsLCMSTypeResolver.TYPE
)
public class MzMLNodeInfo extends AbstractFileNodeInfo {
    
    @Override
    public FileTypeResolver getFileTypeResolver() {
        return MzMLAsLCMSTypeResolver.getInstance();
    }

    
}
