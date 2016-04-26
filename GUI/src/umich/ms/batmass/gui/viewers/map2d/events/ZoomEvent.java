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
package umich.ms.batmass.gui.viewers.map2d.events;

import umich.ms.batmass.gui.viewers.map2d.components.Map2DZoomLevel;



/**
 *
 * @author Dmitry Avtonomov
 */
public class ZoomEvent {
    public enum TYPE {
        ZOOM_START,
        ZOOM_END
    }
    Map2DZoomLevel zoomLevel;
    ZoomEvent.TYPE zoomEventType;

    public ZoomEvent(Map2DZoomLevel zoomLevel, TYPE zoomEventType) {
        this.zoomLevel = zoomLevel;
        this.zoomEventType = zoomEventType;
    }

    public Map2DZoomLevel getZoomLevel() {
        return zoomLevel;
    }

    public TYPE getZoomEventType() {
        return zoomEventType;
    }
    
    public boolean isStart() {
        return zoomEventType == TYPE.ZOOM_START;
    }
    
    public boolean isFinish() {
        return zoomEventType == TYPE.ZOOM_END;
    }
}
