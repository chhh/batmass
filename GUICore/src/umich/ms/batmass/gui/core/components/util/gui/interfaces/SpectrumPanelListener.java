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
package umich.ms.batmass.gui.core.components.util.gui.interfaces;

import umich.ms.batmass.gui.core.components.util.gui.events.RescalingEvent;



/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2007/10/22 10:09:02 $
 */

/**
 * This interface describes the behaviour for a listener that wants to receive information
 * about events that occurred on a SpectrumPanel.
 *
 * @author Lennart Martens
 * @version $Id: SpectrumPanelListener.java,v 1.1 2007/10/22 10:09:02 lennart Exp $
 */
public interface SpectrumPanelListener {

    /**
     * This method will be called whenever the SpectrumPanel is rescaled.
     *
     * @param aSe    ResizinEvent with the details of the rescaling.
     */
    public void rescaled(RescalingEvent aSe);
}
