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
package umich.ms.batmass.gui.core.api;

import javax.swing.JComponent;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Simply a JComponent with a lookup and a component type name.
 * @author Dmitry Avtonomov
 */
public class BMComponentJComp extends JComponent implements BMComponent {
protected InstanceContent ic;
    protected Lookup lkp;

    public BMComponentJComp() {
        super();
        ic = new InstanceContent();
        lkp = new AbstractLookup(ic);
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    /**
     * Just adds an object to the instance content of this TC.
     * @param o to be added
     */
    @Override
    public void addToLookup(Object o) {
        ic.add(o);
    }

    /**
     * Just removes the object from instance content of this TC. No indication
     * of whether the object was present there in the first place is provided,
     * so if you want to be sure, query the lookup first.
     * @param o
     */
    @Override
    public void removeFromLookup(Object o) {
        ic.remove(o);
    }

    /**
     * The default implementation simply returns the canonical name.
     * @return
     */
    @Override
    public String getComponentType() {
        return this.getClass().getCanonicalName();
    };
}
