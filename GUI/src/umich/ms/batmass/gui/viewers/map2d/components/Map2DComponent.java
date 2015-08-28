/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.viewers.map2d.components;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.ILCMSFeature2D;
import umich.ms.batmass.gui.core.api.BMComponentJPanel;
import umich.ms.batmass.gui.core.api.comm.eventbus.ViewerLinkSupport;
import umich.ms.batmass.gui.viewers.map2d.actions.GoToAction;
import umich.ms.batmass.gui.viewers.map2d.actions.UpdateMapAction;
import umich.ms.datatypes.LCMSData;
import umich.ms.datatypes.LCMSDataSubset;
import umich.ms.fileio.exceptions.FileParsingException;

/**
 *
 * @author dmitriya
 */
@NbBundle.Messages({"MapComponent.text=Map2D Info"})
@SuppressWarnings({"rawtypes"})
public class Map2DComponent extends BMComponentJPanel {

    // main display components
    protected Map2DToolbar toolbar;
    protected Map2DPanel map2DPanel;
    protected Map2DInfoLabel map2DInfoLabel;

    // actions
    private UpdateMapAction updateAction;
    private GoToAction goToAction;

    // message bus
    protected ViewerLinkSupport linkSupport;

    // lookup - listening to data
    protected Lookup.Result<LCMSData> dataLkpResult;
    private final LCMSDataResultListener lcmsDataResultListener;
    
    protected Lookup.Result<Features> featureLkpResult;
    private final FeaturesResultListener featuresResultListener;
    
