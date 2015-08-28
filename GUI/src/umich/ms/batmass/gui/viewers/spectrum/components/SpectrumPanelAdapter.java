/*
 * License placeholder.
 */
package umich.ms.batmass.gui.viewers.spectrum.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import umich.ms.batmass.gui.core.components.spectrum.SpectrumPanel;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scan.props.PrecursorInfo;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.exceptions.FileParsingException;

/**
 *
 * @author dmitriya
 */
public class SpectrumPanelAdapter {
    IScanCollection scans;
    SpectrumPanel currentSpectrumPanel;
    Integer currentScanNum;
    double curVisibleMinX;
    double curVisibleMaxX;
    int msLevelMaxAllowed;
    boolean keepZoom = false;

    public SpectrumPanelAdapter(IScanCollection scans) {
        this.scans = scans;
        Set<Integer> msLevelsSet = scans.getMapMsLevel2index().keySet();
        Integer[] msLevels = msLevelsSet.toArray(new Integer[msLevelsSet.size()]);
        Arrays.sort(msLevels);
        msLevelMaxAllowed = msLevels[0];
    }
    
    public void setCurrentScanNum(Integer currentScanNum) {
        this.currentScanNum = currentScanNum;
    }

    public Integer getCurrentScanNum() {
        return currentScanNum;
    }

    public IScanCollection getScans() {
        return scans;
    }

    public void setScans(IScanCollection scans) {
        this.scans = scans;
    }

    public int getMsLevelMaxAllowed() {
        return msLevelMaxAllowed;
    }

    public void setMsLevelMaxAllowed(int msLevelMaxAllowed) {
        this.msLevelMaxAllowed = msLevelMaxAllowed;
    }

    public SpectrumPanel getSpectrumPanel() {
        return currentSpectrumPanel;
    }

    public void setSpectrumPanel(SpectrumPanel spectrumPanel) {
        this.currentSpectrumPanel = spectrumPanel;
        this.curVisibleMinX = spectrumPanel.getXAxisZoomRangeLowerValue();
        this.curVisibleMaxX = spectrumPanel.getXAxisZoomRangeUpperValue();
    }
    
    public SpectrumPanel buildPanelForScan(IScan scan) {
        return buildPanelForScanNum(scan.getNum());
    }
    
    public SpectrumPanel buildPanelForScanNum(int scanNum) {
        IScan scan = scans.getScanByNum(scanNum);
        if (scan == null) {
            return null;
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
                
//                0d, 
//                "+150",
//                String.format("scan %d: MS%d m/z(%d-%d)", scan.getNum(), scan.getMsLevel(), 
//                (int)Math.round(scan.getScanMzWindowLower()), (int)Math.round(scan.getScanMzWindowUpper())),
//                maxPadding, false, false, false, 
                
                precursorMz, 
                charge,
                String.format("Scan #%d(%.2fm): MS%d m/z[%d-%d]", scan.getNum(), scan.getRt(), scan.getMsLevel(),
                    (int)Math.round(scan.getScanMzWindowLower()), (int)Math.round(scan.getScanMzWindowUpper())),
                maxPadding, 
                false, // show filename
                false, false, 
                
                msLevel, 
                profileMode);
//        newSpectrumPanel.setShowPrecursorDetails(profileMode);
        newSpectrumPanel.setXAxisStartAtZero(false);
        
        
        this.curVisibleMinX = newSpectrumPanel.getXAxisZoomRangeLowerValue();
        this.curVisibleMaxX = newSpectrumPanel.getXAxisZoomRangeUpperValue();
        
        
        return newSpectrumPanel;
    }
    
    
    public void zoomCurrentPanel(double mzMin, double mzMax) {
        this.curVisibleMinX = mzMin;
        this.curVisibleMaxX = mzMax;
        this.currentSpectrumPanel.rescale(mzMin, mzMax);
    }
    
    
    public void showNewSpectrumPanel(SpectrumPanel newPanel, Integer newScanNum, boolean keepMzScale) {
        SpectrumPanel prevSpectrumPanel = currentSpectrumPanel;
        Integer prevScanNum = currentScanNum;
        
        if (currentSpectrumPanel != null && keepMzScale) {
            this.curVisibleMinX = this.currentSpectrumPanel.getXAxisZoomRangeLowerValue();
            this.curVisibleMaxX = this.currentSpectrumPanel.getXAxisZoomRangeUpperValue();
            newPanel.rescale(this.curVisibleMinX, this.curVisibleMaxX);
        }
        
        currentSpectrumPanel = newPanel;
        currentScanNum = newScanNum;
        
        Container parent = prevSpectrumPanel.getParent();
//        parent.remove(prevSpectrumPanel);
        parent.removeAll();
        parent.add(constructInfoPanel(), BorderLayout.NORTH);
        parent.add(currentSpectrumPanel, BorderLayout.CENTER);
        parent.validate();
    }
    
    public void showNewSpectrumPanel(SpectrumPanel newPanel, Integer newScanNum) {
        showNewSpectrumPanel(newPanel, newScanNum, true);
    }
    
    public void showNewSpectrumPanel(SpectrumPanel newPanel, Integer newScanNum, Container newParent) {
        if (currentSpectrumPanel != null) {
            SpectrumPanel prevSpectrumPanel = currentSpectrumPanel;
            Integer prevScanNum = currentScanNum;
            Container oldParent = prevSpectrumPanel.getParent();
            oldParent.remove(prevSpectrumPanel);
            this.curVisibleMinX = this.currentSpectrumPanel.getXAxisZoomRangeLowerValue();
            this.curVisibleMaxX = this.currentSpectrumPanel.getXAxisZoomRangeUpperValue();
            newPanel.rescale(this.curVisibleMinX, this.curVisibleMaxX);
        }
        
        
        
        
        currentSpectrumPanel = newPanel;
        currentScanNum = newScanNum;
        
        
        newParent.add(currentSpectrumPanel);
        newParent.validate();
    }
    
    /**
     * Builds a JPanel with information for the current SpectrumPanel
     * @return 
     */
    public JPanel constructInfoPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        
        Set<Integer> keySet = scans.getMapMsLevel2index().keySet();
        Integer[] msLevels = keySet.toArray(new Integer[keySet.size()]);
        Arrays.sort(msLevels);
        
        final JComboBox<Integer> msLevelSelector = new JComboBox<>(msLevels);
        msLevelSelector.setSelectedItem(msLevelMaxAllowed);
        msLevelSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Integer selectedMsLevel = (Integer)msLevelSelector.getSelectedItem();
                msLevelMaxAllowed = selectedMsLevel;
            }
        });
        panel.add(new JLabel("Max MS Level", SwingConstants.LEFT));
        panel.add(msLevelSelector);
        
        
        StringBuilder sb = new StringBuilder();
        IScan scan = scans.getScanByNum(currentScanNum);
        String polarityStr = "?";
        if (scan.getPolarity() != null) {
            polarityStr = scan.getPolarity().toString();
        }
        String description = String.format("Scan: #%d(%.3fm), MS%d[%s] m/z:[%d-%d]", 
                scan.getNum(), scan.getRt(), scan.getMsLevel(), polarityStr, 
                (int)Math.round(scan.getScanMzWindowLower()), 
                (int)Math.round(scan.getScanMzWindowUpper()));
                
        JLabel infoLabel = new JLabel(description, SwingConstants.LEFT);
        panel.add(infoLabel);
        
        
        return panel;
    }
}
