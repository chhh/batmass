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

import java.util.ArrayList;
import java.util.List;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author Dmitry Avtonomov
 */
public class Map2DInfoStatusBar implements IMap2DInfoDisplayer {
    
    private Double mzStart = null;
    private Double mzEnd = null;
    private Double rtStart = null;
    private Double rtEnd = null;
    private Double mouseMz = null;
    private Double mouseRt = null;
    private Double minIntensity = null;
    private Double maxIntensity = null;

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
        String msg = generateTextDescription();
        StatusDisplayer.getDefault().setStatusText(msg);
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

        StringBuilder sb = new StringBuilder();
        //sb.append("<html>");
        for (int i = 0; i < infos.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(infos.get(i));
        }
        //sb.append("</html>");

        return sb.toString();
    }
}