    public static final LCMSDataSubset INITIAL_SUBSET = LCMSDataSubset.MS1_WITH_SPECTRA;
    private Set<LCMSDataSubset> subsets;

    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/view_map2d_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);
    @StaticResource
    private static final String ICON_PATH_LINK = "umich/ms/batmass/gui/viewers/map2d/icons/icon_link.png";
    @StaticResource
    private static final String ICON_PATH_UNLINK = "umich/ms/batmass/gui/viewers/map2d/icons/icon_unlink.png";
    protected static String TITLE_NO_DATA = "2D View";
    
    /**
     * Creates new form MapContainer
     */
    public Map2DComponent() {
        // WARNING: ACHTUNG: removed the GUI-builder call to initComponents(),
        // creating everything manually now
        //initComponents();

        ic.add(ic); // needed for D&D linking between viewers
        // listening for LCMSData in the lookup
        dataLkpResult = lkp.lookupResult(LCMSData.class);
        lcmsDataResultListener = new LCMSDataResultListener();
        dataLkpResult.addLookupListener(lcmsDataResultListener);
        
        featureLkpResult = lkp.lookupResult(Features.class);
        featuresResultListener = new FeaturesResultListener();
        featureLkpResult.addLookupListener(featuresResultListener);
        
        
        subsets = Collections.newSetFromMap(new ConcurrentHashMap<LCMSDataSubset, Boolean>());

        initComponentsManually();
        //populateActionInputMaps();
    }

    
    
    private class LCMSDataResultListener implements LookupListener {

        @Override
        public void resultChanged(LookupEvent ev) {
            Collection<? extends LCMSData> datas = dataLkpResult.allInstances();
            if (datas.isEmpty()) {
                // no LCMSData in the lookup, just return to undetermined state
                initComponentsManually();
                return;
            }
            if (datas.size() > 1) {
                throw new UnsupportedOperationException("Map2D Component does not yet know"
                        + " how to render several datasets at once");
            }

            LCMSData data = datas.iterator().next();
            map2DPanel.setScans(data.getScans());
            map2DPanel.setDefaultViewport(null);
            map2DPanel.initMap();
            populateActionInputMaps();
            toolbar.init(data, Map2DComponent.this);
            revalidate();
        }
    
    }
    
    private class FeaturesResultListener implements LookupListener {

        @Override
        @SuppressWarnings({"unchecked"})
        public void resultChanged(LookupEvent ev) {
            Collection<? extends Features> features = featureLkpResult.allInstances();
            if (features.isEmpty()) {
                // no FeatureData in the lookup, just return to undetermined state
                map2DPanel.setFeatures(null);
                return;
            }
            if (features.size() > 1) {
                throw new UnsupportedOperationException("Map2D Component does not yet know"
                        + " how to render several sets of features at once");
            }

            Features<ILCMSFeature2D<?>> feats = features.iterator().next();
            map2DPanel.setFeatures(feats);
            map2DPanel.initMap();
            revalidate();
        }
        
    }

    /**
     * Map2DPanel must be created prior to calling this method.
     */
    private void populateActionInputMaps() {
        assert(map2DPanel != null);

        ActionMap am = this.getActionMap();
        updateAction = new UpdateMapAction(this);
        am.put(UpdateMapAction.ACTION_ID, updateAction);
        goToAction = new GoToAction(map2DPanel);
        am.put(GoToAction.ACTION_ID, goToAction);
        
        InputMap im = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(UpdateMapAction.ACCELERATOR,  UpdateMapAction.ACTION_ID);
        im.put(GoToAction.ACCELERATOR, GoToAction.ACTION_ID);
        
    }
    

    /**
     * This method is used in the constructor to create all the Components
     * manually instead of relying on NetBeans GUI Builder.<br/>
     */
    private void initComponentsManually() {
        removeAll();
        setLayout(new BorderLayout());

        map2DPanel = new Map2DPanel();
        map2DPanel.setBorder(new EmptyBorder(4, 4, 2, 4));

        map2DInfoLabel = new Map2DInfoLabel();
        map2DInfoLabel.setBorder(new EmptyBorder(2, 4, 2, 4));
        map2DPanel.setInfoDisplayer(map2DInfoLabel);

        linkSupport = new ViewerLinkSupport(
                Collections.singleton(this),        // highlight components
                Collections.singleton(map2DPanel.getBusHandler()),  // subscribers (have @Handler methods to recieve messages)
                Collections.singleton(map2DPanel.getBusHandler()),
                this
        );

        toolbar = new Map2DToolbar(linkSupport);

        add(toolbar, BorderLayout.NORTH);
        add(map2DPanel, BorderLayout.CENTER);
        add(map2DInfoLabel, BorderLayout.SOUTH);
    }

    public Map2DInfoLabel getMap2DInfoLabel() {
        return map2DInfoLabel;
    }

    public Map2DPanel getMap2DPanel() {
        return map2DPanel;
    }

    public Map2DToolbar getToolbar() {
        return toolbar;
    }

    /**
     * Loads data in a particular LCMSData object. Use this method only if you
     * want to pre-load the data in LCMSData before adding this LCMSData to
     * the lookup.
     * @param subset
     * @param data
     * @throws FileParsingException
     */
    public final void load(LCMSDataSubset subset, LCMSData data) throws FileParsingException {
        subsets.add(subset);
        data.load(subset, this);
    }

    /**
     * Unloads data from a particular instance of LCMSData. This method is not
     * very useful for outside users, used internally.
     * @param subset
     * @param data
     */
    public final void unload(LCMSDataSubset subset, LCMSData data) {
        subsets.remove(subset);
        data.unload(subset, this, null);
    }

    /**
     * Loads the subset into all LCMSData objects in the lookup.
     * @param subset
     * @throws FileParsingException
     * @throws IllegalStateException if there are no LCMSData objects in the lookup
     */
    public final void loadIntoAll(LCMSDataSubset subset) throws FileParsingException {
        subsets.add(subset);
        Collection<? extends LCMSData> datas = getLookup().lookupAll(LCMSData.class);
        if (datas.isEmpty()) {
            throw new IllegalStateException("No LCMSData objects found in the lookup");
        }
        for (LCMSData data : datas) {
            data.load(subset, this);
        }
    }

    /**
     * Unloads all loaded subsets from all LCMSData objects in the lookup.
     * @param except Can be null. Subsets in this list won't be unloaded.
     * @throws IllegalStateException if there are no LCMSData objects in the lookup
     */
    public final void unlaodFromAll(Set<LCMSDataSubset> except) {
        Collection<? extends LCMSData> datas = getLookup().lookupAll(LCMSData.class);
        if (datas.isEmpty()) {
            throw new IllegalStateException("No LCMSData objects found in the lookup");
        }

        HashSet<LCMSDataSubset> subsetsUnloaded = new HashSet<>(subsets.size());
        try {
            for (LCMSData data : datas) {
                for (LCMSDataSubset subset : subsets) {
                    if (except != null && except.contains(subset)) {
                        continue;
                    }
                    data.unload(subset, this, except);
                    subsetsUnloaded.add(subset);
                }
            }
        } finally {
            subsets.removeAll(subsetsUnloaded);
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
