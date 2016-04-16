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

import java.util.EventListener;
import umich.ms.batmass.gui.viewers.map2d.events.ZoomEvent;

/**
 *
 * @author dmitriya
 */
public interface Map2DZoomEventListener extends EventListener {
    
    /**
     * Called when zoom-in/out begins or finishes.
     * {@link Map2DZoomLevel} in the event is the initial (starting) zoom level
     * when the event is fired for "beginning zoom" and the final level when
     * "finished zoom".
     * 
     * @param e
     */
    public void handleZoomEvent(ZoomEvent e);
}
