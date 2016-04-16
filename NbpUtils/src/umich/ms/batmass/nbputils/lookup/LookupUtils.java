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
package umich.ms.batmass.nbputils.lookup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Utility class for working with lookups. Especially useful is the caching
 * feature, which stores all the Lookups.forPath() instances, produced from
 * querying the layer.
 * @author Dmitry Avtonomov
 */
public abstract class LookupUtils {
    private LookupUtils(){};

    private static final Object LOCK = new Object();
    // This serves as cache - it's implied that layer.xml contents won't
    // change at runtime, so we can safely cache all creations of
    // Lookups.forPath()
    private static volatile Map<String, Lookup> lookupsForPaths = new ConcurrentHashMap<>();

    /**
     * Finds an already existing Lookup created for a path in layer. If nothing
     * is found, creates a new Lookups.forPath and caches it.
     * @param layerPath
     * @return
     */
    public  static Lookup getLookupForPath(String layerPath) {
        Lookup lkp = lookupsForPaths.get(layerPath);
        if (lkp == null) {
            synchronized (LOCK) {
                lkp = lookupsForPaths.get(layerPath);
                if (lkp == null) {
                    lkp = Lookups.forPath(layerPath);
                    lookupsForPaths.put(layerPath, lkp);
                }
            }
        }
        return lkp;
    }
    
}
