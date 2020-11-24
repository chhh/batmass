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
package umich.ms.batmass.gui.viewers.map2d.actions;


import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.BaseProgressUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import umich.ms.batmass.gui.management.EBus;
import umich.ms.batmass.gui.viewers.map2d.PassiveOverlayKey;
import static umich.ms.batmass.gui.viewers.map2d.actions.GoToAction.ACCELERATOR;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DComponent;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DPanel;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DPanelOptions;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DZoomLevel;
import umich.ms.batmass.gui.viewers.map2d.messages.MsgPassiveOverlay;
import umich.ms.batmass.gui.viewers.map2d.messages.MsgPassiveOverlayAction;
import umich.ms.batmass.gui.viewers.map2d.noise.AbMzRtTransformNoop;
import umich.ms.batmass.gui.viewers.map2d.noise.DenoiseIsoSpacing;
import umich.ms.batmass.gui.viewers.map2d.noise.DenoiseLongEluting;
import umich.ms.batmass.gui.viewers.map2d.noise.DenoiseMexHat;
import umich.ms.batmass.gui.viewers.map2d.noise.IAbMzRtTransform;
import umich.ms.batmass.nbputils.SwingHelper;
import umich.ms.batmass.nbputils.actions.ActionUtils;
import umich.ms.datatypes.LCMSDataSubset;
import umich.ms.datatypes.scancollection.ScanIndex;
import umich.ms.fileio.exceptions.FileParsingException;
import umich.ms.util.DoubleRange;

/**
 *
 * @author Dmitry Avtonomov <dmitriy.avtonomov@gmail.com>
 */
@Messages({
    "UpdateMapAction.tooltip=Update the map",
    "UpdateMapAction.name=Update"})
public class UpdateMapAction extends AbstractAction {
    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/viewers/map2d/icons/icon_update.png";
    private static final String ACTION_NAME = "Update";
    public static final String ACTION_ID = "UPDATE_MAP_ACTION";
    public static final KeyStroke ACCELERATOR = Utilities.stringToKey("S-U");
    
    protected WeakReference<Map2DComponent> mapComponentRef;

    public UpdateMapAction(Map2DComponent mapComponent) {
        super(ACTION_NAME);
        mapComponentRef = new WeakReference<>(mapComponent);

        ImageIcon icon = ImageUtilities.loadImageIcon(ICON_PATH, false);
        putValue(Action.LARGE_ICON_KEY, icon);
        putValue(Action.SMALL_ICON, icon);
        
        putValue(Action.ACCELERATOR_KEY, ACCELERATOR);

        StringBuilder tooltip = new StringBuilder();
        tooltip.append(NbBundle.getMessage(UpdateMapAction.class, "UpdateMapAction.tooltip")); // NOI18N
        tooltip.append(" (");
        tooltip.append(ActionUtils.fromNbKeyStrokeToHuman(Utilities.keyToString(ACCELERATOR)));
        tooltip.append(")");
        putValue(Action.SHORT_DESCRIPTION, tooltip.toString());
        
    }
    
