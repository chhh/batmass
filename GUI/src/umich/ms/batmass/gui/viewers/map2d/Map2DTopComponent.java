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
package umich.ms.batmass.gui.viewers.map2d;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.event.UndoableEditEvent;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.BaseProgressUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.data.core.lcms.features.data.FeatureTableModelData;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.batmass.gui.management.UnloadableLCMSData;
import umich.ms.batmass.gui.viewers.map2d.actions.GoToAction;
import umich.ms.batmass.gui.viewers.map2d.actions.UpdateMapAction;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DComponent;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DPanel;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DZoomEventListener;
import umich.ms.batmass.gui.viewers.map2d.events.ZoomEvent;
import umich.ms.batmass.gui.viewers.map2d.todelete.ProcessingUmpireFeatures;
import umich.ms.batmass.nbputils.SwingHelper;
import umich.ms.datatypes.LCMSData;
import umich.ms.datatypes.LCMSDataSubset;
import umich.ms.fileio.exceptions.FileParsingException;


/**
 *
 * @author dmitriya
 */
@ConvertAsProperties(dtd = "-//umich.ms.batmass.gui.viewers.map2d//Map2DTopComponent//EN", autostore = false)
public class Map2DTopComponent extends BMTopComponent implements Map2DZoomEventListener {

    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/view_map2d_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    //protected IScanCollection scans = null;
    private final UndoRedo.Manager undo;
    private Map2DUndoZoom undoZoomEvent = null;
    private boolean reactingToUndoRedo = false;
    
    protected Map2DComponent mapComponent = null;
    //protected volatile LCMSData data = null;
    protected volatile ProcessingUmpireFeatures puf = null;
    protected static final LCMSDataSubset INITIAL_SUBSET = LCMSDataSubset.MS1_WITH_SPECTRA;

    /**
     * Creates new form Map2DTopComponent
     */
    public Map2DTopComponent() {
        super();
        initComponents();

        setIcon(ICON.getImage());
        this.setFocusable(true);
        undo = new UndoRedo.Manager();
        undo.setLimit(20); // limit to 20 rollback operations
    }


    /**
     * To be called after this TC was opened to set the file to be viewed.
     * @param data
     */
    public void setData(final LCMSData data) {

        // this will set the name of the window before we try to load the data
        final Runnable preDataLoaded = new Runnable() {
            @Override public void run() {
                makeBusy(true);
                setDisplayName(data);
                mapContainer.removeAll();
                mapComponent = new Map2DComponent();
                mapComponent.getMap2DPanel().addZoomEventListener(Map2DTopComponent.this);
                mapContainer.add(mapComponent);
                UnloadableLCMSData unloadable = new UnloadableLCMSData(data);
                addToLookup(unloadable);
            }
        };

        // this will set the name of the window after we loaded the data
        final Runnable postDataLoaded = new Runnable() {
            @Override public void run() {
                mapComponent.addToLookup(data);
                makeBusy(false);
                setKeybindings();
                requestActive();
                setProxiedLookup(mapComponent.getLookup());
            }
        };


        String progressHandleName = data.getSource().getName();
        final ProgressHandle ph = ProgressHandle.createHandle(progressHandleName);
        
        
        final Runnable loadData = new Runnable() {
            @Override public void run() {
                try {
                    mapComponent.load(Map2DComponent.INITIAL_SUBSET, data);
                } catch (FileParsingException ex) {
                    Exceptions.printStackTrace(ex);
                }
                ph.finish();
                SwingHelper.invokeOnEDT(postDataLoaded);
            }
        };


        SwingHelper.invokeOnEDTSynch(preDataLoaded);
        String dialogTitle = "Loading data";
        BaseProgressUtils.runOffEventThreadWithProgressDialog(loadData, dialogTitle, 
                ph, false, 0, 300);
        ph.start();
        // this dims the whole UI and shows a progress bar in the middle
        // ProgressUtils.showProgressDialogAndRun(loadData, dialogTitle);
        
    }

    protected void setDisplayName(LCMSData data) {
        Path path = Paths.get(data.getSource().getName());
        setDisplayName(path.getFileName().toString());
    }

    
    /**
     * @deprecated This is not used anywhere anymore, I guess it's safe to delete this one.
     * TODO: this has not been tested. The idea is that if the TC is still in the
     * process of being opened, then the map2DPanel might not yet have been created.
     * If so, we just set PUF for this TC, and it will automatically supply PUF
     * for map2DPanel, when it's created. If map2DPanel exists, and it's PUF
     * is different from the current one, then we replace it with the new one
     * and cause a redraw by calling initMap().
     * MOST LIKELY THIS WON'T WORK AS EXPECTED.
     * @param puf 
     */
    @Deprecated
    public void overlayFeatures(ProcessingUmpireFeatures puf) {
        this.puf = puf;
        if (mapComponent != null) {
            Map2DPanel map2DPanel = mapComponent.getMap2DPanel();
            if (map2DPanel != null && map2DPanel.getPuf() != puf) {
                map2DPanel.setPuf(puf);
                map2DPanel.initMap();
            }
        }
    }
    
