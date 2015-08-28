/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpireid.data.tabledomain;

import org.netbeans.swing.outline.RowModel;
import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpireId;
import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpirePSM;

/**
 *
 * @author Dmitry Avtonomov
 */
@SuppressWarnings({"rawtypes"})
public class UmpireIdRowModel implements RowModel {

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return Float.class;
            case 2:
                return Byte.class;
            case 3:
                return Float.class;
            case 4:
                return Float.class;
            case 5:
                return Boolean.class;
            case 6:
                return Byte.class;
            case 7:
                return Integer.class;
        }

        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "TPP Seq";
            case 1:
                return "Mass";
            case 2:
                return "Charge";
            case 3:
                return "m/z";
            case 4:
                return "Max Prob";
            case 5:
                return "Is decoy";
            case 6:
                return "# Missed Cleav.";
            case 7:
                return "# Children";
        }

        return "UNKNOWN";
    }

    @Override
    public Object getValueFor(Object node, int column) {


        if (node instanceof UmpireId) {
            UmpireId id = (UmpireId) node;
            switch (column) {
                case 0:
                    return id.getTppSeq();
                case 1:
                    return id.getWeight();
                case 2:
                    return id.getCharge();
                case 3:
                    return id.getMz();
                case 4:
                    return id.getMaxProb();
                case 5:
                    return id.isDecoy();
                case 6:
                    return id.getMissedCleavages();
                case 7:
                    return id.getPsms().length;
            }
        }

        if (node instanceof UmpirePSM) {
            UmpirePSM psm = (UmpirePSM) node;
            switch (column) {
                case 0:
                    return null;
                case 1:
                    return null;
                case 2:
                    return psm.getCharge();
                case 3:
                    return psm.getObservedPrecursorMz();
                case 4:
                    return psm.getProbability();
                case 5:
                    return null;
                case 6:
                    return null;
                case 7:
                    return null;
            }
        }

        return null;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    @Override
    public void setValueFor(Object node, int column, Object value) {
        throw new UnsupportedOperationException("Editing not allowed.");
    }
}
