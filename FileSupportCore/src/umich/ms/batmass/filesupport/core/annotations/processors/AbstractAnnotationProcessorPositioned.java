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
package umich.ms.batmass.filesupport.core.annotations.processors;

import javax.lang.model.element.Element;
import org.openide.filesystems.annotations.LayerBuilder;
import org.openide.filesystems.annotations.LayerGeneratingProcessor;
import org.openide.filesystems.annotations.LayerGenerationException;

/**
 * Just a handy class, that will print to console, whenever it writes files
 * to the layer. Written files must have a position attribute.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractAnnotationProcessorPositioned extends LayerGeneratingProcessor {

    protected void writeLayerFile(String path, Element e, int position)
            throws LayerGenerationException {

        System.out.printf("LayerGeneratingProcessor[%s] writes to:\n"
                + "\t%s\n", this.getClass().getCanonicalName(), path);

        LayerBuilder.File f = layer(e)
                .file(path)
                .position(position);
        f.write();
    }
}
