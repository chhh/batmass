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
package umich.ms.batmass.gui.viewers.map2d.components;

import javax.swing.JToolTip;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Dmitry Avtonomov
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
