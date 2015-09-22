/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.core.components.featuretable;

import javax.swing.table.AbstractTableModel;

/**
 * Just a convenience class, so that you hopefully didn't forget that you can provide 
 * an improved table model, so that Map2D could be zoomed to your particular feature.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractFeatureTableModel extends AbstractTableModel implements FeatureTableModel {

    public AbstractFeatureTableModel() {
    }
    
}
