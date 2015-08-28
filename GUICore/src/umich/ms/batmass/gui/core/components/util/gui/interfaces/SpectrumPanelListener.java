/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-feb-2005
 * Time: 7:23:04
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
