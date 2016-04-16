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
package umich.ms.batmass.gui.core.components.chromatogram;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import umich.ms.batmass.gui.core.components.base.GraphicsPanel;

/**
 * This class provides a JPanel that can display a chromatogram.
 *
 * @author Lennart Martens
 * @author Harald Barsnes
 * @version $Id$
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ChromatogramPanel extends GraphicsPanel {

    /**
     * Color in which the chromatogram polyline is rendered. Defaults to gray.
     */
    private Color iChromatogramColor = Color.GRAY;
    /**
     * Color in which the data points and peaks are rendered. Defaults to black.
     */
    private Color iChromatogramPointColor = new Color(0, 0, 0, 0);

    /**
     * This constructor creates a ChromatogramPanel based on the passed parameters. This constructor assumes
     * chromatogram data rather than profile spectrum data. For profile spectrum data use the SpectrumPanel
     * class instead and set ProfileMode to 'true'.
     *
     * @param aXAxisData    double[] with all the X axis data.
     * @param aYAxisData    double[] with all the Y axis data.
     */
    public ChromatogramPanel(double[] aXAxisData, double[] aYAxisData) {
        this(aXAxisData, aYAxisData, null, null, null);
    }

    /**
     * This constructor creates a ChromatogramPanel based on the passed parameters. This constructor assumes
     * chromatogram data rather than profile spectrum data. For profile spectrum data use the SpectrumPanel
     * class instead and set ProfileMode to 'true'.
     *
     * @param aXAxisData    double[] with all the X axis data.
     * @param aYAxisData    double[] with all the Y axis data.
     * @param aXAxisLabel   String with the label for the x-axis
     *                      (can have a unit between brackets, if available) - can be 'null' for no label
     * @param aYAxisLabel   String with the label for the y-axis
     *                      (can have a unit between brackets, if available) - can be 'null' for no label
     */
    public ChromatogramPanel(double[] aXAxisData, double[] aYAxisData, String aXAxisLabel, String aYAxisLabel) {
        this(aXAxisData, aYAxisData, aXAxisLabel, aYAxisLabel, null);
    }

    /**
     * This constructor creates a ChromatogramPanel based on the passed parameters. This constructor assumes
     * chromatogram data rather than profile spectrum data. For profile spectrum data use the SpectrumPanel
     * class instead and set ProfileMode to 'true'.
     *
     * @param aXAxisData    double[] with all the X axis data.
     * @param aYAxisData    double[] with all the Y axis data.
     * @param aXAxisLabel   String with the label for the x-axis
     *                      (can have a unit between brackets, if available) - can be 'null' for no label
     * @param aYAxisLabel   String with the label for the y-axis
     *                      (can have a unit between brackets, if available) - can be 'null' for no label
     * @param aPointSize    Integer with the point size to use
     */
    public ChromatogramPanel(double[] aXAxisData, double[] aYAxisData, String aXAxisLabel, String aYAxisLabel, Integer aPointSize) {

        // if point size is given, update the point size, otherwise keep the default point size
        if (aPointSize != null) {
            this.setPointSize(aPointSize);
        }

        this.currentGraphicsPanelType = GraphicsPanelType.chromatogram;

        dataSetCounter = 0;
        initData(aXAxisData, aYAxisData, aXAxisLabel, aYAxisLabel);
        this.iSpecPanelListeners = new ArrayList();
        this.addListeners();
    }

    /**
     * This method wraps all the shared logic of the various constructors.
     *
     * @param aXAxisData    double[] with all the X axis data.
     * @param aYAxisData    double[] with all the Y axis data.
     * @param aXAxisLabel   String with the label for the x-axis
     *                      (can have a unit between brackets, if available) - can be 'null' for no label
     * @param aYAxisLabel   String with the label for the y-axis
     *                      (can have a unit between brackets, if available) - can be 'null' for no label
     */
    private void initData(double[] aXAxisData, double[] aYAxisData, String aXAxisLabel, String aYAxisLabel) {
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.setBackground(Color.WHITE);
        processXAndYData(aXAxisData, aYAxisData, iChromatogramColor, iChromatogramPointColor);
        this.iXAxisLabel = (aXAxisLabel == null ? "unknown" : aXAxisLabel);
        this.iYAxisLabel = (aYAxisLabel == null ? "unknown" : aYAxisLabel);
    }

    /**
     * Adds an additional chromatogram dataset to be displayed in the same Chromatogram 
     * Panel. Remember to use different colors for the different datasets.
     *
     * @param aXAxisData            double[] with all the x-axis values.
     * @param aYAxisData            double[] with all the y-axis values
     * @param dataPointAndLineColor the color to use for the data points and lines
     * @param areaUnderCurveColor   the color to use for the area under the curve
     */
    public void addAdditionalDataset(double[] aXAxisData, double[] aYAxisData, Color dataPointAndLineColor, Color areaUnderCurveColor) {

        processXAndYData(aXAxisData, aYAxisData, dataPointAndLineColor, areaUnderCurveColor);

        this.showFileName = false;
        this.showPrecursorDetails = false;
        this.showResolution = false;
    }

    /**
     * This method allows the caller to set the point size for the
     * chromatogram. b>Note</b> that this number needs to be even, so
     * any uneven number will be replaced by the closest, lower, even
     * integer (e.g., 5 becomes 4, 13 becomes 12).
     *
     * @param aPointSize int with the point size, that will be reduced
     *                   to the closest, lower even integer  (e.g.,
     *                   5 becomes 4, 13 becomes 12).
     */
    public void setPointSize(Integer aPointSize) {
        if (aPointSize % 2 != 0) {
            aPointSize--;
        }
        iPointSize = aPointSize;
    }
}
