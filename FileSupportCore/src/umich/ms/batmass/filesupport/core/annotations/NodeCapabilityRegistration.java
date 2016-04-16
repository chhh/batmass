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
package umich.ms.batmass.filesupport.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.filesupport.core.spi.nodes.CapabilityProvider;
import umich.ms.batmass.projects.core.type.BMProject;


/**
 * Used to register new {@link CapabilityProvider} into the layer.
 * @author Dmitry Avtonomov
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NodeCapabilityRegistration {
    /**
     * The specific project type for which the node-info will be registered.
     * Default is any project type.
     * @return lowercase string
     */
    String projectType() default BMProject.TYPE_ANY;
    /**
     * The specific type of the file. E.g. 'mzxml'. The complete specific type
     * is actually identified by a combination of {@link #fileCategory() } and
     * {@link #fileType() }.
     * @return lowercase string
     */
    String fileType() default FileTypeResolver.TYPE_ANY;
    /**
     * Category of the file. Please check what's currently available in the
     * system already, to figure out proper category names. Category names
     * should be all lowercase!<br/>
     * E.g. 'mzxml' files are mapped to 'lcms' category.
     * @return lowercase string
     */
    String fileCategory() default FileTypeResolver.CATEGORY_ANY;
    /**
     * Optional path extensions, as to where inside the default directory
     * to put instances of this class. Don't use this for now, just use the
     * default.
     * @return
     */
    String[] paths() default {};
    int position() default Integer.MAX_VALUE;
}