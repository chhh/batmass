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
package umich.ms.batmass.gui.viewers.map2d.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A better version of JComboBox, which handles situations, when your parent 
 * component size is not enough to display the drop-down menu. The menu will
 * be laid over any underlying elements and even stick outside of the main window
 * if its width is not enough to display te contents of the menu.<br/>
 * Taken from http://tutiez.com/how-make-jcombobox-drop-down-width-as-wide-as-needed.html
 * @author Dmitry Avtonomov
 */
public class BMJComboBox<E> extends JComboBox<E> {
    private boolean layingOut = false;
    private int widestLengh = 0;
    private boolean wide = true;

    public BMJComboBox(ComboBoxModel<E> aModel) {
        super(aModel);
    }

    public BMJComboBox(E[] items) {
        super(items);
    }

    public BMJComboBox() {
    }

    public boolean isWide() {
        return wide;
    }
    
    /**
     * When true, the elements of the drop-down list will be layered over any other
     * content, even if the lines don't fit the available JFrame size.
     * @param wide default is true
     */
    public void setWide(boolean wide) {
        this.wide = wide;
        widestLengh = getWidestItemWidth();
    }

    @Override
    public Dimension getSize() {
        Dimension dim = super.getSize();
        if (!layingOut && isWide()) {
            dim.width = Math.max(widestLengh, dim.width);
        }
        return dim;
    }

    /**
     * Gets width of the widest item in the current model, respecting the selected
     * font. Can be used to set preferred width of the component after the model
     * is updated.
     * @return 
     */
    public int getWidestItemWidth() {

        int numOfItems = this.getItemCount();
        Font font = this.getFont();
        FontMetrics metrics = this.getFontMetrics(font);
        int widest = 0;
        for (int i = 0; i < numOfItems; i++) {
            Object item = this.getItemAt(i);
            int lineWidth = metrics.stringWidth(item.toString());
            widest = Math.max(widest, lineWidth);
        }

        return widest + 5;
    }

    @Override
    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }


    public static void main(String[] args) {
        String title = "Combo Test";
        JFrame frame = new JFrame(title);

        String[] items = {"I need lot of width to be visible , oh am I visible now", "I need lot of width to be visible , oh am I visible now"};
        BMJComboBox<String> simpleCombo = new BMJComboBox<>();
        for (String s : items) {
            simpleCombo.addItem(s);
        }
        simpleCombo.setPreferredSize(new Dimension(180, 20));
        simpleCombo.setWide(true);
        JLabel label = new JLabel("Wider Drop Down Demo");

        frame.getContentPane().add(simpleCombo, BorderLayout.NORTH);
        frame.getContentPane().add(label, BorderLayout.SOUTH);
        int width = 200;
        int height = 150;
        frame.setSize(width, height);
        frame.setVisible(true);

    }
}
