/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.core.components.featuretable;

import javax.swing.table.TableModel;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;

/**
 *
 * @author Dmitry Avtonomov
 */
public interface FeatureTableModel extends TableModel {
    /**
     * This is used to convert a row from the table to an mz-rt region on the 2D map,
     * so that it can be zoomed in automatically on double-click.
     * @param row
     * @return null, if you don't want to have this functionality implemented
     */
    MzRtRegion rowToRegion(int row);
}
