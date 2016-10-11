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
package umich.ms.batmass.filesupport.files.types.mzrt.data;

import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.filesupport.files.types.mzrt.model.MzrtFile;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MzrtFeatures extends Features<MzrtFeature> {
    private MzrtFile file;

    public MzrtFeatures(MzrtFile file) {
        this.file = file;
    }

    public MzrtFile getFile() {
        return file;
    }
}
