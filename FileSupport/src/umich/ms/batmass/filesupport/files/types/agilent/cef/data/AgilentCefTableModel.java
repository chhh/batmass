/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.agilent.cef.data;

import java.util.List;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentCompound;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.AgilentMSPeak;
import umich.ms.batmass.filesupport.files.types.agilent.cef.model.IonId;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.components.featuretable.AbstractFeatureTableModel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class AgilentCefTableModel extends AbstractFeatureTableModel {
    List<AgilentCefFeature> features;
    
    protected String[] colNames = {
        /* 0 */ "Mono m/z",
        /* 1 */ "Num isotopes",
        /* 2 */ "Charge",
        /* 3 */ "RT",
        /* 4 */ "RT min",
        /* 5 */ "RT max",
        /* 6 */ "Total intensity",
        /* 7 */ "Max intensity",
        /* 8 */ "Adduct",
        /* 9 */ "Z Carrier"
    };
    protected Class[] colTypes = {
        /* 0 */ Double.class,
        /* 1 */ Integer.class,
        /* 2 */ Integer.class,
        /* 3 */ Double.class,
        /* 4 */ Double.class,
        /* 5 */ Double.class,
        /* 6 */ Integer.class,
        /* 7 */ Integer.class,
        /* 8 */ String.class,
        /* 9 */ String.class
    };

    public AgilentCefTableModel(List<AgilentCefFeature> features) {
        this.features = features;
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }
    
    @Override
    public int getRowCount() {
        return features.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AgilentCefFeature f = features.get(rowIndex);
        AgilentCompound ac = f.getCompund();
        List<AgilentMSPeak> peaks = ac.getPeaks();
        if (peaks.isEmpty()) {
            return null;
        }
        AgilentMSPeak p = peaks.get(0);
        switch (columnIndex) {
            case 0:
                return p.getMz();
            case 1:
                return ac.getPeaks().size();
            case 2:
                return p.getZ();
            case 3:
                return ac.getRt();
            case 4:
                return ac.getRtLo();
            case 5:
                return ac.getRtHi();
            case 6:
                return ac.getAbTot();
            case 7:
                return ac.getAbMax();
            case 8:
                IonId id = p.getIonId();
                return id == null ? "" : id.getAdduct();
            case 9:
                id = p.getIonId();
                return id == null ? "" : id.getzCarrier();
        }
        return null;
    }

    @Override
    public MzRtRegion rowToRegion(int row) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
