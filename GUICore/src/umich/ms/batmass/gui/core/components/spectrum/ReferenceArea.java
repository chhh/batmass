/* 
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
package umich.ms.batmass.gui.core.components.spectrum;

import java.awt.Color;

/**
 * A reference area to be added to a GraphicsPanel.
 *
 * @author Harald Barsnes.
 */
public class ReferenceArea {

    /**
     * The reference identifier, has to be unique.
     */
    private String identifier;
    /**
     * The reference label.
     */
    private String label;
    /**
     * The start of the reference area.
     */
    private double start;
    /**
     * The end of the reference area.
     */
    private double end;
    /**
     * The color of the reference area.
     */
    private Color areaColor;
    /**
     * The alpha level (transparency) of the reference area.
     */
    private float alpha;
    /**
     * If the area is to be drawn on top of or behind the data.
     */
    private boolean drawOnTop;
    /**
     * If true the label is drawn.
     */
    private boolean drawLabel;
    /**
     * The color to use for the label. Defaults to black.
     */
    private Color labelColor = Color.BLACK;
    /**
     * If true, bold font is used for the label.
     */
    private boolean boldFont = true;
    /**
     * The color to use for the border around the reference area. Defaults to
     * light gray.
     */
    private Color borderColor = Color.LIGHT_GRAY;
    /**
     * The width of the border around the reference area. Defaults to 0.1.
     */
    private float borderWidth = 0.2f;
    /**
     * The length of the reference area in percent. For x-axis areas this is the
     * height of the area from the x-axis upwards, while for y-axis area this is
     * the width of the area from the y-axis and to the right. Range: [0 - 100].
     */
    private double percentLength = 1;

