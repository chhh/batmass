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
package umich.ms.batmass.gui.viewers.featuretable.messages;

import umich.ms.batmass.gui.core.api.data.MzRtRegion;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MsgFeatureClick {
    protected final MzRtRegion region;
    private final Object origin;
    
    public MsgFeatureClick(MzRtRegion region, Object origin) {
        this.region = region;
        this.origin = origin;
    }

    public MzRtRegion getRegion() {
        return region;
    }

    public Object getOrigin() {
        return origin;
    }
    
}
