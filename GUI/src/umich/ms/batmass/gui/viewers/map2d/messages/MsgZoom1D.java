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
package umich.ms.batmass.gui.viewers.map2d.messages;


import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.datatypes.scan.IScan;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MsgZoom1D {
    private final Object origin;
    private final MzRtRegion region;
    private final IScan scan;

    public MsgZoom1D(Object origin, MzRtRegion region, IScan scan) {
        this.origin = origin;
        this.region = region;
        this.scan = scan;
    }

    public Object getOrigin() {
        return origin;
    }

    public MzRtRegion getRegion() {
        return region;
    }

    public IScan getScan() {
        return scan;
    }
}
