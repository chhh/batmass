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
package umich.ms.batmass.gui.management;

import umich.ms.batmass.data.core.api.AbstractUnloadable;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.datatypes.LCMSData;

/**
 * Put instances of this into {@link BMTopComponent}'s lookup using its
 * {@link BMTopComponent#addToLookup(java.lang.Object) } method, then when
 * the component will be closing, it will automagically free the resources.
 * @author Dmitry Avtonomov
 */
public class UnloadableLCMSData extends AbstractUnloadable<LCMSData> {

    public UnloadableLCMSData(LCMSData data) {
        super(data);
    }
    
    @Override
    public void unload() {
        data.releaseMemory();
    }
}
