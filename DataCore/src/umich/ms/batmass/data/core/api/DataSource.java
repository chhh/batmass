/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
