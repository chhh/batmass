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
package umich.ms.batmass.gui.viewers.map2d.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import umich.ms.batmass.gui.core.api.comm.dnd.DnDButton;
import umich.ms.batmass.gui.core.api.comm.eventbus.ViewerLinkSupport;
import umich.ms.batmass.gui.core.api.swing.BMToolBar;
import umich.ms.batmass.gui.core.api.swing.CustomComboBoxRenderer;
import umich.ms.batmass.gui.core.awt.ModifiedFlowLayout;
import umich.ms.batmass.gui.management.EBus;
import umich.ms.batmass.gui.viewers.map2d.actions.GoToAction;
import umich.ms.batmass.gui.viewers.map2d.actions.HomeMapAction;
import umich.ms.batmass.gui.viewers.map2d.actions.UpdateMapAction;
import umich.ms.batmass.gui.viewers.map2d.messages.MsgMouseAction;
import umich.ms.datatypes.LCMSData;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.util.DoubleRange;
import umich.ms.util.IntervalST;

/**
 *
 * @author Dmitry Avtonomov
 */
public class Map2DToolbar extends BMToolBar implements PropertyChangeListener {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/viewers/map2d/icons/icon_msn.png";
    
    protected JComboBox<Integer> cmbMsLevel;
    protected DefaultComboBoxModel<Integer> msLevelsModel;
    
    protected JComboBox<DoubleRange> cmbMzRange;
    protected DefaultComboBoxModel<DoubleRange> mzRangesModel;

    protected JComboBox<String> comboDenoise;
    protected JFormattedTextField fmtIntensityCutoff;

    protected JButton btnUpdate;
    protected JButton btnHome;

    protected JButton btnGoTo;
    protected JToggleButton btnMsNDisplay;

    protected ViewerLinkSupport linkSupport;
    protected EBus bus;
    protected DnDButton btnLinkDnD;
    protected JButton btnUnlink;

    // maintains its own copy of the options class, if you're using this one in
    // Map2DPanel, you should make an explicit copy via the copy constructor
    protected Map2DPanelOptions options;
    private UpdateMapAction updateAction;
    private GoToAction goToAction;
    private HomeMapAction homeAction;

