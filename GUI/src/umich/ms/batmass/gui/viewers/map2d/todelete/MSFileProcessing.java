/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.map2d.todelete;

import umich.ms.datatypes.LCMSData;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class MSFileProcessing {

    /**
     * This name will be used further to place Actions in proper folders.
     * @return
     * @see
     */
    public abstract String getProcessingName();

    /**
     * Which file has been processed. Needed to later open views with overlays of processed data
     * over original spectra.
     * @return
     */
    public abstract LCMSData getParentFile();
}