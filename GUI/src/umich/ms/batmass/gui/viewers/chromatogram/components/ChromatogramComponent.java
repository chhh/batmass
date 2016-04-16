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
package umich.ms.batmass.gui.viewers.chromatogram.components;

import java.util.Map;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import umich.ms.batmass.gui.core.api.BMComponentJPanel;
import umich.ms.batmass.gui.core.components.chromatogram.ChromatogramPanel;
import umich.ms.batmass.gui.viewers.chromatogram.actions.ExtractChromatogramAction;
import umich.ms.batmass.nbputils.OutputWndPrinter;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.exceptions.FileParsingException;


/**
 *
 * @author dmitriya
 */
public class ChromatogramComponent extends BMComponentJPanel {
    private PlotType plotType;
    private IScanCollection scans;
    private final DefaultComboBoxModel<PlotType> comboModel;
    private ChromatogramPanel chromoPanel;
    private Viewport view;

    private ExtractChromatogramAction extractChromatogramAction;

    public enum PlotType {
        TIC, BPC, BPCbyTIC, TICbyBPC, BPCbyMIN, MIN;
        private String description;

        static {
            for (PlotType type : PlotType.values()) {
                switch (type) {
                    case TIC:
                        type.description = NbBundle.getMessage(ChromatogramComponent.class, "ChromatogramComponent.btnBasePeak.text.TIC");
                        break;
                    case BPC:
                        type.description = NbBundle.getMessage(ChromatogramComponent.class, "ChromatogramComponent.btnBasePeak.text.BasePeak");
                        break;
                    case TICbyBPC:
                        type.description = NbBundle.getMessage(ChromatogramComponent.class, "ChromatogramComponent.btnBasePeak.text.TICbyBPC");
                        break;
                    case BPCbyTIC:
                        type.description = NbBundle.getMessage(ChromatogramComponent.class, "ChromatogramComponent.btnBasePeak.text.BPCbyTIC");
                        break;
                    case BPCbyMIN:
                        type.description = NbBundle.getMessage(ChromatogramComponent.class, "ChromatogramComponent.btnBasePeak.text.BPCbyMIN");
                        break;
                    case MIN:
                        type.description = NbBundle.getMessage(ChromatogramComponent.class, "ChromatogramComponent.btnBasePeak.text.MIN");
                        break;
                    default:
                        throw new IllegalStateException("Should never happen, only Enum types can be provided");
                }
            }
        }

        @Override
        public String toString() {
            return description;
        }
    };

    /**
     * Creates new form ChromatogtamPanel
     */
    public ChromatogramComponent() {
        ic.add(ic); // needed for D&D linking between viewers
        initComponents();

        plotType = PlotType.TIC;

        // set up the plot type combo box model
        comboModel = new DefaultComboBoxModel<>(PlotType.values());
        comboPlotType.setModel(comboModel);
        comboPlotType.setMaximumRowCount(PlotType.values().length);
        //comboPlotType.setSelectedIndex(0);
        
        extractChromatogramAction = new ExtractChromatogramAction(this);

        this.validate();
    }

    public void setScanCollection(IScanCollection scans) {
        this.scans = scans;
        this.view = new Viewport(1);
        updateChromatogramView(plotType, view);
    }

    public IScanCollection getScanCollection() {
        return this.scans;
    }

    public Viewport getView() {
        return view;
    }

    public ExtractChromatogramAction getExtractChromatogramAction() {
        return extractChromatogramAction;
    }

    public void updateChromatogramView(Viewport view) {
        updateChromatogramView(plotType, view);
    }

    public void updateChromatogramView(PlotType type, Viewport view) {
        plotType = type;
        this.view = view;
        ChromatogramPanel newChromatogramPanel = createChromatogramPanel(type, view);
        this.mainPanel.removeAll();
        if (newChromatogramPanel == null) {
            OutputWndPrinter.printOut("DEBUG", "Returned ChromatogramPanel was null, removing the "
                    + "old one, but not adding the new one.");
            return;
        }
        this.mainPanel.add(newChromatogramPanel);
        this.chromoPanel = newChromatogramPanel;
        if (view.getMzLo() != null && view.getMzHi() != null) {
            this.infoLabel.setText(String.format(
                "XIC: [%.4f - %.4f] @ [%.2f - %.2f]",  view.getMzLo(), view.getMzHi(), view.getRtLo(), view.getRtHi()));
        } else {
            this.infoLabel.setText("");
        }
        this.revalidate();
    }