    protected static final int toolbarBtnHSpacing = 3;
    private final JComboBox<String> comboMouseAction;
   
    
    /**
     * Creates a new toolbar with all the controls disabled.
     * @param linkSupport 
     */
    @SuppressWarnings({"unchecked"})
    public Map2DToolbar(ViewerLinkSupport linkSupport, EBus bus) {
        this.linkSupport = linkSupport;
        this.bus = bus;

        // Creating the toolbar
        setBorder(new EmptyBorder(0, toolbarBtnHSpacing, toolbarBtnHSpacing, toolbarBtnHSpacing));
        setFloatable(false);
        setLayout(new ModifiedFlowLayout(FlowLayout.LEFT, 0, 3));
        // make sure the toolbar can't get focus by itself
        setFocusable(false);
        setRollover(true);

        // MS Level selector
        cmbMsLevel = new JComboBox<>();
        cmbMsLevel.setPrototypeDisplayValue(10);
        cmbMsLevel.setMaximumSize(cmbMsLevel.getMinimumSize());

        JLabel lblMsLevel = new JLabel("MS Lvl", SwingConstants.LEFT);
        add(lblMsLevel);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        add(lblMsLevel);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        add(cmbMsLevel);


        // m/z range selector
        cmbMzRange = new JComboBox<>();
        cmbMzRange.setRenderer(new MzRangeRenderer());
        cmbMzRange.setPrototypeDisplayValue(new DoubleRange(1000d, 1001d));
        cmbMzRange.setMaximumSize(cmbMzRange.getMinimumSize());

//        cmbMzRange = new BMJComboBox<>();
//        MzRangeRenderer mzRangeRenderer = new MzRangeRenderer();
//        mzRangeRenderer.addToComboBox(cmbMzRange);
//        cmbMzRange.setPrototypeDisplayValue(new DoubleRange(1000d, 1001d));
//        cmbMzRange.setMaximumSize(cmbMzRange.getMinimumSize());
//        cmbMzRange.setMaximumRowCount(10);
        

        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        JLabel lblMzRange = new JLabel("m/z");
        add(lblMzRange);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        add(cmbMzRange);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));


        btnUpdate = new JButton();
        add(btnUpdate);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));

        // GoTo scan button
        btnGoTo = new JButton();
        add(btnGoTo);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        
        // Home button (zoom out)
        btnHome = new JButton();
        add(btnHome);
        
        btnMsNDisplay = new JToggleButton();
        ImageIcon btnMsNDisplayIcon = ImageUtilities.loadImageIcon(ICON_PATH, false);
        btnMsNDisplay.setIcon(btnMsNDisplayIcon);
        add(btnMsNDisplay);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        
        
        
        
        DefaultComboBoxModel<String> modelComboDenoise = new DefaultComboBoxModel<>(new String[] {
            Map2DPanelOptions.Denoise.NONE,
            Map2DPanelOptions.Denoise.ISO_SPACING,
            Map2DPanelOptions.Denoise.MEX_HAT,
            Map2DPanelOptions.Denoise.LONG_ELUTING,
        });
        comboDenoise = new JComboBox<String>(modelComboDenoise);
        final String tipDenoise = "Apply denoising to map";
        final JLabel lblDenoise = new JLabel("Denoise");
        comboDenoise.setToolTipText(tipDenoise);
        lblDenoise.setToolTipText(tipDenoise);
        add(lblDenoise);
        add(comboDenoise);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        
        
        
        DefaultComboBoxModel<String> modelComboMouseAction = new DefaultComboBoxModel<>(new String[] {
            Map2DPanelOptions.MouseAction.ZOOM,
            Map2DPanelOptions.MouseAction.SELECT,
        });
        comboMouseAction = new JComboBox<String>(modelComboMouseAction);
        final String tipMouseAction = "What happens when you click-drag with mouse";
        final JLabel lblMosueAction = new JLabel("Mouse");
        comboDenoise.setToolTipText(tipMouseAction);
        lblMosueAction.setToolTipText(tipMouseAction);
        add(lblMosueAction);
        add(comboMouseAction);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        
        String fmtIntensityCutoffTooltip = "Apply hard cutoff at the specified intensity level.";
        fmtIntensityCutoff = new JFormattedTextField(0.0d);
        fmtIntensityCutoff.setMinimumSize(new Dimension(50, 25));
        fmtIntensityCutoff.setPreferredSize(new Dimension(75, 25));
        fmtIntensityCutoff.setToolTipText(fmtIntensityCutoffTooltip);
        JLabel lblCutoff = new JLabel("Cut");
        lblCutoff.setToolTipText(fmtIntensityCutoffTooltip);
        add(lblCutoff);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));
        add(fmtIntensityCutoff);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));

        // Link button
        btnLinkDnD = linkSupport.getBtnLinkDnD();
        add(btnLinkDnD);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));


        // Unlink button
        btnUnlink = linkSupport.getBtnUnlink();
        add(btnUnlink);
        add(Box.createHorizontalStrut(toolbarBtnHSpacing));


        // make sure no children of the toolbar can be focused
        for (int i=0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            if (JComboBox.class.isAssignableFrom(comp.getClass())) {
                // combo-boxes should be focusable, so you could use the keybord
                comp.setFocusable(true);
                comp.setEnabled(true);
                continue;
            }
            comp.setFocusable(false);
            comp.setEnabled(false);
        }
    }


    private void populateMzRangeModel(IntervalST<Double, TreeMap<Integer, IScan>> mzRangesAtCurLevel) {
        mzRangesModel.removeAllElements();

        if (mzRangesAtCurLevel == null) {
            mzRangesModel.addElement(Map2DPanel.OPT_DISPLAY_ALL_MZ_REGIONS);
            return;
        }

        if (mzRangesAtCurLevel.size() > 1) {
            // add the infinite interval, which represents the option to show any m/z range
            mzRangesModel.addElement(Map2DPanel.OPT_DISPLAY_ALL_MZ_REGIONS);
        }
        for (IntervalST.Node<Double, TreeMap<Integer, IScan>> rangeNode : mzRangesAtCurLevel) {
            mzRangesModel.addElement(DoubleRange.fromInterval1D(rangeNode.getInterval()));
        }
    }

    //public void init(LCMSData data, UpdateMapAction updateAction, Map2DPanelOptions opts) {
    public void init(LCMSData data, Map2DComponent mapComp) {
        assert(mapComp.getMap2DPanel() != null);
        final WeakReference<IScanCollection> scansRef = new WeakReference<>(data.getScans());
        IScanCollection scans = data.getScans();

        ActionMap am = mapComp.getActionMap();
        Action action = am.get(GoToAction.ACTION_ID);
        assert(action != null && action instanceof GoToAction);
        this.goToAction = (GoToAction)action;

        action = am.get(UpdateMapAction.ACTION_ID);
        assert(action != null && action instanceof UpdateMapAction);
        this.updateAction = (UpdateMapAction)action;
        
        action = am.get(HomeMapAction.ACTION_ID);
        assert(action != null && action instanceof HomeMapAction);
        this.homeAction = (HomeMapAction)action;

        this.options = mapComp.getMap2DPanel().getOptions().copy();

        // MS Level selector
        Set<Integer> msLevelsSet = scans.getMapMsLevel2index().keySet();
        Integer[] msLevels = msLevelsSet.toArray(new Integer[msLevelsSet.size()]);
        Arrays.sort(msLevels);
        msLevelsModel = new DefaultComboBoxModel<>(msLevels);
        Integer curMsLevel = options.getMsLevel();
        msLevelsModel.setSelectedItem(curMsLevel);

        cmbMsLevel.setPrototypeDisplayValue(msLevels[msLevels.length-1]);
        cmbMsLevel.setMaximumSize(cmbMsLevel.getMinimumSize());
        cmbMsLevel.setModel(msLevelsModel);
        cmbMsLevel.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Integer selectedMsLevel = (Integer)cmbMsLevel.getSelectedItem();
                if (Objects.equals(selectedMsLevel, options.getMsLevel())) {
                    // if it's the same ms level, then don't do anything
                    return;
                }
                options.setMsLevel(selectedMsLevel);
                IScanCollection scans = scansRef.get();
                if (scans == null) {
                    // the scan collection is not available anymore
                    return;
                }
                IntervalST<Double, TreeMap<Integer, IScan>> rangeGrps = scans.getMapMsLevel2rangeGroups().get(selectedMsLevel);
                if (cmbMzRange != null && rangeGrps != null) {
                    populateMzRangeModel(rangeGrps);
                }
            }
        });
        cmbMsLevel.setEnabled(msLevels.length >= 2);

        // m/z range selector
        TreeMap<Integer, IntervalST<Double, TreeMap<Integer, IScan>>> rangeGrps = scans.getMapMsLevel2rangeGroups();
        IntervalST<Double, TreeMap<Integer, IScan>> mzRangesAtCurLevel = rangeGrps.get(curMsLevel);
        mzRangesModel = new DefaultComboBoxModel<>();
        populateMzRangeModel(mzRangesAtCurLevel);
        mzRangesModel.setSelectedItem(options.getMzRange());
        mzRangesModel.addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                setState();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                setState();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {}

            private void setState() {
                boolean shouldBeEnabled = mzRangesModel.getSize() > 1;
                if (cmbMzRange.isEnabled() != shouldBeEnabled) {
                    cmbMzRange.setEnabled(shouldBeEnabled);
                }
            }
        });
        DoubleRange largestRange = mzRangesModel.getElementAt(mzRangesModel.getSize()-1);
        cmbMzRange.setPrototypeDisplayValue(largestRange);
        cmbMzRange.setMaximumSize(cmbMzRange.getMinimumSize());
        cmbMzRange.setMaximumRowCount(10);
        cmbMzRange.setModel(mzRangesModel);
        cmbMzRange.setEnabled(cmbMzRange.getModel().getSize() >= 2);
        cmbMzRange.addItemListener(new ItemListener() {
            @SuppressWarnings("unchecked")
            @Override public void itemStateChanged(ItemEvent e) {
                DoubleRange selectedMzRange = (DoubleRange)e.getItem();
                if (Objects.equals(options.getMzRange(), selectedMzRange)) {
                    return;
                }
                options.setMzRange(selectedMzRange);
            }
        });


        // Update button
        btnUpdate.setAction(updateAction);
        btnUpdate.setEnabled(true);

        // GoTo button
        btnGoTo.setAction(goToAction);
        btnGoTo.setEnabled(true);
        
        // Home button
        btnHome.setAction(homeAction);
        btnHome.setEnabled(true);
        
        // MSn event display button
        btnMsNDisplay.setEnabled(true);
        btnMsNDisplay.setSelected(options.getMs2Overlay());
        btnMsNDisplay.setToolTipText("Draw locations of MS/MS events.");
        btnMsNDisplay.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.setMs2Overlay(btnMsNDisplay.isSelected());
            }
        });
        
        // Denoise combo box
        comboDenoise.addItemListener((e) -> {
            options.setDoDenoise((String)comboDenoise.getSelectedItem());
        });
        comboDenoise.setEnabled(true);
        
        comboMouseAction.addItemListener(e -> {
            bus.postSticky(new MsgMouseAction((String)comboMouseAction.getSelectedItem()));
        });
        
        // Cutoff value
        fmtIntensityCutoff.setValue(options.getCutoff());
        fmtIntensityCutoff.setEnabled(true);
        fmtIntensityCutoff.setEditable(true);
        fmtIntensityCutoff.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Object newValue = evt.getNewValue();
                if (newValue == null || (Double)newValue < 0) {
                    fmtIntensityCutoff.setValue(0d);
                    options.setCutoff(0d);
                } else {
                    options.setCutoff((Double)newValue);
                }
            }
        });

        // Link button
        btnLinkDnD.setEnabled(true);
        
        // Unlink button
        btnUnlink.setEnabled(false);

        // make sure no children of the toolbar can be focused
        for (int i=0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            if (JComboBox.class.isAssignableFrom(comp.getClass())
                    || JFormattedTextField.class.isAssignableFrom(comp.getClass())
                    || JTextField.class.isAssignableFrom(comp.getClass())) {
                // combo-boxes and text boxes should be focusable, so you could use the keybord
                comp.setFocusable(true);
            } else {
                comp.setFocusable(false);
            }
            if (JLabel.class.isAssignableFrom(comp.getClass()) 
                    || JRadioButton.class.isAssignableFrom(comp.getClass())) {
                comp.setEnabled(true);
            }
        }

        // register self as a listener for changes in options
        options.addPropertyChangeListener(this);

        revalidate();
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // listen to changes in Map2DPanelOptions changes, associated with this toolbar
        updateAction.actionPerformed(null);
    }

    public Map2DPanelOptions getOptions() {
        return options;
    }

    
    protected class MzRangeRenderer extends CustomComboBoxRenderer {

        @Override
        protected String getDisplayValue(Object value) {
            if (value instanceof DoubleRange) {
                @SuppressWarnings("unchecked")
                DoubleRange interval = (DoubleRange) value;

                if (interval.equals(Map2DPanel.OPT_DISPLAY_ALL_MZ_REGIONS)) {
                    return "All available";
                }

                // check if we have decimal values for our intervals
                String fmt;
                long loDecPart = (long) ((interval.lo - Math.floor(interval.lo)) * 100);
                long hiDecPart = (long) ((interval.hi - Math.floor(interval.hi)) * 100);
                if (loDecPart == 0 && hiDecPart == 0) {
                    fmt = "%.0f - %.0f";
                } else {
                    fmt = "%.1f - %.1f";
                }
                return String.format(fmt, interval.lo, interval.hi);
            }
            return value.toString();
        }
    }
    
}
