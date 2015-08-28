/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.map2d.util;

/**
 *
 * @author Dmitry Avtonomov
 */
import java.awt.Component;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *  This class can be used as the renderer and KeySelectionManager for an
 *	Object added to the ComboBoxModel.
 * <br/><br/>
 *  The class must be extended and the getDisplayValue() method must be
 *  implemented. This method will return a String to be rendered in the
 *  JComboBox. The same String will be used to do key selection of an
 *  item in the ComboBoxModel.
 * <br/>
 * We can't get rid of SuppressWarnings, because we're just overriding methods
 * from JComboBox interface.
 */
@SuppressWarnings(value = {"rawtypes", "unchecked"})
public abstract class KeySelectionRenderer extends BasicComboBoxRenderer
        implements JComboBox.KeySelectionManager {
    //  Used by the KeySelectionManager implementation to determine when to
    //  start a new search or append typed character to the existing search.

    private final long timeFactor;
    private long lastTime;
    private long time;
    private String prefix = "";

    public KeySelectionRenderer() {
        Long l = (Long) UIManager.get("ComboBox.timeFactor");
        timeFactor = l == null ? 1000L : l;
    }
    
    /**
     * Set up the combo-box to use this renderer.
     * @param comboBox combo box for which this renderer will be set up
     */
    public void addToComboBox(JComboBox comboBox) {
        comboBox.setRenderer(this);
        comboBox.setKeySelectionManager(this);
    }

    /**
     * This method must be implemented in the extended class.
     *
     * @param item an item from the ComboBoxModel
     * @return
     * @returns a String containing the text to be rendered for this item.
     */
    public abstract String getDisplayValue(Object item);

	//  Implement the renderer
    @Override
    public Component getListCellRendererComponent(
            JList list, Object item, int index, boolean isSelected, boolean hasFocus) {
        super.getListCellRendererComponent(list, item, index, isSelected, hasFocus);

        if (item != null) {
            setText(getDisplayValue(item));
        }

        return this;
    }

    //  Implement the KeySelectionManager
    @Override
    public int selectionForKey(char aKey, ComboBoxModel model) {
        time = System.currentTimeMillis();

        //  Get the index of the currently selected item
        int size = model.getSize();
        int startIndex = -1;
        Object selectedItem = model.getSelectedItem();

        if (selectedItem != null) {
            for (int i = 0; i < size; i++) {
                if (selectedItem == model.getElementAt(i)) {
                    startIndex = i;
                    break;
                }
            }
        }

        //  Determine the "prefix" to be used when searching the model. The
        //  prefix can be a single letter or multiple letters depending on how
        //  fast the user has been typing and on which letter has been typed.
        if (time - lastTime < timeFactor) {
            if ((prefix.length() == 1) && (aKey == prefix.charAt(0))) {
                // Subsequent same key presses move the keyboard focus to the next
                // object that starts with the same letter.
                startIndex++;
            } else {
                prefix += aKey;
            }
        } else {
            startIndex++;
            prefix = "" + aKey;
        }

        lastTime = time;

		//  Search from the current selection and wrap when no match is found
        if (startIndex < 0 || startIndex >= size) {
            startIndex = 0;
        }

        int index = getNextMatch(prefix, startIndex, size, model);

        if (index < 0) {
            // wrap
            index = getNextMatch(prefix, 0, startIndex, model);
        }

        return index;
    }

    /*
     **  Find the index of the item in the model that starts with the prefix.
     */
    private int getNextMatch(String prefix, int start, int end, ComboBoxModel model) {
        for (int i = start; i < end; i++) {
            Object item = model.getElementAt(i);

            if (item != null) {
                String displayValue = getDisplayValue(item).toLowerCase();

                if (displayValue.startsWith(prefix)) {
                    return i;
                }
            }
        }

        return -1;
    }
}
