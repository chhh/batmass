/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.viewers.map2d.components;

/**
 *
 * @author dmitriya
 */
public interface IMap2DInfoDisplayer {
    
    public void setMzRange(Double mzStart, Double mzEnd);
    
    public void setRtRange(Double rtStart, Double rtEnd);
    
    public void setMouseCoords(Double mz, Double rt);
    
    public void setIntensityRange(Double minIntensity, Double maxIntensity);
    
    /**
     * Updates the visual state, should be called after the info is updated.
     */
    public void refresh();
}
