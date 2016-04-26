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

/**
 * Provides a mapping from a class name to a path in the layer.xml.
 * Intended use: to register class-specific actions. You register your actions
 * in a folder of your choosing, and provide an implementation of this
 * interface in project's lookup.
 * @author Dmitry Avtonomov
 */
public interface LayerMappedPathProvider extends LayerPathProvider {
    /**
     * Class type for which the mapping is to be applied. Think of it as a capability.
     * @return
     */
    public Class<?> getClassType();
}
