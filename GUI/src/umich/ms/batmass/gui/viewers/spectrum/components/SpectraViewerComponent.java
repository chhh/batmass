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
package umich.ms.batmass.gui.viewers.spectrum.components;

import MSUmpire.BaseDataStructure.XYData;
import MSUmpire.BaseDataStructure.XYPointCollection;
import MSUmpire.PeakDataStructure.PeakCluster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import net.engio.mbassy.listener.Handler;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import umich.ms.batmass.gui.core.api.BMComponentJPanel;
import umich.ms.batmass.gui.core.api.comm.dnd.DnDButton;
import umich.ms.batmass.gui.core.api.comm.eventbus.AbstractBusPubSub;
import umich.ms.batmass.gui.core.api.comm.eventbus.ViewerLinkSupport;
import umich.ms.batmass.gui.core.components.spectrum.SpectrumPanel;
import umich.ms.batmass.gui.core.components.util.gui.events.RescalingEvent;
import umich.ms.batmass.gui.core.components.util.gui.interfaces.SpectrumPanelListener;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DZoomLevel;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.viewers.map2d.messages.MsgZoom1D;
import umich.ms.batmass.gui.viewers.map2d.messages.MsgZoom2D;
import umich.ms.batmass.gui.viewers.spectrum.actions.GoToScanAction;
import umich.ms.batmass.gui.viewers.spectrum.actions.NextScanAction;
import umich.ms.batmass.gui.viewers.spectrum.actions.PrevScanAction;
import umich.ms.batmass.gui.viewers.spectrum.todelete.PeakClusterContainer;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scan.props.PrecursorInfo;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.datatypes.scancollection.ScanIndex;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.exceptions.FileParsingException;


/**
 *
 * @author dmitriya
 */
public class SpectraViewerComponent extends BMComponentJPanel {
    // message bus
    protected ViewerLinkSupport linkSupport;
    protected BusHandler busHandler;
    
    private final IScanCollection scans;
    private ArrayList<PeakClusterContainer> clusters;
    private final int scanNumFirst;
    private final int scanNumLast;

    private JComboBox<Integer> comboBoxMsLevelSelector;
    private JButton btnPrevScan;
    private JButton btnNextScan;
    private JButton btnGotoScan;
    private JCheckBox checkBoxSaveZoom;
    protected DnDButton btnLinkDnD;
    protected JButton btnUnlink;
    
    private JToolBar toolbar;

    private int msLevelMaxAllowed;
    private Viewport viewport;
    private SpectrumPanel spectrumPanel;
    private JPanel infoPanel;
    private boolean isZoomStoredBetweenSpectra = false;

    private static final boolean doSubtractionOfFeatures = false;
    private static final int toolbarBtnHSpacing = 3;

    
    /**
     * Creates new form SpectraViewerComponent
     * @param scans
     */
    public SpectraViewerComponent(IScanCollection scans) {
        ic.add(ic); // needed for D&D linking between viewers

        initComponents();
        

        this.scans = scans;
        scanNumFirst = scans.getMapNum2scan().firstKey();
        scanNumLast = scans.getMapNum2scan().lastKey();
        initViewer();
    }