    public void addData(final FeatureTableModelData<?> data) {
        
        // this will set the name of the window before we try to load the data
        final Runnable preDataLoaded = new Runnable() {
            @Override public void run() {
                makeBusy(true);
            }
        };

        
        final AtomicBoolean isDataLoadSuccess = new AtomicBoolean(true);
        // this will set the name of the window after we loaded the data
        final Runnable postDataLoaded = new Runnable() {
            @Override public void run() {
                if (!isDataLoadSuccess.get()) {
                    // if the data hasn't been loaded successfully
                    // just close the component
                    Map2DTopComponent.this.close();
                    return;
                }
                mapComponent.addToLookup(data.getData());
                makeBusy(false);
                setKeybindings();
                requestActive();
            }
        };


        String progressHandleName = data.getSource().getOriginURI().toString();
        final ProgressHandle ph = ProgressHandle.createHandle(progressHandleName);
        
        
        final Runnable loadData = new Runnable() {
            @Override public void run() {
                try {
                    data.load(mapComponent);
                } catch (DataLoadingException ex) {
                    Exceptions.printStackTrace(ex);
                    isDataLoadSuccess.set(false);
                }
                ph.finish();
                SwingHelper.invokeOnEDT(postDataLoaded);
            }
        };


        SwingHelper.invokeOnEDTSynch(preDataLoaded);
        String dialogTitle = "Loading data";
        BaseProgressUtils.runOffEventThreadWithProgressDialog(loadData, dialogTitle, 
                ph, false, 0, 300);
        ph.start();
    }


    @Override
    protected void componentActivated() {
        super.componentActivated();
        setKeybindings();
    }

    @Override
    protected void componentOpened() {
        super.componentOpened();
        setKeybindings();
    }

    @Override
    protected void componentShowing() {
        super.componentShowing();
        setKeybindings();
    }

    private void setKeybindings() {
        InputMap im = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = this.getActionMap();

        if (mapComponent != null && mapComponent.getMap2DPanel() != null) {
            GoToAction goToAction = new GoToAction(this.mapComponent.getMap2DPanel());
            //KeyStroke ctrlG = Utilities.stringToKey("D-G");
            //im.put(ctrlG, goToAction.getValue(Action.NAME));
            im.put(GoToAction.ACCELERATOR, goToAction.getValue(Action.NAME));
            am.put(goToAction.getValue(Action.NAME), goToAction);
            
            UpdateMapAction updateMapAction = new UpdateMapAction(mapComponent);
            im.put(UpdateMapAction.ACCELERATOR, updateMapAction.getValue(Action.NAME));
            am.put(updateMapAction.getValue(Action.NAME), updateMapAction);
        }
    }

    private void clearKeybindings() {
        InputMap im = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = this.getActionMap();

        im.clear();
        am.clear();
    }


    
    ////////////////////////////////////////////////////////////////////////////
    //
    //             Undo/Redo support for the undelying Map2DPanel
    //
    ////////////////////////////////////////////////////////////////////////////


    /**
     * Don't use this method, serves internally to track if 2D panel was zoomed manually
     * or via undo/redo buttons.
     * @return
     */
    public boolean isReactingToUndoRedo() {
        return reactingToUndoRedo;
    }
    
    /**
     * Don't use this method, serves internally to track if 2D panel was zoomed manually
     * or via undo/redo buttons.
     * @param reactingToUndoRedo
     */
    public void setReactingToUndoRedo(boolean reactingToUndoRedo) {
        this.reactingToUndoRedo = reactingToUndoRedo;
    }

    @Override
    public UndoRedo getUndoRedo() {
        return undo;
    }

    @Override
    public void handleZoomEvent(ZoomEvent e) {
        if (isReactingToUndoRedo()) {
            return; // if it's just the undo/redo caused zoom event, we don't want to react to it
        }

        if (e.isStart()) {
            undoZoomEvent = new Map2DUndoZoom(mapComponent.getMap2DPanel(), this);
            undoZoomEvent.setLocOld(e.getZoomLevel().getAxes().getMapDimensions());
        } else {
            if (undoZoomEvent == null) {
//                Exceptions.printStackTrace(new IllegalStateException(
//                        "undoZoomEvent was null when adding an evenet to UndoRedo manager."));
                return;
            }
            if (undoZoomEvent.getLocOld() == null) {
//                Exceptions.printStackTrace(new IllegalStateException(
//                        "undoZoomEvent.getLocOld() was null when adding an evenet to UndoRedo manager."));
                return;
            }
            undoZoomEvent.setLocNew(e.getZoomLevel().getAxes().getMapDimensions());
            undo.undoableEditHappened(new UndoableEditEvent(this, undoZoomEvent));
            undoZoomEvent = null;
        }
    }


    /////////////////////////////////////////////////
    //
    //  Netbeans specific section.
    //      writeProperties()/readProperties are required because of @ConvertAsProperties(dtd annotation
    //
    /////////////////////////////////////////////////

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    void writeProperties(Properties p) {

    }

    void readProperties(Properties p) {

    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mapContainer = new javax.swing.JPanel();

        mapContainer.setBackground(new java.awt.Color(255, 204, 204));
        mapContainer.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mapContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mapContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mapContainer;
    // End of variables declaration//GEN-END:variables

}
