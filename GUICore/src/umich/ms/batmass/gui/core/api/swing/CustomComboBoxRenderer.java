/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api.swing;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * This renderer will paint opaque labels for the items being displayed by the
 * combo box while painting the normal drop-down list.
 *
 * @author Dmitry Avtonomov
 */
public abstract class CustomComboBoxRenderer extends BasicComboBoxRenderer {

    @Override @SuppressWarnings({"rawtypes"})
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            // was used for debug
        //System.out.printf("Rendering value: %s, index: %d, selected: %s, focus: %s\n",
        //        value.toString(), index, Boolean.toString(isSelected), Boolean.toString(cellHasFocus));
        if (index >= 0) {
            setOpaque(true);
        } else {
                // index is set to -1 for the item that has been selected and is being rendered on the
            // face of inactive combo box
            setOpaque(false);
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setFont(list.getFont());

        if (value instanceof Icon) {
            setIcon((Icon) value);
        } else {
            setText((value == null) ? "" : getDisplayValue(value));
        }
        return this;
    }

    protected abstract String getDisplayValue(Object value);
}
