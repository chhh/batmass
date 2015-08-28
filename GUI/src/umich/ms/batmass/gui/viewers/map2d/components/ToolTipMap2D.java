/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.map2d.components;

import javax.swing.JToolTip;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author dmitriya
 */
public class ToolTipMap2D extends JToolTip {

    private String separator = null;
    
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public void setTipText(String tipText) {
//        String[] lines = tipText.split(separator);
        String[] lines = StringUtils.splitByWholeSeparator(tipText, separator);
        StringBuilder sb = new StringBuilder(tipText.length());
        sb.append("<html>");
        sb.append(StringUtils.join(lines, "<br/>"));
        sb.append("</html>");
        super.setTipText(sb.toString());
    }
}
