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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import umich.ms.batmass.gui.core.components.base.GraphicsPanel;
import umich.ms.batmass.gui.core.components.util.interfaces.SpectrumFile;


/*
 * CVS information:
 *
 * $Revision: 1.9 $ $Date: 2009/08/17 15:15:28 $
 */
/**
 * This file was originally a part of compomics-utilities: https://github.com/compomics/compomics-utilities.
 * The file might have been modified compared to the orignal. 
 * 
 * This class presents a JPanel that will hold and display a mass spectrum in
 * centroid or profile mode.
 *
 * @author Lennart Martens
 * @author Harald Barsnes
 * @version $Id: SpectrumPanel.java,v 1.9 2009/08/17 15:15:28 lennart Exp $
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SpectrumPanel extends GraphicsPanel {
    /**
     * The color used for the peaks. Default to red.
     */
    private Color spectrumPeakColor = Color.RED;
    /**
     * The color used for the profile mode spectra. Defaults to pink.
     */
    private Color spectrumProfileModeLineColor = Color.PINK;
    
    /**
     * This constructor creates a SpectrumPanel based on the spectrum
     * information in the specified SpectrumFile as an interactive lines plot.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     */
    public SpectrumPanel(SpectrumFile aSpecFile) {
        this(aSpecFile, DrawingStyle.LINES, true);
    }

    /**
     * This constructor creates a SpectrumPanel based on the spectrum
     * information in the specified SpectrumFile as a line plot.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     * @param aEnableInteraction boolean that specifies whether user-derived
     * events should be caught and dealt with.
     */
    public SpectrumPanel(SpectrumFile aSpecFile, boolean aEnableInteraction) {
        this(aSpecFile, DrawingStyle.LINES, aEnableInteraction);
    }

    /**
     * This constructor creates a SpectrumPanel based on the spectrum
     * information in the specified SpectrumFile with the specified drawing
     * style.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     * @param aDrawStyle the drawing style to use.
     * @param aEnableInteraction boolean that specifies whether user-derived
     * events should be caught and dealt with.
     */
    public SpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction) {
        this(aSpecFile, aDrawStyle, aEnableInteraction, null);
    }

    /**
     * This constructor creates a SpectrumPanel based on the spectrum
     * information in the specified SpectrumFile with the specified drawing
     * style.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     * @param aDrawStyle the drawing style to use.
     * @param aEnableInteraction boolean that specifies whether user-derived
     * events should be caught and dealt with.
     * @param aSpectrumFilenameColor Color with the color for the
     * spectrumfilename on the panel can be 'null' for default coloring.
     */
    public SpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor) {
        this(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor, 50, false, true, true);
    }

    /**
     * This constructor creates a SpectrumPanel based on the spectrum
     * information in the specified SpectrumFile with the specified drawing
     * style.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     * @param aDrawStyle the drawing style to use.
     * @param aEnableInteraction boolean that specifies whether user-derived
     * events should be caught and dealt with.
     * @param aSpectrumFilenameColor Color with the color for the
     * spectrumfilename on the panel can be 'null' for default coloring.
     * @param aMaxPadding int the sets the maximum padding size.
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel
     */
    public SpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor,
            int aMaxPadding, boolean aShowFileName) {
        this(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor, aMaxPadding, aShowFileName, true, true);
    }

    /**
     * This constructor creates a SpectrumPanel based on the spectrum
     * information in the specified SpectrumFile with the specified drawing
     * style.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     * @param aDrawStyle the drawing style to use.
     * @param aEnableInteraction boolean that specifies whether user-derived
     * events should be caught and dealt with.
     * @param aSpectrumFilenameColor Color with the color for the
     * spectrumfilename on the panel can be 'null' for default coloring.
     * @param aMaxPadding int the sets the maximum padding size.
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel
     * @param aShowPrecursorDetails boolean that specifies if the precursor
     * details should be shown in the panel
     * @param aShowResolution boolean that specifies if the resolution should be
     * shown in the panel
     */
    public SpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor,
            int aMaxPadding, boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution) {
        this(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor, aMaxPadding, aShowFileName, aShowPrecursorDetails, aShowResolution, 0);
    }

    /**
     * This constructor creates a SpectrumPanel based on the spectrum
     * information in the specified SpectrumFile with the specified drawing
     * style.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     * @param aDrawStyle the drawing style to use.
     * @param aEnableInteraction boolean that specifies whether user-derived
     * events should be caught and dealt with.
     * @param aSpectrumFilenameColor Color with the color for the
     * spectrumfilename on the panel can be 'null' for default coloring.
     * @param aMaxPadding int the sets the maximum padding size.
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel
     * @param aShowPrecursorDetails boolean that specifies if the precursor
     * details should be shown in the panel
     * @param aShowResolution boolean that specifies if the resolution should be
     * shown in the panel
     * @param aMSLevel int with the ms level for the spectrum
     */
    public SpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction, Color aSpectrumFilenameColor,
            int aMaxPadding, boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution, int aMSLevel) {
        this(aSpecFile, aDrawStyle, aEnableInteraction, aSpectrumFilenameColor, aMaxPadding, aShowFileName, aShowPrecursorDetails, aShowResolution, aMSLevel, false);
    }

    /**
     * This constructor creates a SpectrumPanel based on the spectrum
     * information in the specified SpectrumFile with the specified drawing
     * style.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     * @param aDrawStyle the drawing style to use.
     * @param aEnableInteraction boolean that specifies whether user-derived
     * events should be caught and dealt with.
     * @param aSpectrumFilenameColor Color with the color for the
     * spectrumfilename on the panel can be 'null' for default coloring.
     * @param aMaxPadding int the sets the maximum padding size.
     *
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel
     * @param aShowPrecursorDetails boolean that specifies if the precursor
     * details should be shown in the panel
     * @param aShowResolution boolean that specifies if the resolution should be
     * shown in the panel
     * @param aMSLevel int with the ms level for the spectrum, set to 0 if ms
     * level is unknown
     * @param aProfileMode boolean if set to true the spectrum will be drawn in
     * profile mode
     */
    public SpectrumPanel(SpectrumFile aSpecFile, DrawingStyle aDrawStyle, boolean aEnableInteraction,
            Color aSpectrumFilenameColor, int aMaxPadding,
            boolean aShowFileName, boolean aShowPrecursorDetails, boolean aShowResolution,
            int aMSLevel, boolean aProfileMode) {
        this.iCurrentDrawStyle = aDrawStyle;
        this.iSpecPanelListeners = new ArrayList();
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.setBackground(Color.WHITE);
        if (aSpecFile != null) {
            dataSetCounter = 0;
            this.processSpectrumFile(aSpecFile, spectrumPeakColor, spectrumProfileModeLineColor);
        }
        if (aEnableInteraction) {
            this.addListeners();
        }
        this.iFilenameColor = aSpectrumFilenameColor;
        this.maxPadding = aMaxPadding;
        this.showFileName = aShowFileName;
        this.showPrecursorDetails = aShowPrecursorDetails;
        this.showResolution = aShowResolution;
        this.iMSLevel = aMSLevel;

        if (aProfileMode) {
            this.currentGraphicsPanelType = GraphicsPanelType.profileSpectrum;
        } else {
            this.currentGraphicsPanelType = GraphicsPanelType.centroidSpectrum;
        }
    }

    /**
     * This constructor creates a SpectrumPanel based on the passed parameters.
     * This constructor will be used to annotate matched ions on the spectrum
     * panels.
     *
     * @param aXAxisData double[] with all the x-axis values.
     * @param aYAxisData double[] with all the y-axis values.
     * @param aPrecursorMZ double with the precursor mass.
     * @param aPrecursorCharge String with the precursor intensity.
     * @param aFileName String with the title of the Query.
     */
    public SpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName) {
        this(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, 50, false, true, true);
    }

    /**
     * This constructor creates a SpectrumPanel based on the passed parameters.
     * This constructor will be used to annotate matched ions on the spectrum
     * panels.
     *
     * @param aXAxisData double[] with all the x-axis values.
     * @param aYAxisData double[] with all the y-axis values.
     * @param aPrecursorMZ double with the precursor mass.
     * @param aPrecursorCharge String with the precursor intensity.
     * @param aFileName String with the title of the Query.
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel.
     */
    public SpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName,
            boolean aShowFileName) {
        this(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, 50, aShowFileName, true, true);
    }

    /**
     * This constructor creates a SpectrumPanel based on the passed parameters.
     * This constructor will be used to annotate matched ions on the spectrum
     * panels.
     *
     * @param aXAxisData double[] with all the x-axis values.
     * @param aYAxisData double[] with all the y-axis values.
     * @param aPrecursorMZ double with the precursor mass.
     * @param aPrecursorCharge String with the precursor intensity.
     * @param aFileName String with the title of the Query.
     * @param aMaxPadding int the sets the maximum padding size.
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel.
     */
    public SpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge,
            String aFileName, int aMaxPadding, boolean aShowFileName) {
        this(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, aMaxPadding, aShowFileName, true, true);
    }

    /**
     * This constructor creates a SpectrumPanel based on the passed parameters.
     * This constructor will be used to annotate matched ions on the spectrum
     * panels.
     *
     * @param aXAxisData double[] with all the x-axis values.
     * @param aYAxisData double[] with all the y-axis values.
     * @param aPrecursorMZ double with the precursor mass.
     * @param aPrecursorCharge String with the precursor intensity.
     * @param aFileName String with the title of the Query.
     * @param aMaxPadding int the sets the maximum padding size.
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel
     * @param aShowPrecursorDetails boolean that specifies if the precursor
     * details should be shown in the panel
     * @param aShowResolution boolean that specifies if the resolution should be
     * shown in the panel
     */
    public SpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge,
            String aFileName, int aMaxPadding, boolean aShowFileName,
            boolean aShowPrecursorDetails, boolean aShowResolution) {
        this(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, aMaxPadding,
                aShowFileName, aShowPrecursorDetails, aShowResolution, 0);
    }

    /**
     * This constructor creates a SpectrumPanel based on the passed parameters.
     * This constructor will be used to annotate matched ions on the spectrum
     * panels.
     *
     * @param aXAxisData double[] with all the x-axis values.
     * @param aYAxisData double[] with all the y-axis values.
     * @param aPrecursorMZ double with the precursor mass.
     * @param aPrecursorCharge String with the precursor intensity.
     * @param aFileName String with the title of the Query.
     * @param aMaxPadding int the sets the maximum padding size.
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel
     * @param aShowPrecursorDetails boolean that specifies if the precursor
     * details should be shown in the panel
     * @param aShowResolution boolean that specifies if the resolution should be
     * shown in the panel
     * @param aMSLevel int with the ms level for the spectrum, set to 0 if ms
     * level is unknown
     */
    public SpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge,
            String aFileName, int aMaxPadding, boolean aShowFileName,
            boolean aShowPrecursorDetails, boolean aShowResolution, int aMSLevel) {
        this(aXAxisData, aYAxisData, aPrecursorMZ, aPrecursorCharge, aFileName, aMaxPadding,
                aShowFileName, aShowPrecursorDetails, aShowResolution, aMSLevel, false);
    }

    /**
     * This constructor creates a SpectrumPanel based on the passed parameters.
     * This constructor will be used to annotate matched ions on the spectrum
     * panels.
     *
     * @param aXAxisData double[] with all the x-axis values.
     * @param aYAxisData double[] with all the y-axis values.
     * @param aPrecursorMZ double with the precursor mass.
     * @param aPrecursorCharge String with the precursor charge.
     * @param aFileName String with the title of the Query.
     * @param aMaxPadding int the sets the maximum padding size.
     * @param aShowFileName boolean that specifies if the file name should be
     * shown in the panel
     * @param aShowPrecursorDetails boolean that specifies if the precursor
     * details should be shown in the panel
     * @param aShowResolution boolean that specifies if the resolution should be
     * shown in the panel
     * @param aMSLevel int with the ms level for the spectrum, set to 0 if ms
     * level is unknown
     * @param aProfileMode boolean if set to true the spectrum will be drawn in
     * profile mode
     */
    public SpectrumPanel(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge,
            String aFileName, int aMaxPadding, boolean aShowFileName,
            boolean aShowPrecursorDetails, boolean aShowResolution, int aMSLevel,
            boolean aProfileMode) {
        this.iCurrentDrawStyle = DrawingStyle.LINES;
        this.iSpecPanelListeners = new ArrayList();
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.setBackground(Color.WHITE);
        dataSetCounter = 0;
        processXAndYData(aXAxisData, aYAxisData, spectrumPeakColor, spectrumProfileModeLineColor);
        iPrecursorMZ = aPrecursorMZ;
        iPrecursorCharge = aPrecursorCharge;
        iFilename = aFileName;
        this.maxPadding = aMaxPadding;
        this.showFileName = aShowFileName;
        this.showPrecursorDetails = aShowPrecursorDetails;
        this.showResolution = aShowResolution;
        this.iMSLevel = aMSLevel;

        if (aProfileMode) {
            this.currentGraphicsPanelType = GraphicsPanelType.profileSpectrum;
        } else {
            this.currentGraphicsPanelType = GraphicsPanelType.centroidSpectrum;
        }

        this.addListeners();
    }

    /**
     * Add a mirrored spectrum (or chromatogram).
     *
     * @param aXAxisData
     * @param aYAxisData
     * @param aPrecursorMZ
     * @param aPrecursorCharge
     * @param aFileName
     * @param aProfileMode
     * @param aSpectrumPeakColor
     * @param aSpectrumProfileModeLineColor
     */
    public void addMirroredSpectrum(double[] aXAxisData, double[] aYAxisData, double aPrecursorMZ, String aPrecursorCharge, String aFileName, boolean aProfileMode,
            Color aSpectrumPeakColor, Color aSpectrumProfileModeLineColor) {

        iPrecursorMZMirroredSpectrum = aPrecursorMZ;
        iPrecursorChargeMirorredSpectrum = aPrecursorCharge;
        iFilenameMirrorredSpectrum = aFileName;

        processMirroredXAndYData(aXAxisData, aYAxisData, aSpectrumPeakColor, aSpectrumProfileModeLineColor);

        if (aProfileMode) {
            this.currentGraphicsPanelType = GraphicsPanelType.profileSpectrum;
        } else {
            this.currentGraphicsPanelType = GraphicsPanelType.centroidSpectrum;
        }

        this.showFileName = false;
        this.showPrecursorDetails = false;
        this.showResolution = false;
        this.yAxisZoomExcludesBackgroundPeaks = false;
        this.yDataIsPositive = false;
    }

    /**
     * Adds an additional spectrum dataset to be displayed in the same Spectrum
     * Panel. Remember to use different colors for the different datasets.
     *
     * @param aXAxisData double[] with all the x-axis values.
     * @param aYAxisData double[] with all the y-axis values
     * @param dataPointAndLineColor the color to use for the data points and
     * lines
     * @param areaUnderCurveColor the color to use for the area under the curve
     */
    public void addAdditionalDataset(double[] aXAxisData, double[] aYAxisData, Color dataPointAndLineColor, Color areaUnderCurveColor) {

        processXAndYData(aXAxisData, aYAxisData, dataPointAndLineColor, areaUnderCurveColor);

        this.showFileName = false;
        this.showPrecursorDetails = false;
        this.showResolution = false;
    }

    /**
     * Adds an additional mirrored spectrum dataset to be displayed in the same
     * Spectrum Panel. Remember to use different colors for the different
     * datasets.
     *
     * @param aXAxisData double[] with all the x-axis values.
     * @param aYAxisData double[] with all the y-axis values
     * @param dataPointAndLineColor the color to use for the data points and
     * lines
     * @param areaUnderCurveColor the color to use for the area under the curve
     */
    public void addAdditionalMirroredDataset(double[] aXAxisData, double[] aYAxisData, Color dataPointAndLineColor, Color areaUnderCurveColor) {

        processMirroredXAndYData(aXAxisData, aYAxisData, dataPointAndLineColor, areaUnderCurveColor);

        this.showFileName = false;
        this.showPrecursorDetails = false;
        this.showResolution = false;
    }

    /**
     * Change the drawing type of the spectrum. Profile or centroid mode.
     *
     * @param aProfileMode if true, the spectrum is drawn in profile mode
     */
    public void setProfileMode(boolean aProfileMode) {
        if (aProfileMode) {
            this.currentGraphicsPanelType = GraphicsPanelType.profileSpectrum;
        } else {
            this.currentGraphicsPanelType = GraphicsPanelType.centroidSpectrum;
        }
    }

    /**
     * Set the default spectrum peak color. (Note that this only has an impact
     * on the first spectrum added. For additional spectra or mirrored spectra
     * set the color in the given constructor.)
     *
     * @param aSpectrumPeakColor the color to set
     */
    public void setSpectrumPeakColor(Color aSpectrumPeakColor) {
        this.spectrumPeakColor = aSpectrumPeakColor;
    }

    /**
     * Set the default spectrum profile mode color. (Note that this only has an
     * impact on the first spectrum added. For additional spectra or mirrored
     * spectra set the color in the given constructor.)
     *
     * @param aSpectrumProfileModeLineColor the color to set
     */
    public void setSpectrumProfileModeLineColor(Color aSpectrumProfileModeLineColor) {
        this.spectrumProfileModeLineColor = aSpectrumProfileModeLineColor;
    }

    /**
     * If true only the annotated peaks will be drawn. The default value is
     * false, and result in all peaks being drawn. Note that this setting is
     * ignored when in profile mode!
     *
     * @param aAnnotatedPeaks if true only the annotated peaks will be drawn
     */
    public void showAnnotatedPeaksOnly(boolean aAnnotatedPeaks) {
        this.showAllPeaks = !aAnnotatedPeaks;
    }

    /**
     * This method initializes a SpectrumPanel based on the spectrum information
     * in the specified SpectrumFile.
     *
     * @param aSpecFile SpectrumFile with the information about masses and
     * intensities that will be copied here. Note that mass-sorting will take
     * place in this step as well.
     */
    public void setSpectrumFile(SpectrumFile aSpecFile) {
        this.processSpectrumFile(aSpecFile, spectrumPeakColor, spectrumProfileModeLineColor);
    }

    /**
     * This method reads the peaks and their intensities from the specified
     * SpectrumFile and stores these internally for drawing. The masses are
     * sorted in this step.
     *
     * @param aSpecFile SpectrumFile from which the peaks and intensities will
     * be copied.
     * @param dataPointAndLineColor the color to use for the data points and
     * line
     * @param areaUnderCurveColor the color to use for the area under the curve
     */
    private void processSpectrumFile(SpectrumFile aSpecFile, Color dataPointAndLineColor, Color areaUnderCurveColor) {

        if (dataSetCounter == 0) {
            iXAxisData = new ArrayList<double[]>();
            iYAxisData = new ArrayList<double[]>();
        }

        iDataPointAndLineColor.add(dataPointAndLineColor);
        iAreaUnderCurveColor.add(areaUnderCurveColor);

        HashMap peaks = aSpecFile.getPeaks();

        iXAxisData.add(new double[peaks.size()]);
        iYAxisData.add(new double[peaks.size()]);

        iFilename = aSpecFile.getFilename();

        // Maximum intensity of the peaks.
        double maxInt = 0.0;

        // TreeSets are sorted.
        TreeSet masses = new TreeSet(peaks.keySet());
        Iterator iter = masses.iterator();

        int count = 0;

        while (iter.hasNext()) {
            Double key = (Double) iter.next();
            double mass = key.doubleValue();
            double intensity = ((Double) peaks.get(key)).doubleValue();
            if (intensity > maxInt) {
                maxInt = intensity;
            }
            iXAxisData.get(dataSetCounter)[count] = mass;
            iYAxisData.get(dataSetCounter)[count] = intensity;
            count++;
        }

        if (iXAxisStartAtZero) {
            this.rescale(0.0, getMaxXAxisValue());
        } else {
            this.rescale(getMinXAxisValue(), getMaxXAxisValue());
        }

        this.iPrecursorMZ = aSpecFile.getPrecursorMZ();
        int liTemp = aSpecFile.getCharge();

        if (liTemp == 0) {
            iPrecursorCharge = "?";
        } else {
            iPrecursorCharge = Integer.toString(liTemp);
            iPrecursorCharge += (liTemp > 0 ? "+" : "-");
        }

        dataSetCounter++;
    }
}
