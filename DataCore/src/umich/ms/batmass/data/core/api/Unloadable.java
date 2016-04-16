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
package umich.ms.batmass.data.core.api;

/**
 * Marks resources, that can be automatically unloaded from memory, e.g. when
 * an editor closes and the fully parsed representation of a file is not needed
 * anymore.
 * @author Dmitry Avtonomov
 */
public interface Unloadable {
    /**
     * When called, should make an attempt to free memory (nullify hard refs or
     * at least set WeakReferences for resources).
     */
    void unload();

    /**
     * A check if this Unloadable represents the same resource as some other
     * Unloadable.<br/>
     * 
     * Use case:<br/>
     *      Your TC receives some data and puts it into its lookup. When the
     * TC is closing, it scans other TC's lookups for presence of Unloadables
     * representing the same resources as this one is using - if no one is using
     * it, it can be safely unloaded. The problem with this is that each TC
     * might create it's own implementation of Unloadable and store it in the
     * lookup, however it will still be for the same resource, such as LCMSData.
     * So we can't just use .equals() to check for resource similarity.
     * 
     * @param other
     * @return
     */
    boolean isSameResource(Unloadable other);

    /**
     * The resource managed by this Unloadable. Needed to run checks against
     * other Unloadables, if they use the same resource.
     * @return
     */
    Object getResource();
}
