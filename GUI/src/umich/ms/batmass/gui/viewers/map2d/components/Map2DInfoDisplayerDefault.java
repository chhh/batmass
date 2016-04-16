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
package umich.ms.batmass.gui.viewers.map2d.components;

/**
 * Default do-nothing implementation, for ease of use.<br/>
 * When {@link Map2DPanel} is created, an instance of this one is installed by default.
 * If you want to actually display something somewhere, call 
 * {@link Map2DPanel#setInfoDisplayer(umich.gui.viewers.scancollection2d.components.IMap2DInfoDisplayer) }
 * and provide some JComponent capable of drawing stuff on itself.
 * 
 * @author Dmitry Avtonomov
 */
public class Map2DInfoDisplayerDefault implements IMap2DInfoDisplayer {

    @Override
    public void setMzRange(Double mzStart, Double mzEnd) {
        
    }

    @Override
    public void setRtRange(Double rtStart, Double rtEnd) {
        
    }

    @Override
    public void refresh() {
        
    }

    @Override
    public void setMouseCoords(Double mz, Double rt) {
        
    }

    @Override
    public void setIntensityRange(Double minIntensity, Double maxIntensity) {
        
    }
    
}
