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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import umich.ms.batmass.nbputils.refs.RefQ;

/**
 * Holds some data and tracks its usage by users. Serves as a link between
 * data type {@code <T>} and a {@code DataSource<T>}.
 * When there are no active users unloads the data from memory.
 * @author Dmitry Avtonomov
 * @param <T> Data type for this container
 */
public abstract class DataContainer<T> implements Unloadable {
    /** Users of the data. */
    protected final Cache<Object, Boolean> cache;
    private final RemovalListener<Object, Boolean> cacheRemovalListener;

    private volatile T data;
    private final DataSource<T> source;

    public DataContainer(DataSource<T> source) {
        this.source = source;
        this.cacheRemovalListener = buildRemovalListener();
        this.cache = CacheBuilder.newBuilder()
                .weakKeys()
                .concurrencyLevel(1)
                .removalListener(cacheRemovalListener)
                .build();
    }

    private RemovalListener<Object, Boolean> buildRemovalListener() {
        return new RemovalListener<Object, Boolean>() {
            @Override
            public void onRemoval(RemovalNotification<Object, Boolean> notification) {
                // if there are no more entries in the user cache - delete the data
                // this might be not a very good idea to do it like that
                // beacause we have no syncronization with loading/unloading mechanism
                // TODO: ideally we should be running some RxJava observable,
                //       which should only emit a value where there were no onRemoval() and load()
                //       calls for, say, 500ms.
                if (cache.size() == 0) {
                    data = null;
                }
            }
        };
    }


    /**
     * The source that is used to populate the data object maintained by this container.
     * It is better to deal with the data source via this DataContainer class, as it will
     * manage the loading/unloading of data, tracking data usage. When there are no users of the
     * loaded data - memory will be freed.
     * @return
     */
    public DataSource<T> getSource() {
        return source;
    }

    /**
     * Data in this container in its current state. If you haven't loaded the data
     * yourself, but there are other users, this might return non-null, if not
     * all other users have unloaded it.
     * @return null, if you haven't loaded the data yourself before.
     */
    public synchronized T getData() {
        return data;
    }

    /**
     * 
     * @param user
     * @return
     * @throws umich.ms.batmass.data.core.api.DataLoadingException
     */
    public synchronized T load(Object user) throws DataLoadingException {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        // loading data
        if (data == null) {
            data = source.load();
        }

        // adding user to the cache
        Boolean isPresent = cache.getIfPresent(user);
        if (isPresent == null) {
            cache.put(user, Boolean.TRUE);
        }

        return data;
    }

    /**
     *
     * @param user
     */
    public synchronized void unload(Object user) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        Boolean isPresent = cache.getIfPresent(user);
        if (isPresent == null) {
            throw new IllegalStateException(String.format("Unload was called by a user, "
                    + "which hasn't  loaed the data: %s", user.toString()));
        }

        // will automatically trigger eviction listener, which will clear the data,
        // if there are no users left
        cache.invalidate(user);
        
    }

    
    /**
     * This is just a phantom reference, which is placed on the automatic monitoring
     * queue. So that if a user is garbage collected and hasn't called {@link #unload(java.lang.Object) }
     * method itself, we will call it for him.<br/>
     * In fact, we won't be able to call that method exactly, because we won't have a pointer
     * to the original object anymore, so we just fire up cache cleanup. The cache has weak keys,
     * so when {@link #onFinalize() } is run, the key in the cache will point to null object.
     * We can count non-null keys in cache to determine if the data should be unloaded.
     */
    protected class User extends RefQ.User {
        public User(Object referent) {
            super(referent);
        }

        @Override
        public void onFinalize() {
            cache.cleanUp();
        }
    }


    //============================================================================
    //==============   
    //==============   Unloadable implementation
    //==============
    //============================================================================

    /**
     * You should be careful about calling this method - it removes all the users of this data
     * without any checks, as soon as all users are removed from cache, the whole data
     * will be automatically unloaded, so if anyone tries to access it via {@link #getData() } method,
     * they will get null.<br/>
     * On the other hand, if some user stored a reference to the data, it will be the responsibility
     * of that user now to remove the reference, so that the data can be garbage collected.<br/>
     * And if anyone tries to load the data again, a new copy will be created.
     */
    @Override
    public synchronized void unload() {
        // will automatically trigger eviction listener, which will clear the data
        cache.invalidateAll();
    }

    @Override
    public boolean isSameResource(Unloadable other) {
        return getResource() == other;
    }

    @Override
    public Object getResource() {
        return this;
    }
}
