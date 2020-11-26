/*
 * Copyright 2020 chhh.
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

import umich.ms.batmass.gui.viewers.map2d.PassiveMap2DOverlayProvider;
import umich.ms.batmass.gui.viewers.map2d.PassiveOverlayKey;

/**
 *
 * @author chhh
 */
public class MsgPassiveOverlay {
    public final PassiveMap2DOverlayProvider<?> overlay;
    public static enum Action {ADD, REMOVE, CLEAR}
    public final Action whatToDo;

    public MsgPassiveOverlay(Action whatToDo, PassiveMap2DOverlayProvider<?> overlayProvider) {
        this.whatToDo = whatToDo;
        this.overlay = overlayProvider;
    }
}