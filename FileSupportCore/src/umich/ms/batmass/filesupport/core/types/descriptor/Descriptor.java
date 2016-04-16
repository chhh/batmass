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
package umich.ms.batmass.filesupport.core.types.descriptor;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dummy object which should be extended by concrete Descriptors.
 * E.g. {@link FileDesc} might have a path to the original file, it's size,
 * but still needs to provide UID for internal linking.
 * @author dmitriya
 */
public abstract class Descriptor implements Serializable {
    private static volatile transient AtomicLong importCounter = new AtomicLong(0);
    protected String UID;
    public static final String PROP_UID = "uid";
    
//    /**
//     * Never call this method yourself, it's here only for de-serialization.
//     * @param s
//     */
//    public final void setUID(String s) {
//        UID = s;
//    }
//
    /**
     * A unique identifier for the file, that this Descriptor links to.
     * Used to simplify internal linking.
     * @return
     */
    public final String getUID() {
        return UID;
    }
    
    public final String calcUID(String someId) {
        return someId + "_" + String.valueOf(System.nanoTime()) + "_" + String.valueOf(getImportCounterAndIncrement());
    }
    
    private static synchronized long getImportCounterAndIncrement() {
        return importCounter.getAndIncrement();
    }

    /**
     * The type of the descriptor. This can be used for cases when the same file
     * type can be used by multiple parsers. The default strategy to be used is
     * to use the simple class name (i.e. not the fully qualified name, but just
     * the name of the class).
     * @return the type
     */
    public abstract String getDescriptorType();
}
