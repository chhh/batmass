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
package umich.ms.batmass.projects.core.services.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A generic layer path provider annotation, annotate a class with a no-argument
 * constructor with it, and an instance of that object will be installed into
 * the system-filesystem.
 * @author Dmitry Avtonomov
 */
public interface LayerPathProvider {
    /**
     * These are the paths, that users of this Provider will receive.
     * @return 
     */
    public String[] getPaths();

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    @interface Registration {
        /**
         * The path in the layer where your instances will be created.
         * @return
         */
        String[] paths();
        int position() default Integer.MAX_VALUE;
    }
}
