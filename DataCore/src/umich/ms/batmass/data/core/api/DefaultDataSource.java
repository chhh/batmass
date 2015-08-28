/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.api;

import java.net.URI;

/**
 * Default implementation, just storing the URI of the origin of this source.
 * @author Dmitry Avtonomov
 * @param <T>
 */
public abstract class DefaultDataSource<T> implements DataSource<T>{
    protected URI uri;

    public DefaultDataSource(URI origin) {
        this.uri = origin;
    }

    @Override
    public URI getOriginURI() {
        return uri;
    }
}
