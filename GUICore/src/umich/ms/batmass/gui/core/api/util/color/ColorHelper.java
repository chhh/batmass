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
package umich.ms.batmass.gui.core.api.util.color;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class ColorHelper {

    private ColorHelper() {
    }


    public final static String toHexString(Color colour) throws NullPointerException {
        String hexColour = Integer.toHexString(colour.getRGB() & 0xffffff);
        if (hexColour.length() < 6) {
            hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
        }
        return "#" + hexColour;
    }

     /**
     * Computes several visually distinct colors such that they are equally
     * spaced apart in the HSB color space.
     *
     * @param k how many colors to get
     * @return a list of colors
     */
    public static List<Color> getDistinctColors(int k)
    {
        return getDistinctColors(k, 0.95f, 0.7f);
    }

    /**
     * Computes several visually distinct colors such that they are equally
     * spaced apart in the HSB color space.
     *
     * @param k how many colors to get
     * @param saturation the saturation level for all the colors
     * @param brightness the brightness for all the colors
     * @return a list of colors
     */
    public static List<Color> getDistinctColors(int k, float saturation, float brightness)
    {
        ArrayList<Color> categoryColors = new ArrayList<>(k);
        float colorFactor = 1.0f / k;
        for (int i = 0; i < k; i++)
        {
            Color c = Color.getHSBColor(i * colorFactor, saturation, brightness);
            categoryColors.add(c);
        }

        return categoryColors;
    }
}
