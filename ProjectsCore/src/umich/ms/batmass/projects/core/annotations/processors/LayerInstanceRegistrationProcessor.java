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
package umich.ms.batmass.projects.core.annotations.processors;

import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.openide.filesystems.annotations.LayerBuilder;
import org.openide.filesystems.annotations.LayerGeneratingProcessor;
import org.openide.filesystems.annotations.LayerGenerationException;
import org.openide.util.lookup.ServiceProvider;
import umich.ms.batmass.nbputils.LayerUtils;
import umich.ms.batmass.projects.core.annotations.LayerInstance;
import umich.ms.batmass.projects.core.annotations.LayerRegistration;

/**
 * @deprecated use {@link LayerRegistration} annotation, for which
 * {@link LayerRegistrationProcessor} will be used. These annotations are
 * exactly similar.
 * @author Dmitry Avtonomov
 */
@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes("umich.ms.batmass.projects.core.annotations.LayerInstance.Registration")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@Deprecated
public class LayerInstanceRegistrationProcessor extends LayerGeneratingProcessor {

    @Override
    protected boolean handleProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws LayerGenerationException {
        
        Elements elements = processingEnv.getElementUtils();
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(LayerInstance.Registration.class);
        for (Element e : elementsAnnotatedWith) {
            TypeElement clazz = (TypeElement) e;
            LayerInstance.Registration annotation = clazz.getAnnotation(LayerInstance.Registration.class);
            String teName = elements.getBinaryName(clazz).toString();

            String[] paths = annotation.paths();
            for (String path : paths) {
                String fileName = LayerUtils.getLayerFriendlyInstanceName(teName);
                path = LayerUtils.getLayerPath(path, fileName);
                
                LayerBuilder.File f = layer(e)
                    .file(path)
                    .position(annotation.position());
                f.write();
            }
        }
        return true;
    }

}