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

import java.net.URI;

/**
 * A generic source of data, to be used in {@link DataContainer}s, as a DataContainer
 * manages the life-cycle of an object it will use the {@link #load() } method
 * of its assigned DataSource to load the data.
 * @author Dmitry Avtonomov
 * @param <T> The type of data this source produces
 */
public interface DataSource<T> {
    /**
     * Loads the data from some resource, such as a file.
     * @return
     * @throws umich.ms.batmass.data.core.api.DataLoadingException
     */
    T load() throws DataLoadingException;

    /**
     * URI of the data source. Might be a file, or a database table or whatever.
     * @return
     */
    URI getOriginURI();

}
