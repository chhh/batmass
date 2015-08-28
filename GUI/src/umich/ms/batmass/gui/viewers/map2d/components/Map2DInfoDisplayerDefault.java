/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.viewers.map2d.components;

/**
 * Default do-nothing implementation, for ease of use.<br/>
 * When {@link Map2DPanel} is created, an instance of this one is installed by default.
 * If you want to actually display something somewhere, call 
 * {@link Map2DPanel#setInfoDisplayer(umich.gui.viewers.scancollection2d.components.IMap2DInfoDisplayer) }
 * and provide some JComponent capable of drawing stuff on itself.
 * 
 * @author Dmitry Avtonomov
 */
public class Map2DInfoDisplayerDefault implements IMap2DInfoDisplayer {

    @Override
    public void setMzRange(Double mzStart, Double mzEnd) {
        
    }

    @Override
    public void setRtRange(Double rtStart, Double rtEnd) {
        
    }

    @Override
    public void refresh() {
        
    }

    @Override
    public void setMouseCoords(Double mz, Double rt) {
        
    }

    @Override
    public void setIntensityRange(Double minIntensity, Double maxIntensity) {
        
    }
    
}
