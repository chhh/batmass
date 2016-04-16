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
 * Simple implementation of comparison of resources used by Unloadables by their
 * references.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractUnloadable<T> implements Unloadable {
    protected final T data;

    public AbstractUnloadable(T data) {
        this.data = data;
    }


    @Override
    public T getResource() {
        return data;
    }

    @Override
    public boolean isSameResource(Unloadable other) {
        // this was a bad idea, as two different objects might return
        // true from .equals method
        //return Objects.equals(this, other);

        return this.getResource() == other.getResource();
    }

}
