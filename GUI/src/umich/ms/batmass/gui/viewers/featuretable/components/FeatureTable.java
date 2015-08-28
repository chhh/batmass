/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.featuretable.components;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import net.engio.mbassy.bus.MBassador;
import org.netbeans.swing.etable.ETable;
import umich.ms.batmass.gui.core.api.comm.eventbus.AbstractBusPubSub;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
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

    public FeatureTable(TableModel dm) {
        super(dm);
        constructor();
    }

    public FeatureTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
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
                    int mzLoIdx = table.getColumnModel().getColumnIndex("m/z lo");
                    int mzHiIdx = table.getColumnModel().getColumnIndex("m/z hi");
                    int rtLoIdx = table.getColumnModel().getColumnIndex("RT lo");
                    int rtHiIdx = table.getColumnModel().getColumnIndex("RT hi");
                    
                    Double mzLo = (Double)getValueAt(row, mzLoIdx) - 3d;
                    Double mzHi = (Double)getValueAt(row, mzHiIdx) + 3d;
                    Double rtLo = (Double)getValueAt(row, rtLoIdx) - 5d;
                    Double rtHi = (Double)getValueAt(row, rtHiIdx) + 5d;
                    
                    
                    MzRtRegion region = new MzRtRegion(mzLo, mzHi, rtLo, rtHi);
                    getBusHandler().featureClicked(region);
                }
            }
        });
    }


    @Override
    public boolean isPopupUsedFromTheCorner() {
        return true;
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