    private void initViewer() {
        setLayout(new BorderLayout());

        // set the MS Level allowance to the maximum available in this Scan Collection
        Set<Integer> msLevelsSet = scans.getMapMsLevel2index().keySet();
        Integer[] msLevels = msLevelsSet.toArray(new Integer[msLevelsSet.size()]);
        Arrays.sort(msLevels);
        msLevelMaxAllowed = msLevels[msLevels.length-1];

        // we need the actions to create the toolbar, however it might all break
        // if someone tries to use an action before creatino of this component is
        // fullly finished
        populateActionInputMaps();
        ActionMap am = getActionMap();

        // Creating the toolbar
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        // make sure the toolbar can't get focus by itself
        toolbar.setFocusable(false);
        toolbar.setRollover(true);

        // MS Level selector
        comboBoxMsLevelSelector = new JComboBox<>(msLevels);
        comboBoxMsLevelSelector.setFocusable(false);
        comboBoxMsLevelSelector.setSelectedItem(msLevelMaxAllowed);
        comboBoxMsLevelSelector.setPrototypeDisplayValue(msLevels[msLevels.length-1]);
        comboBoxMsLevelSelector.setMaximumSize(comboBoxMsLevelSelector.getMinimumSize());
        comboBoxMsLevelSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Integer selectedMsLevel = (Integer)comboBoxMsLevelSelector.getSelectedItem();
                msLevelMaxAllowed = selectedMsLevel;
            }
        });
        JLabel lblMaxMsLvl = new JLabel("Max MS Level", SwingConstants.LEFT);
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        toolbar.add(lblMaxMsLvl);
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        toolbar.add(comboBoxMsLevelSelector);

        toolbar.addSeparator();

        // Previous scan button
        btnPrevScan = new JButton();
        btnPrevScan.setAction(am.get(PrevScanAction.getActionID()));
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        toolbar.add(btnPrevScan);



        // Next scan button
        btnNextScan = new JButton();
        btnNextScan.setAction(am.get(NextScanAction.getActionID()));
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        toolbar.add(btnNextScan);


        // GoTo scan button
        btnGotoScan = new JButton();
        btnGotoScan.setAction(am.get(GoToScanAction.getActionID()));
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        toolbar.add(btnGotoScan);


        toolbar.addSeparator();


        // Save zoom between scans checkbox
        checkBoxSaveZoom = new JCheckBox();
        checkBoxSaveZoom.setSelected(isZoomStoredBetweenSpectra);
        checkBoxSaveZoom.setText(NbBundle.getMessage(SpectraViewerComponent.class,
                "SpectraViewerComponent.cboxSaveZoom.text")); // NOI18N
        checkBoxSaveZoom.setToolTipText(NbBundle.getMessage(SpectraViewerComponent.class,
                "SpectraViewerComponent.cboxSaveZoom.tooltip")); // NOI18N
        checkBoxSaveZoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isZoomStoredBetweenSpectra = checkBoxSaveZoom.isSelected();
            }
        });
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        toolbar.add(checkBoxSaveZoom);
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));

        
        // DnD linking support
        busHandler = new BusHandler();
        linkSupport = new ViewerLinkSupport(
                Collections.singleton(this),        // highlight components
                Collections.singleton(getBusHandler()),  // subscribers (have @Handler methods to recieve messages)
                Collections.singleton(getBusHandler()),
                this
        );
        
        // Link button
        btnLinkDnD = linkSupport.getBtnLinkDnD();
        toolbar.add(btnLinkDnD);
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));


        // Unlink button
        
        btnUnlink = linkSupport.getBtnUnlink();
        toolbar.add(btnUnlink);
        toolbar.add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        

        // make sure no children of the toolbar can be focused
        for (int i=0; i < toolbar.getComponentCount(); i++) {
            Component comp = toolbar.getComponent(i);
            comp.setFocusable(false);
        }

        // adding the toolbar to the common JPanel
        this.add(toolbar, java.awt.BorderLayout.NORTH);


        // build the first spectrum panel
        int firstScanNum = scans.getMapNum2scan().firstKey();
        showSpectrum(firstScanNum);
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    public class BusHandler extends AbstractBusPubSub implements SpectrumPanelListener {
        private volatile boolean isRespondingToRecievedZoomEvent = false;

        @Handler
        public void eventbusHandleZoom2DEvent(MsgZoom2D m) {
            // we're also receiving our own events, which this Map published to the bus
            // need to filter those out
            if (m.getOrigin() != this) {
                Map2DZoomLevel zoomLvl = m.getZoomLvl();

                // check ms level first, compare to our max allowed ms level
                int msLevel = zoomLvl.getMsLevel();
                int msLevelMax = getMsLevelMaxAllowed();
                if (msLevel > msLevelMax) {
                    return;
                }

                MzRtRegion mapDims = zoomLvl.getAxes().getMapDimensions();
                double rtLo = mapDims.getRtLo();
                double rtHi = mapDims.getRtHi();
                double rtMid = (rtLo + rtHi) / 2;

                ScanIndex index = scans.getMapMsLevel2index().get(msLevel);
                Map.Entry<Double, List<IScan>> midEntry = index.getRt2scan().ceilingEntry(rtMid);
                List<IScan> scansMid = midEntry.getValue();
                if (scansMid.isEmpty()) {
                    throw new IllegalStateException("Spectrum viewer received MsgZoom2D, tried to find the central scan number"
                            + ", but got nothing when quarying ScanCollection");
                }
                
                Integer scanNumMid = scansMid.get(0).getNum();
                showSpectrum(scanNumMid);
                SpectraViewerComponent.this.zoom(mapDims.getMzLo(), mapDims.getMzHi());
            }
        }

        @Override
        public void rescaled(RescalingEvent e) {
            int scanNum = SpectraViewerComponent.this.viewport.scanNum;
            IScan scan = scans.getScanByNum(scanNum);
            MzRtRegion mzRtRegion = new MzRtRegion(e.getMinMass(), e.getMaxMass(), scan.getRt(), scan.getRt());
            this.publish(new MsgZoom1D(this, mzRtRegion, scan));
        }
    }

    private void populateActionInputMaps() {
        ActionMap am = this.getActionMap();
        am.put(PrevScanAction.getActionID(), new PrevScanAction(this));
        am.put(NextScanAction.getActionID(), new NextScanAction(this));
        am.put(GoToScanAction.getActionID(), new GoToScanAction(this));

        InputMap im = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(PrevScanAction.getAcceleratorKey(),  PrevScanAction.getActionID());
        im.put(NextScanAction.getAcceleratorKey(), NextScanAction.getActionID());
        im.put(GoToScanAction.getAcceleratorKey(),   GoToScanAction.getActionID());
    }

    public void setClusters(ArrayList<PeakClusterContainer> clusters) {
        this.clusters = clusters;
    }

    /**
     * Zoom the current panel to a specific m/z region.
     * Provided m/z values can be null, then the min/max m/z value for that
     * scan will be used.
     * @param mzLo if null, will be auto-replaced by the lowest m/z value in scan.
     * @param mzHi if null, will be auto-replaced by the highest m/z value in scan.
     */
    public void zoom(Double mzLo, Double mzHi) {
        if (spectrumPanel == null)
            // if the spectrum panel is not yet initialized, don't do anything
            return;
        if (mzLo == null) {
            mzLo = spectrumPanel.getXAxisZoomRangeLowerValue();
        }
        if (mzHi == null) {
            mzHi = spectrumPanel.getXAxisZoomRangeUpperValue();
        }
        spectrumPanel.rescale(mzLo, mzHi);
        viewport.setMzLo(mzLo);
        viewport.setMzHi(mzHi);
    }

    /**
     * Removes the old spectrum panel, adds the new one and handles setting up
     * {@link Viewport} (respecting parameters like {@link #isZoomStoredBetweenSpectra}).
     * Also handles enablement of actions.
     * @param sp the new spectrum panel
     * @param scanNum scan number for the new panel
     */
    public void showSpectrum(int scanNum) {

        SpectrumPanel sp = buildPanelForScanNum(scanNum);

        if (spectrumPanel == null) {
            // this is the case of initial panel creation
            sp.rescale(sp.getMinXAxisValue(), sp.getMaxXAxisValue());
            viewport = new Viewport(sp.getMinXAxisValue(), sp.getMaxXAxisValue(), scanNum);
        }

        // respect the default value of isZoomStoredBetweenSpectra
        if (isZoomStoredBetweenSpectra) {
            sp.rescale(viewport.mzLo, viewport.mzHi);
        } else {
            // this is needed because if ugly GraphicsPanel implementation
            sp.rescale(sp.getMinXAxisValue(), sp.getMaxXAxisValue());
        }
        
        // only update the viewport info after we've created the current view
        // and set it's zoom to match that of the previously displayed spectrum
        viewport.setScanNum(scanNum);
        viewport.setMzLo(sp.getXAxisZoomRangeLowerValue());
        viewport.setMzHi(sp.getXAxisZoomRangeUpperValue());


        sp.addSpectrumPanelListener(new SpectrumPanelListener() {
            @Override
            public void rescaled(RescalingEvent e) {
                viewport.setMzLo(e.getMinMass());
                viewport.setMzHi(e.getMaxMass());
            }
        });
        sp.addSpectrumPanelListener(busHandler);

        // handle actions enablement
        ActionMap am = getActionMap();
        Action prevAction = am.get(PrevScanAction.getActionID());
        Action nextAction = am.get(NextScanAction.getActionID());
        prevAction.setEnabled(true);
        nextAction.setEnabled(true);
        if (scanNumFirst == scanNum)
            prevAction.setEnabled(false);
        if (scanNumLast == scanNum)
            nextAction.setEnabled(false);


        // remove the old info panel and insert the new one
        if (infoPanel != null)
            this.remove(infoPanel);
        infoPanel = buildInfoPanelForScanNum(scanNum);
        this.add(infoPanel, BorderLayout.SOUTH);
        // remove the old spectrum panel and insert the new one
        if (spectrumPanel != null)
            this.remove(spectrumPanel);
        this.add(sp, BorderLayout.CENTER);
        spectrumPanel = sp;
        this.revalidate();
    }

    private JPanel buildInfoPanelForScanNum(int scanNum) {
        IScan scan = scans.getScanByNum(scanNum);
        if (scan == null) {
            throw new IllegalStateException(String.format("The scan number (%d) does not exist in ScanCollection", scanNum));
        }

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        StringBuilder sb = new StringBuilder();
        String polarityStr = "?";
        if (scan.getPolarity() != null) {
            polarityStr = scan.getPolarity().toString();
        }
//        String description = String.format("Scan: #%d(%.3fm), MS%d[%s] m/z:[%d-%d]",
//                scan.num, scan.rt, scan.msLevel, polarityStr,
//                (int)Math.round(scan.getScanMzWindowLower()),
//                (int)Math.round(scan.getScanMzWindowUpper()));
//        JLabel infoLabel = new JLabel(description, SwingConstants.LEFT);
//        panel.add(infoLabel);
        String desc1 = String.format("Scan: #%d(%.3fm), ", scan.getNum(), scan.getRt());
        String desc2;
        // that's the default value, if MS level is >1, then add precursor info
        desc2 = String.format("MS%d[%s],", scan.getMsLevel(), polarityStr);
        if (scan.getMsLevel() > 1 && scan.getPrecursor() != null) {
            Double mzRangeStart = scan.getPrecursor().getMzRangeStart();
            Double mzRangeEnd = scan.getPrecursor().getMzRangeEnd();
            if (Objects.equals(mzRangeEnd, mzRangeStart)) {
                desc2 = String.format("MS%d[%s](%.3f),", scan.getMsLevel(), polarityStr, mzRangeStart);
            } else {
                desc2 = String.format("MS%d[%s](%.3f-%.3f),", scan.getMsLevel(), polarityStr, mzRangeStart, mzRangeEnd);
            }
        }
        String desc3 = String.format("m/z:[%d-%d]",
                (int)Math.round(scan.getScanMzWindowLower()),
                (int)Math.round(scan.getScanMzWindowUpper()));
        panel.add(new JLabel(desc1, SwingConstants.LEFT));
        panel.add(new JLabel(desc2, SwingConstants.LEFT));
        panel.add(new JLabel(desc3, SwingConstants.LEFT));


        return panel;
    }

    private SpectrumPanel buildPanelForScanNum(int scanNum) {
        IScan scan = scans.getScanByNum(scanNum);
        if (scan == null) {
            throw new IllegalStateException(String.format("The scan number (%d) does not exist in ScanCollection", scanNum));
        }


        ISpectrum spectrum = null;
        try {
            spectrum = scan.fetchSpectrum();
        } catch (FileParsingException ex) {
            Exceptions.printStackTrace(ex);
        }

        double[] xValues = spectrum.getMZs();
        double[] yValues = spectrum.getIntensities();

        boolean profileMode = !scan.isCentroided();
        int msLevel = scan.getMsLevel();
        int maxPadding = 50;
        PrecursorInfo precursor = scan.getPrecursor();
        String charge = "-";
        double precursorMz = 0.0d;
        if (precursor != null) {
            if (precursor.getMzRangeStart() != null) {
                precursorMz = precursor.getMzRangeStart();
            } else if (precursor.getMzRangeEnd() != null) {
                precursorMz = precursor.getMzRangeEnd();
            }
        }

        SpectrumPanel newSpectrumPanel = new SpectrumPanel(
                xValues,
                yValues,
                precursorMz,
                charge,
                String.format("Scan #%d(%.2fm): MS%d m/z[%d-%d]", scan.getNum(), scan.getRt(), scan.getMsLevel(),
                    (int)Math.round(scan.getScanMzWindowLower()), (int)Math.round(scan.getScanMzWindowUpper())),
                maxPadding,
                false, // show filename
                false, false,
                msLevel,
                profileMode);
        newSpectrumPanel.setXAxisStartAtZero(false);


        // now the block handling feature drawing if those were set
        if (clusters == null)
            return newSpectrumPanel;
        List<PeakCluster> pcs = new ArrayList<>();
        for (PeakClusterContainer pcc : clusters) {
            if (scanNum >= pcc.startScanNum && scanNum <= pcc.endScanNum) {
                pcs.add(pcc.peakCluster);
            }
        }

        // This "if block" deals with subtracting features from original spectra
        // it's not so easy, because we don't have a direct correspondence of feature points
        // to points in spectra, so we have to guess a lot. Because of that
        // and because of interpolation from smoothed peak-curves we're not always
        // able to subtract the full original peak from the spectrum, even if
        // we guessed m/z correctly.
        if (doSubtractionOfFeatures && !pcs.isEmpty()) {
            for (int i = 0; i < pcs.size(); i++) {
                PeakCluster pc = pcs.get(i);
                for (int j = 0; j < pc.mz.length; j++) {
                    double pcMz = pc.mz[j];
                    if (pc.mz[j] <= 0.0f)
                        break;

                    float a, b;
                    XYData loXYPoint, hiXYPoint;
                    XYPointCollection smoothedList;

                    smoothedList = pc.IsoPeaksCurves[j].GetSmoothedList();
                    loXYPoint = smoothedList.GetPoinByXLower(scan.getRt().floatValue());
                    hiXYPoint = smoothedList.GetPoinByXHigher(scan.getRt().floatValue());


                    a = (loXYPoint.getY() - hiXYPoint.getY()) / (loXYPoint.getX() - hiXYPoint.getX());
                    b = loXYPoint.getY() - a * loXYPoint.getX();
                    double avgIntensityInterpolatedFromSmoothedData = a * scan.getRt().floatValue() + b;


                    int binarySearchResult = Arrays.binarySearch(xValues, pcMz);
                    if (binarySearchResult < 0) {
                        int insertionPoint = - 1 - binarySearchResult;
                        if (insertionPoint == 0) {
                            yValues[0] -= avgIntensityInterpolatedFromSmoothedData;
                            if (yValues[0] < 0d) yValues[0] = 0d;
                        } else if (insertionPoint >= yValues.length - 1) {
                            yValues[yValues.length - 1] -= avgIntensityInterpolatedFromSmoothedData;
                            if (yValues[yValues.length - 1] < 0d) yValues[yValues.length - 1] = 0d;
                        } else {
                            double dMzLeft = Math.abs(xValues[insertionPoint-1] - pcMz);
                            double dMzCenter = Math.abs(xValues[insertionPoint] - pcMz);
                            double dMzRight = Math.abs(xValues[insertionPoint+1] - pcMz);


                            double dIntLeft = Math.abs(yValues[insertionPoint-1] - avgIntensityInterpolatedFromSmoothedData);
                            double dIntCenter = Math.abs(yValues[insertionPoint] - avgIntensityInterpolatedFromSmoothedData);
                            double dIntRight = Math.abs(yValues[insertionPoint+1] - avgIntensityInterpolatedFromSmoothedData);
                            double min = Math.min(Math.min(dIntCenter, dIntLeft), dIntRight);

                            if (dIntLeft < dIntRight && dIntLeft < dIntCenter) {
                                yValues[insertionPoint-1] -= avgIntensityInterpolatedFromSmoothedData;
                                if (yValues[insertionPoint-1] < 0d) yValues[insertionPoint-1] = 0d;

                            } else if (dIntRight < dIntLeft && dIntRight < dIntCenter) {
                                yValues[insertionPoint+1] -= avgIntensityInterpolatedFromSmoothedData;
                                if (yValues[insertionPoint+1] < 0d) yValues[insertionPoint+1] = 0d;
                            } else {
                                yValues[insertionPoint] -= avgIntensityInterpolatedFromSmoothedData;
                                if (yValues[insertionPoint] < 0d) yValues[insertionPoint] = 0d;
                            }
                        }
                    } else {
                        yValues[binarySearchResult] -= avgIntensityInterpolatedFromSmoothedData;
                    }
                }
            }
        }
        // END: feature subtraction block

        // looking for peak clusters for this scan (just an array of clusters)
        // multiple colors for clusters
        Color color;
        double[] mirroredSpectrumMzArrPrev = new double[0];
        double avgIntensityInterpolatedFromSmoothedData;
        float a, b;
        XYData loXYPoint, hiXYPoint;
        XYPointCollection smoothedList;

        if (!pcs.isEmpty()) {
            for (int i = 0; i < pcs.size(); i++) {
                PeakCluster pc = pcs.get(i);
                ArrayList<Double> mirroredSpectrumMz = new ArrayList<>();
                ArrayList<Double> mirroredSpectrumInt = new ArrayList<>();

                for (int j = 0; j < pc.mz.length; j++) {
                    if (pc.mz[j] <= 0.0f) {
                        break;
                    }
                    smoothedList = pc.IsoPeaksCurves[j].GetSmoothedList();
                    loXYPoint = smoothedList.GetPoinByXLower(scan.getRt().floatValue());
                    hiXYPoint = smoothedList.GetPoinByXHigher(scan.getRt().floatValue());

                    a = (loXYPoint.getY() - hiXYPoint.getY()) / (loXYPoint.getX() - hiXYPoint.getX());
                    b = loXYPoint.getY() - a * loXYPoint.getX();
                    avgIntensityInterpolatedFromSmoothedData = a * scan.getRt().floatValue() + b;

                    mirroredSpectrumMz.add((double) pc.mz[j]);
                    // this was for plotting the original intensities instead of averaged from
                    // two nearest points
//                    mirroredSpectrumInt.add((double)pc.PeakHeight[j]);
                    mirroredSpectrumInt.add(avgIntensityInterpolatedFromSmoothedData);
                }

                double[] mirroredSpectrumMzArr = new double[mirroredSpectrumMz.size()];
                double[] mirroredSpectrumIntArr = new double[mirroredSpectrumInt.size()];
                for (int j = 0; j < mirroredSpectrumIntArr.length; j++) {
                    mirroredSpectrumMzArr[j] = mirroredSpectrumMz.get(j);
                    for (int k = 0; k < mirroredSpectrumMzArrPrev.length; k++) {
                        if (Math.abs(mirroredSpectrumMzArr[j] - mirroredSpectrumMzArrPrev[k]) < 0.01) {
                            mirroredSpectrumMzArr[j] += 0.01;
                            break;
                        }
                    }
                    mirroredSpectrumIntArr[j] = mirroredSpectrumInt.get(j);
                }
                mirroredSpectrumMzArrPrev = mirroredSpectrumMzArr;

                color = SpectrumPanelAndUmpireFeaturesAdapter.ColorWheel.getColorForInt(pc.Index);
                if (i == 0) {
                    newSpectrumPanel.addMirroredSpectrum(
                            mirroredSpectrumMzArr, mirroredSpectrumIntArr,
                            0.0,
                            "neutral",
                            "umpire features",
                            false,
                            color, color);
                } else {
                    newSpectrumPanel.addAdditionalMirroredDataset(mirroredSpectrumMzArr, mirroredSpectrumIntArr, color, color);
                }
            }
        }


        return newSpectrumPanel;
    }

    /**
     * Use this to force-redraw of the SpectrumPanel after making changes,
     * like adding features overlay.
     */
    public void redisplayCurrentSpectrum() {
        showSpectrum(viewport.getScanNum());
    }

    public int getMsLevelMaxAllowed() {
        return msLevelMaxAllowed;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public IScanCollection getScans() {
        return scans;
    }

    public SpectrumPanel getSpectrumPanel() {
        return spectrumPanel;
    }

    public BusHandler getBusHandler() {
        return busHandler;
    }

    /**
     * Holds mz region to be shown.
     * If either value is null, will show the full spectrum.
     */
    public static class Viewport {
        double mzLo;
        double mzHi;
        int scanNum;

        public Viewport(double mzLo, double mzHi, int scanNum) {
            this.mzLo = mzLo;
            this.mzHi = mzHi;
            this.scanNum = scanNum;
        }

        public void setMzLo(double mzLo) {
            this.mzLo = mzLo;
        }

        public void setMzHi(double mzHi) {
            this.mzHi = mzHi;
        }

        public double getMzLo() {
            return mzLo;
        }

        public double getMzHi() {
            return mzHi;
        }

        public int getScanNum() {
            return scanNum;
        }

        public void setScanNum(int scanNum) {
            this.scanNum = scanNum;
        }
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
