/*
 * License placeholder
 */

package umich.ms.batmass.gui.core.api.util.color;

import java.util.Arrays;


/**
 * A color map provides a mapping from numeric values to specific colors.
 * This useful for assigning colors to visualized items. The numeric values
 * may represent different categories (i.e. nominal variables) or run along
 * a spectrum of values (i.e. quantitative variables).<br/>
 * To get the palette to set, use methods from {@link ColorLib}
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 *
 * Borrowed from https://github.com/prefuse/Prefuse

 * @author dmitriya
 */
public class ColorMap {

    private int[] palette;
    private double valLo, valHi, valSpan;
    private int lastIdx;

    /**
     * Creates a new ColorMap instance using the given internal color map
     * array and minimum and maximum index values.
     * @param map the color palette, an int array of color values
     * @param min the minimum value in the color map
     * @param max the maximum value in the color map
     */
    public ColorMap(int[] palette, double min, double max) {
        if (max <= min)
            throw new IllegalArgumentException("Max must be strictly larger than min value.");
        this.palette = palette;
        lastIdx = palette.length - 1;
        valLo = min;
        valHi = max;
        valSpan = valHi - valLo;


    }

    /**
     * Returns the color associated with the given value. If the value
     * is outside the range defined by this map's minimum or maximum
     * values, a endpoint value is returned (i.e. the first entry
     * in the color map for values below the minimum, the last enty
     * for value above the maximum).
     * @param val the value for which to retrieve the color
     * @return the color corresponding the given value
     */
    public int getColor(double val) {
        int idx = (int)(palette.length * (val-valLo)/valSpan);
        if ( idx < 0 ) {
            return palette[0];
        } else if ( idx >= lastIdx ) {
            return palette[lastIdx];
        }
        return palette[idx];
    }

    /**
     * Gets the internal color palette, an int array of color values.
     * @return returns the color palette.
     */
    public int[] getColorPalette() {
        return palette;
    }

    /**
     * Sets the internal color palette, an int array of color values.
     * @param palette the new palette.
     */
    public void setColorPalette(int[] palette) {
        this.palette = palette;
        lastIdx = palette.length - 1;
    }

    /**
     * Gets the maximum value that corresponds to the last
     * color in the color map.
     * @return returns the max index value into the color map.
     */
    public double getMaxValue() {
        return valHi;
    }

    /**
     * Sets the maximum value that corresponds to the last
     * color in the color map.
     * @param maxValue the new max index value.
     */
    public void setMaxValue(double maxValue) {
        this.valHi = maxValue;
    }

    /**
     * Gets the minimum value that corresponds to the first
     * color in the color map.
     * @return Returns the min index value.
     */
    public double getMinValue() {
        return valLo;
    }

    /**
     * Sets the minimum value that corresponds to the first
     * color in the color map.
     * @param minValue the new min index value.
     */
    public void setMinValue(double minValue) {
        this.valLo = minValue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Arrays.hashCode(this.palette);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.valLo) ^ (Double.doubleToLongBits(this.valLo) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.valHi) ^ (Double.doubleToLongBits(this.valHi) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColorMap other = (ColorMap) obj;
        if (Double.doubleToLongBits(this.valLo) != Double.doubleToLongBits(other.valLo)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valHi) != Double.doubleToLongBits(other.valHi)) {
            return false;
        }
        if (!Arrays.equals(this.palette, other.palette)) {
            return false;
        }
        return true;
    }



} // end of class ColorMap
