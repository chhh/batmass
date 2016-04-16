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
package umich.ms.batmass.gui.viewers.featuretable.components;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import net.engio.mbassy.bus.MBassador;
import org.netbeans.swing.etable.ETable;
import umich.ms.batmass.filesupport.files.types.umpire.data.features.UmpireFeaturesTableModel;
import umich.ms.batmass.gui.core.api.comm.eventbus.AbstractBusPubSub;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.components.featuretable.FeatureTableModel;
import umich.ms.batmass.gui.viewers.featuretable.messages.MsgFeatureClick;

/**
 *
 * @author Dmitry Avtonomov
 */
public class FeatureTable extends ETable {

    protected BusHandler busHandler;


    public FeatureTable() {
        constructor();
    }

    /**
     * A regular super-fast table, can display millions of rows.
     * @param tm consider providing a {@link FeatureTableModel} here
     */
    public FeatureTable(TableModel tm) {
        super(tm);
        constructor();
    }

    public FeatureTable(TableModel tm, TableColumnModel cm) {
        super(tm, cm);
        constructor();
    }

    private void constructor() {
        busHandler = new BusHandler();
        this.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent me) {
                ETable table = (ETable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    
                    // TODO: the model needs to be more specific here, so we could get mz and rt rows
                    // TODO: ACHTUNG: WARNING: remove this dirty hack
                    MzRtRegion region = null;
                    if (FeatureTable.this.getModel() instanceof UmpireFeaturesTableModel) {
                        int mzLoIdx = table.getColumnModel().getColumnIndex("m/z lo");
                        int mzHiIdx = table.getColumnModel().getColumnIndex("m/z hi");
                        int rtLoIdx = table.getColumnModel().getColumnIndex("RT lo");
                        int rtHiIdx = table.getColumnModel().getColumnIndex("RT hi");

                        Double mzLo = (Double)getValueAt(row, mzLoIdx) - 3d;
                        Double mzHi = (Double)getValueAt(row, mzHiIdx) + 3d;
                        Double rtLo = (Double)getValueAt(row, rtLoIdx) - 5d;
                        Double rtHi = (Double)getValueAt(row, rtHiIdx) + 5d;

                        region = new MzRtRegion(mzLo, mzHi, rtLo, rtHi);
                    } else if (FeatureTable.this.getModel() instanceof FeatureTableModel) {
                        FeatureTableModel model = (FeatureTableModel)FeatureTable.this.getModel();
                        region = model.rowToRegion(table.convertRowIndexToModel(row));
                    }
                    if (region != null) {
                        getBusHandler().featureClicked(region);
                    }
                }
            }
        });
    }
    
    

    @Override
    public boolean isPopupUsedFromTheCorner() {
        return false;
    }

    public BusHandler getBusHandler() {
        return busHandler;
    }

    public class BusHandler extends AbstractBusPubSub {
        private FeatureTable table = FeatureTable.this;
        private volatile boolean isResponding = false;
        
        public void featureClicked(MzRtRegion region) {
            
            for (MBassador<Object> bus : getBuses()) {
                bus.publish(new MsgFeatureClick(region, FeatureTable.this));
            }
        }
        
    }
}