    public String getName() {
        return NbBundle.getMessage(UpdateMapAction.class, "UpdateMapAction.name"); // NOI18N
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final Map2DComponent mapComponent = mapComponentRef.get();
        if (mapComponent == null) {
            // the MapComponent has been garbage collected already
            return;
        }
        final Map2DPanel mapPanel = mapComponent.getMap2DPanel();
        final Map2DZoomLevel zoomLevelLast = mapPanel.getZoomLevels().getLast();
        if (mapPanel == null) {
            throw new IllegalStateException("Update action could not get a map panel"
                    + " from the map component");
        }

        final Map2DPanelOptions optionsNew = mapComponent.getToolbar().getOptions();
        final Map2DPanelOptions optionsOld = mapPanel.getOptions();
        
        // check if anything has changed
        DiffNode diff = ObjectDifferBuilder.buildDefault().compare(optionsNew, optionsOld);
        if (diff.isUntouched()) {
            return;
        }

        final Runnable postDataLoaded = () -> {
            mapPanel.setOptions(optionsNew);
            mapPanel.resetZoomLevels();
            mapPanel.setDefaultViewport(null);
            mapPanel.initMap();
            mapPanel.zoomOut(true, null, true, true);
            mapPanel.zoom(zoomLevelLast.getAxes().getMapDimensions());
        };
        DiffNode msLevelChange = diff.getChild("msLevel");
        DiffNode mzRangeChange = diff.getChild("mzRange");
        
        ScanIndex scansAtMsLevel = mapPanel.getScans().getMapMsLevel2index().get(optionsNew.getMsLevel());
        IAbMzRtTransform denoiser = null;
        final EBus bus = mapPanel.getBusLocal();
        switch (optionsNew.getDoDenoise()) {
            
            case AbMzRtTransformNoop.NAME:
                PassiveOverlayKey keyClear = new PassiveOverlayKey(null, DenoiseLongEluting.CATEGORY);
                MsgPassiveOverlayAction msgClear = new MsgPassiveOverlayAction(MsgPassiveOverlayAction.Action.CLEAR_CATEGORY, keyClear);
                bus.post(msgClear);
                break;
            
            case DenoiseIsoSpacing.NAME:
                denoiser = DenoiseIsoSpacing.from(scansAtMsLevel.getNum2scan());
                break;
            
            case DenoiseMexHat.NAME:
                DenoiseMexHat mexHat = DenoiseMexHat.from(scansAtMsLevel.getNum2scan());
                denoiser = mexHat;
                bus.post(new MsgPassiveOverlay(MsgPassiveOverlay.Action.ADD, mexHat));
                break;
            
            case DenoiseLongEluting.NAME:
                DenoiseLongEluting longEluting = DenoiseLongEluting.from(scansAtMsLevel.getNum2scan());
                denoiser = longEluting;
                bus.post(new MsgPassiveOverlay(MsgPassiveOverlay.Action.ADD, longEluting));
                break;
        }
        if (denoiser == null) {
            bus.removeStickyEvent(AbMzRtTransformNoop.class);
        } else {
            bus.postSticky(denoiser);
        }
        
        if ((msLevelChange != null && msLevelChange.isChanged())
                || (mzRangeChange != null && mzRangeChange.isChanged())) {

                // if ms level or mz-range has changed, we need to load the new data
            // and unload the old one
            Set<Integer> msLevels = Collections.singleton(optionsNew.getMsLevel());
            List<DoubleRange> mzRanges;
            DoubleRange newMzRange = optionsNew.getMzRange();
            if (newMzRange.equals(Map2DPanel.OPT_DISPLAY_ALL_MZ_REGIONS)) {
                mzRanges = null;
            } else {
                mzRanges = Collections.singletonList(newMzRange);
            }
            final LCMSDataSubset subsetToLoad = new LCMSDataSubset(null, null, msLevels, mzRanges);

            String dialogTitle = "Loading data (Update Map Action)";
            String progressHandleName = "Updating currently loaded spectra";
            final ProgressHandle ph = ProgressHandle.createHandle(progressHandleName);
            final Runnable loadData;
            loadData = () -> {
                try {
                    // unload old data
                    Set<LCMSDataSubset> excludeFutureLoadedSubset = Collections.singleton(subsetToLoad);
                    mapComponent.unlaodFromAll(excludeFutureLoadedSubset);
                    // load new data
                    mapComponent.loadIntoAll(subsetToLoad);
                } catch (FileParsingException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ph.finish();
                }
                
                SwingHelper.invokeOnEDT(postDataLoaded);
            };
            BaseProgressUtils.runOffEventThreadWithProgressDialog(loadData, dialogTitle, ph, false, 0, 300);
            ph.start();

        } else {
            SwingHelper.invokeOnEDT(postDataLoaded);
        }
    }
}
