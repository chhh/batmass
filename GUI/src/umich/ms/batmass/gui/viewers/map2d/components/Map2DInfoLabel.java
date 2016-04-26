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

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import umich.ms.batmass.gui.viewers.map2d.util.NoopGraphics;

/**
 *
 * @author Dmitry Avtonomov
 */
public class Map2DInfoLabel extends javax.swing.JLabel implements IMap2DInfoDisplayer {

    private Double mzStart = null;
    private Double mzEnd = null;
    private Double rtStart = null;
    private Double rtEnd = null;
    private Double mouseMz = null;
    private Double mouseRt = null;
    private Double minIntensity = null;
    private Double maxIntensity = null;
    private volatile NoopGraphics g = null;
    Container validateRoot = null;
    


    @Override
    public void setMzRange(Double mzStart, Double mzEnd) {
        this.mzStart = mzStart;
        this.mzEnd = mzEnd;
    }

    @Override
    public void setRtRange(Double rtStart, Double rtEnd) {
        this.rtStart = rtStart;
        this.rtEnd = rtEnd;
    }

    @Override
    public void setMouseCoords(Double mz, Double rt) {
        mouseMz = mz;
        mouseRt = rt;
    }

    @Override
    public void setIntensityRange(Double minIntensity, Double maxIntensity) {
        this.minIntensity = minIntensity;
        this.maxIntensity = maxIntensity;
    }

    @Override
    public void refresh() {
        final String desc = generateTextDescription();
        if (SwingUtilities.isEventDispatchThread()) {
            // this method should always be called from EDT only, but just in case..
            safelySetText(desc);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    safelySetText(desc);
                }
            });
        }
    }
    
    /**
     * Update the text on label without flickering.<br/>
     * Flickering happens when you have a label displaying HTML and the text
     * has to be wrapped. In that case the label thinks that it will only have one line,
     * and after the first render it has to be revalidated to wrap the text properly,
     * which results in visible flickering.
     * @param text 
     */
    private void safelySetText(String text) {
        setText(text);

        // this code should prevent flickering when the label becomes multi-line
        if (validateRoot == null)
            return;
//        validateRoot.validate();
        paint(getG());
        validateRoot.validate();
    }

    /**
     * Overriding to make sure we always update the current {@code validateRoot}
     * of this JLabel.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        Container validationRoot = this;
        while (!validationRoot.isValidateRoot()) {
            Container parent = validationRoot.getParent();
            if (parent == null) {
                break;
            }
            validationRoot = parent;
        }
        validateRoot = validationRoot;
    }

    /**
     * Returns a singleton fake Graphics which does nothing.
     * @return 
     */
    public NoopGraphics getG() {
        NoopGraphics gg = g;
        if (gg == null) {
            synchronized(this) {
                gg = g;
                if (gg == null) {
                    g = gg = new NoopGraphics(0, 0, getWidth(), getHeight(), getGraphicsConfiguration(), false, false);
                }
            }
        }
        return gg;
    }
    
    private String generateTextDescription() {
        List<String> infos = new ArrayList<>();
        if (mzStart != null && mzEnd != null)
            infos.add(String.format("m/z: %.2f - %.2f", mzStart, mzEnd));
        if (rtStart != null && rtEnd != null)
            infos.add(String.format("RT: %.2f - %.2fm", rtStart, rtEnd));
        if (minIntensity != null && maxIntensity != null) {
            String format = "Intensity: %.0f - %.0f";
            if (minIntensity < 10d || maxIntensity < 10d) {
                format = "Intensity: %.2f - %.2f";
            }
            infos.add(String.format(format, minIntensity, maxIntensity));
        }
        if (mouseMz != null && mouseRt != null)
            infos.add(String.format("Cursor: (mz: %8.4f rt: %5.2fm)", mouseMz, mouseRt));

        StringBuilder sb = new StringBuilder("<html>");
        for (int i = 0; i < infos.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(infos.get(i));
        }
        sb.append("</html>");

        return sb.toString();
    }
}