    private ChromatogramPanel createChromatogramPanel(PlotType type, Viewport view) {
        plotType = type;
        int msLevel = view.getMsLevel();
        Integer scanCount = scans.getScanCountAtMsLevel(view.getMsLevel());
        if (scanCount == null) {
            Exceptions.printStackTrace(new IllegalStateException(String.format(
                    "Scan count was null at MS Level %d, meaning this MS Level was not present in "
                            + "the scan collection.", view.getMsLevel())));
            return null;
        }
        double[] xValues = new double[scanCount];
        double[] yValues = new double[scanCount];

        ISpectrum spectrum = null;
        int cnt = 0;
        for (Map.Entry<Integer, IScan> is : scans.getMapMsLevel2index().get(msLevel).getNum2scan().entrySet()) {
            IScan s = is.getValue();
            try {
                spectrum = s.fetchSpectrum();
            } catch (FileParsingException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (spectrum == null) {
                Exceptions.printStackTrace(new IllegalStateException("Fetched spectrum was null"));
                return null;
            }

            if (!view.isXIC()) {
                switch (type) {
                    case TIC:
                        yValues[cnt] = s.getTic();
                        break;
                    case BPC:
                        yValues[cnt] = s.getBasePeakIntensity();
                        break;
                    case TICbyBPC:
                        yValues[cnt] = s.getTic() / s.getBasePeakIntensity();
                        break;
                    case BPCbyTIC:
                        yValues[cnt] = s.getBasePeakIntensity() / s.getTic();
                        break;
                    case BPCbyMIN:
                        yValues[cnt] = s.getBasePeakIntensity() / spectrum.getMinIntNonZero();
                        break;
                    case MIN:
                        yValues[cnt] = spectrum.getMinIntNonZero();
                        break;
                    default:
                        throw new AssertionError("Should never happen, only Enum types can be provided");
                }
            } else {

                double mzLo = view.getMzLo() == null ? spectrum.getMinMZ() : view.getMzLo();
                double mzHi = view.getMzHi() == null ? spectrum.getMaxMZ() : view.getMzHi();
                int[] mzIdxs = spectrum.findMzIdxs(mzLo, mzHi);

                if (mzIdxs != null) {
                    int idxLo = mzIdxs[0];
                    int idxHi = mzIdxs[1];
                    int len = idxHi - idxLo + 1;
                    double[] ints = spectrum.getIntensities();
                    double intensity, bpc = 0d, tic = 0d, minNonZero = Double.POSITIVE_INFINITY;
                    switch (type) {
                        case TIC:
                            for (int i = 0; i < len; i++) {
                                yValues[cnt] += ints[idxLo + i];
                            }
                            break;
                        case BPC:
                            bpc = 0d;
                            for (int i = 0; i < len; i++) {
                                intensity = ints[idxLo + i];
                                bpc = bpc < intensity ? intensity : bpc;
                            }
                            yValues[cnt] = bpc;
                            break;
                        case TICbyBPC:
                            bpc = 0d;
                            tic = 0d;
                            for (int i = 0; i < len; i++) {
                                intensity = ints[idxLo + i];
                                bpc = bpc < intensity ? intensity : bpc;
                                tic += intensity;
                            }
                            yValues[cnt] = tic / bpc;
                            break;
                        case BPCbyTIC:
                            bpc = 0d;
                            tic = 0d;
                            for (int i = 0; i < len; i++) {
                                intensity = ints[idxLo + i];
                                bpc = bpc < intensity ? intensity : bpc;
                                tic += intensity;
                            }
                            yValues[cnt] = bpc / tic;
                            break;
                        case BPCbyMIN:
                            bpc = 0d;
                            minNonZero = Double.POSITIVE_INFINITY;
                            for (int i = 0; i < len; i++) {
                                intensity = ints[idxLo + i];
                                bpc = bpc < intensity ? intensity : bpc;
                                if (intensity < minNonZero) {
                                    minNonZero = intensity;
                                }
                            }
                            yValues[cnt] = bpc / minNonZero;
                            break;
                        case MIN:
                            minNonZero = Double.POSITIVE_INFINITY;
                            for (int i = 0; i < len; i++) {
                                intensity = ints[idxLo + i];
                                if (intensity < minNonZero) {
                                    minNonZero = intensity;
                                }
                            }
                            yValues[cnt] = minNonZero == Double.POSITIVE_INFINITY ? 0d : minNonZero;
                            break;
                        default:
                            throw new AssertionError("Should never happen, only Enum types can be provided");
                    }
                } else {
                    yValues[cnt] = 0d;
                }
            }

            xValues[cnt] = s.getRt();
            cnt++;
        }


        // create the chromatogram
        ChromatogramPanel chromatogramPanel = new ChromatogramPanel(
                xValues, yValues, "Time", "Intensity");
        chromatogramPanel.setMaxPadding(65);
        chromatogramPanel.setBorder(null);
        chromatogramPanel.setYDataIsPositive(true);


        if (view.isXIC()) {
            TreeMap<Integer, IScan> num2scan = scans.getMapMsLevel2index().get(msLevel).getNum2scan();
            Double rtLo = view.getRtLo();
            Double rtHi = view.getRtHi();
            rtLo = rtLo != null ? rtLo : num2scan.firstEntry().getValue().getRt();
            rtHi = rtHi != null ? rtHi : num2scan.lastEntry().getValue().getRt();
            chromatogramPanel.rescale(rtLo, rtHi);
        }

        return chromatogramPanel;
    }

    public void zoom() {
        
    }

    public static class Viewport {

        int msLevel;
        /** Can be null, for TIC. */
        Double mzLo;
        /** Can be null, for TIC. */
        Double mzHi;
        Double rtLo;
        Double rtHi;

        public Viewport(int msLevel, Double mzLo, Double mzHi, Double rtLo, Double rtHi) {
            this.msLevel = msLevel;
            this.mzLo = mzLo;
            this.mzHi = mzHi;
            this.rtLo = rtLo;
            this.rtHi = rtHi;
        }

        public Viewport copy() {
            Viewport viewport = new Viewport(this.getMsLevel());
            viewport.setMzLo(this.getMzLo());
            viewport.setMzHi(this.getMzHi());
            viewport.setRtLo(this.getRtLo());
            viewport.setRtHi(this.getRtHi());
            return viewport;
        }

        public Viewport(int msLevel) {
            this.msLevel = msLevel;
        }

        public Double getMzLo() {
            return mzLo;
        }

        public void setMzLo(Double mzLo) {
            this.mzLo = mzLo;
        }

        public Double getMzHi() {
            return mzHi;
        }

        public void setMzHi(Double mzHi) {
            this.mzHi = mzHi;
        }

        public Double getRtLo() {
            return rtLo;
        }

        public void setRtLo(Double rtLo) {
            this.rtLo = rtLo;
        }

        public Double getRtHi() {
            return rtHi;
        }

        public void setRtHi(Double rtHi) {
            this.rtHi = rtHi;
        }

        public int getMsLevel() {
            return msLevel;
        }

        public void setMsLevel(int msLevel) {
            this.msLevel = msLevel;
        }

        public boolean isXIC() {
            return mzLo != null || mzHi != null;
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

        mainPanel = new javax.swing.JPanel();
        infoPanel = new javax.swing.JPanel();
        infoLabel = new javax.swing.JLabel();
        comboPlotType = new javax.swing.JComboBox<PlotType>();
        btnReset = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        mainPanel.setMinimumSize(new java.awt.Dimension(50, 50));
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new java.awt.BorderLayout());
        add(mainPanel, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(ChromatogramComponent.class, "ChromatogramComponent.infoLabel.text")); // NOI18N

        comboPlotType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPlotTypeActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnReset, org.openide.util.NbBundle.getMessage(ChromatogramComponent.class, "ChromatogramComponent.btnReset.text")); // NOI18N
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(comboPlotType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnReset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboPlotType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(infoPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void comboPlotTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPlotTypeActionPerformed
        PlotType type = (PlotType)comboModel.getSelectedItem();
        if (type == null)
            return;
        plotType = type;
        updateChromatogramView(plotType, view);
    }//GEN-LAST:event_comboPlotTypeActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        extractChromatogramAction.actionPerformed(null);
    }//GEN-LAST:event_btnResetActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReset;
    private javax.swing.JComboBox<PlotType> comboPlotType;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
