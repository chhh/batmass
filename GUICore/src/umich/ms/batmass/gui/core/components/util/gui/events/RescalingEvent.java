/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-feb-2005
 * Time: 7:26:12
 */
package umich.ms.batmass.gui.core.components.util.gui.events;

import javax.swing.JPanel;


/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2007/10/22 10:09:02 $
 */

/**
 * This class implements a resizing event that occurred on a SpectrumPanel.
 *
 * @author Lennart Martens
 * @version $Id: RescalingEvent.java,v 1.1 2007/10/22 10:09:02 lennart Exp $
 */
public class RescalingEvent {
    /**
     * The minimal mass to display after rescaling.
     */
    private double iMinMass = 0;

    /**
     * The maximal mass to display after rescaling.
     */
    private double iMaxMass = 0;

    /**
     * This JPanel represents the source.
     */
    private JPanel iSource = null;

    /**
     * The constructor takes the resizing event X coordinates in mass units.
     *
     * @param aSource   The JPanel that threw the event.
     * @param aMinMass  The minimal mass to display after rescaling.
     * @param aMaxMass  The maximal mass to display after rescaling.
     */
    public RescalingEvent(JPanel aSource, double aMinMass, double aMaxMass) {
        this.iSource = aSource;
        this.iMinMass = aMinMass;
        this.iMaxMass = aMaxMass;
    }

    /**
     * Returns the maximum mass.
     *
     * @return the maximum mass
     */
    public double getMaxMass() {
        return iMaxMass;
    }

    /**
     * Returns the minimum mass.
     *
     * @return the minimum mass
     */
    public double getMinMass() {
        return iMinMass;
    }

    /**
     * Returns the JPanel source.
     *
     * @return the JPanel source
     */
    public JPanel getSource() {
        return iSource;
    }
}
