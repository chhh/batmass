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
package umich.ms.batmass.gui.core.api.comm.messages;

import java.awt.Color;
import java.awt.color.ColorSpace;

/**
 * When a JComponent receives such a message, it should somehow highlight itself.
 * E.g. by changing border color.
 * @author Dmitry Avtonomov
 */
@SuppressWarnings("rawtypes")
public class MsgHighlight {
    protected final boolean doHightlight;
    protected final Color color;
    protected static final Color[] COLORS;
    static {
        Color[] baseColors = {Color.RED, Color.BLUE, Color.ORANGE, Color.GREEN, 
            Color.MAGENTA, Color.PINK, Color.CYAN};
        COLORS = new Color[baseColors.length];
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
        float[] comps = new float[cs.getNumComponents()];
        for (int i = 0; i < COLORS.length; i++) {
            Color c = baseColors[i];
            COLORS[i] = new Color(cs, c.getColorComponents(cs, comps), .75f);
        }
    }

    private static volatile int colorNum = 0;

    public MsgHighlight(boolean doHightlight) {
        this.doHightlight = doHightlight;
        color = generateColor();
    }

    public MsgHighlight(boolean doHightlight, Color color) {
        this.doHightlight = doHightlight;
        this.color = color;
    }

    private Color generateColor() {
//        int[] palette = ColorLib.getCategoryPalette(12, .75f, .25f, colorNum, 1f);
//        int colorRGB = palette[colorNum++ % palette.length];
//        return new Color(colorRGB);
        Color c = COLORS[colorNum % COLORS.length];
        colorNum++;
        return c;
    }

    public boolean isDoHightlight() {
        return doHightlight;
    }

    public Color getColor() {
        return color;
    }
    
}
