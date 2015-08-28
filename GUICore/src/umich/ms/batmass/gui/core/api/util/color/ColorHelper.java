/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api.util.color;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dmitriya
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
