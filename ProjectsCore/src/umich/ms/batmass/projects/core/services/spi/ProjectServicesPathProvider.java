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

import umich.ms.batmass.projects.core.type.BMProject;
import umich.ms.batmass.projects.core.util.BMProjectUtils;

/**
 * Just a convenience service that is only meant for Projects to look up
 * additional paths for their ProjectServiceProviders.
 * Example:
 * <pre>
 *
 * {@literal @}ProjectServiceProvider(
 *     service=ProjectServicesPathProvider.class,
 *     projectType=MetabolomicsProject.TYPE)
 * </pre>
 * @see BMProject#getType()
 * @see BMProjectUtils#getProjectType(java.lang.Class) 
 * @author dmitriya
 */
public interface ProjectServicesPathProvider extends LayerPathProvider {

}
