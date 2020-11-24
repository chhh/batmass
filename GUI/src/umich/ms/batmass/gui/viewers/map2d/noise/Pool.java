/*
 * Copyright 2020 chhh.
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
package umich.ms.batmass.gui.viewers.map2d.noise;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author chhh
 */
public class Pool<T> {
  private final Supplier<T> factory;
  private final Consumer<T> cleanup;
  private final ConcurrentLinkedDeque<T> pool = new ConcurrentLinkedDeque<>();

  public Pool(Supplier<T> factory, Consumer<T> cleanup) {
    this.factory = factory;
    this.cleanup = cleanup != null ? cleanup : (t) -> {
    };
  }

  public T borrow() {
    T t = pool.pollFirst();
    return t != null ? t : factory.get();
  }

  public void surrender(T t) {
    cleanup.accept(t);
    pool.addLast(t);
  }

  public void purge() {
    pool.clear();
  }
}