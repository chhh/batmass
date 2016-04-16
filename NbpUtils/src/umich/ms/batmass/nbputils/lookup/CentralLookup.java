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

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author dmitriya
 */
public class CentralLookup extends AbstractLookup {

    private InstanceContent content = null;
    private static CentralLookup def = new CentralLookup();
    
    public CentralLookup(InstanceContent content) {
        super(content);
        this.content = content;
    }
    
    public CentralLookup() {
        this(new InstanceContent());
    }
    
    public static CentralLookup getDefault() {
        return def;
    }
    
    public void add(Object instance) {
        content.add(instance);
    }
    
    public void remove(Object instance) {
        content.remove(instance);
    }

    /**
     * Adds an item to the lookup and immediately removes it,
     * can be used as notification mechanism, so that you didn't forget to
     * remove your notice from the lookup later.
     * @param notice
     */
    public void poke(Object notice) {
        content.add(notice);
        content.remove(notice);
    }
}