    /**
     * Creates a new ReferenceArea.
     *
     * @param identifier the reference identifier, has to be unique
     * @param label the reference label, i.e., what is shown on the screen
     * @param start the start of the reference area
     * @param end the end of the reference area
     * @param areaColor the color of the reference area
     * @param alpha the alpha level (transparency) of the reference area
     * @param drawOnTop if the area is to be drawn on top of or behind the data
     * @param drawLabel if the label is to be drawn or not
     * @throws IllegalArgumentException alpha must be in the range 0.0f to 1.0f
     */
    public ReferenceArea(String identifier, String label, double start, double end, Color areaColor, float alpha, boolean drawOnTop, boolean drawLabel) throws IllegalArgumentException {

        this.identifier = identifier;
        this.label = label;
        this.start = start;
        this.end = end;
        this.areaColor = areaColor;
        this.drawOnTop = drawOnTop;
        this.drawLabel = drawLabel;

        // check the validity of alpha
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("The alpha transparency must be in the range 0.0f to 1.0f!");
        } else {
            this.alpha = alpha;
        }
    }

    /**
     * Creates a new ReferenceArea.
     *
     * @param identifier the reference identifier, has to be unique
     * @param label the reference label, i.e., what is shown on the screen
     * @param start the start of the reference area
     * @param end the end of the reference area
     * @param areaColor the color of the reference area
     * @param alpha the alpha level (transparency) of the reference area
     * @param drawOnTop if the area is to be drawn on top of or behind the data
     * @param drawLabel if the label is to be drawn or not
     * @param labelColor the color to use for the label
     * @param boldFont if the label is to be in bold font
     * @param borderColor the border color
     * @param borderWidth the border width
     * @param percentLength the length in percent, [0.0 - 1.0].
     * @throws IllegalArgumentException alpha must be in the range 0.0f to 1.0f
     */
    public ReferenceArea(String identifier, String label, double start, double end, Color areaColor, float alpha, boolean drawOnTop, boolean drawLabel,
            Color labelColor, boolean boldFont, Color borderColor, float borderWidth, double percentLength) throws IllegalArgumentException {

        this.identifier = identifier;
        this.label = label;
        this.start = start;
        this.end = end;
        this.areaColor = areaColor;
        this.drawOnTop = drawOnTop;
        this.drawLabel = drawLabel;
        this.labelColor = labelColor;
        this.boldFont = boldFont;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.percentLength = percentLength;

        // check the validity of alpha
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("The alpha transparency has to be in the range 0.0f to 1.0f!");
        } else {
            this.alpha = alpha;
        }

        // check the validity of percent length
        if (percentLength < 0 || percentLength > 1) {
            throw new IllegalArgumentException("The percent length has to be in the range [0 - 100]!");
        }
    }

    /**
     * Returns the label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     *
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the start value.
     *
     * @return the start
     */
    public double getStart() {
        return start;
    }

    /**
     * Set the start value.
     *
     * @param start the start to set
     */
    public void setStart(double start) {
        this.start = start;
    }

    /**
     * Get the end value.
     *
     * @return the end
     */
    public double getEnd() {
        return end;
    }

    /**
     * Set the end value.
     *
     * @param end the end to set
     */
    public void setEnd(double end) {
        this.end = end;
    }

    /**
     * Get the area color.
     *
     * @return the areaColor
     */
    public Color getAreaColor() {
        return areaColor;
    }

    /**
     * Set the area color.
     *
     * @param areaColor the areaColor to set
     */
    public void setAreaColor(Color areaColor) {
        this.areaColor = areaColor;
    }

    /**
     * Get the alpha level (transparency).
     *
     * @return the alpha level
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Set the alpha level (transparency).
     *
     * @param alpha the alpha level to set
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * Returns true if the area is to be drawn in front of the data, false
     * otherwise.
     *
     * @return true if the area is to be drawn in front of the data, false
     * otherwise
     */
    public boolean drawOnTop() {
        return drawOnTop;
    }

    /**
     * Set to true if the area is to be drawn in front of the data, false
     * otherwise.
     *
     * @param drawOnTop if the area is to be drawn in front of the data
     */
    public void setDrawOnTop(boolean drawOnTop) {
        this.drawOnTop = drawOnTop;
    }

    /**
     * Returns true if the label is to be drawn, false otherwise.
     *
     * @return true if the label is to be drawn, false otherwise
     */
    public boolean drawLabel() {
        return drawLabel;
    }

    /**
     * Set to true if the label is to be drawn, false otherwise.
     *
     * @param drawLabel if the label is to be drawn
     */
    public void setDrawLabel(boolean drawLabel) {
        this.drawLabel = drawLabel;
    }

    /**
     * Returns the label color.
     *
     * @return the labelColor
     */
    public Color getLabelColor() {
        return labelColor;
    }

    /**
     * Set the label color.
     *
     * @param labelColor the labelColor to set
     */
    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
    }

    /**
     * Returns true of the label should be in bold.
     *
     * @return the boldFont
     */
    public boolean useBoldFont() {
        return boldFont;
    }

    /**
     * Set if the label is to be in bold.
     *
     * @param boldFont the boldFont to set
     */
    public void setBoldFont(boolean boldFont) {
        this.boldFont = boldFont;
    }

    /**
     * Returns the border color.
     *
     * @return the borderColor
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Set the border color.
     *
     * @param borderColor the borderColor to set
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Returns the border width.
     *
     * @return the borderWidth
     */
    public float getBorderWidth() {
        return borderWidth;
    }

    /**
     * Set the border width.
     *
     * @param borderWidth the borderWidth to set
     */
    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * Returns the length of the reference area in percent. For x-axis areas
     * this is the height of the area from the x-axis upwards, while for y-axis
     * area this is the width of the area from the y-axis and to the right.
     * Range: [0.0 - 1.0].
     *
     * @return the percentLength
     */
    public double getPercentLength() {
        return percentLength;
    }

    /**
     * Set the length of the reference area in percent. For x-axis areas this is
     * the height of the area from the x-axis upwards, while for y-axis area
     * this is the width of the area from the y-axis and to the right. Range:
     * [0.0 - 1.0].
     *
     * @param percentLength the percentLength to set
     */
    public void setPercentLength(double percentLength) {
        this.percentLength = percentLength;
    }

    /**
     * Returns the reference identifier.
     * 
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the references identifier. Has to be unique.
     * 
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
