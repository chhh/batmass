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

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.api.util.RequestFocusListener;
import umich.ms.batmass.gui.viewers.map2d.components.BaseMap2D;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DPanel;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DZoomLevel;
import umich.ms.batmass.nbputils.actions.ActionUtils;


/**
 *
 * @author Dmitry Avtonomov
 */
public class GoToAction extends AbstractAction {
    private final WeakReference<Map2DPanel> mapPanelRef;

    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/viewers/map2d/icons/icon_zoom.png";
    private static final String ACTION_NAME = "Go To";
    private static final String SHORT_DESC = "Zoom into a region";
    public static final String ACTION_ID = "GOTO_2DMAP_ACTION";
    public static final KeyStroke ACCELERATOR = Utilities.stringToKey("D-G");

    public GoToAction(Map2DPanel mapPanel) {
        super(ACTION_NAME);
        mapPanelRef = new WeakReference<>(mapPanel);
        
        putValue(Action.ACCELERATOR_KEY, ACCELERATOR);
      
        ImageIcon icon = ImageUtilities.loadImageIcon(ICON_PATH, false);
        putValue(Action.LARGE_ICON_KEY, icon);
        putValue(Action.SMALL_ICON, icon);

        StringBuilder tooltip = new StringBuilder();
        tooltip.append(SHORT_DESC);
        tooltip.append(" (");
        tooltip.append(ActionUtils.fromNbKeyStrokeToHuman(Utilities.keyToString(ACCELERATOR)));
        tooltip.append(")");
        putValue(Action.SHORT_DESCRIPTION, tooltip.toString());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Map2DPanel mapPanel = mapPanelRef.get();
        if (mapPanel == null) {
            Exceptions.printStackTrace(new IllegalStateException("Map panel was null, when Go To (2D) action was triggered."));
            return;
        }
        GoTo2DDialog gtd = new GoTo2DDialog();
        gtd.getFieldMzStart().addAncestorListener(new RequestFocusListener());  // will set focus on MZ Start field
        Map2DZoomLevel zoomLvl0 = mapPanel.getZoomLevels().getFirst();
        BaseMap2D baseMap0 = zoomLvl0.getBaseMap();
        gtd.setAvailableRanges(baseMap0.getMzStart(), baseMap0.getMzEnd(), baseMap0.getRtStart(), baseMap0.getRtEnd());
        Map2DZoomLevel zoomLvlCur = mapPanel.getCurrentZoomLevel();
        BaseMap2D baseMapCur = zoomLvlCur.getBaseMap();
        gtd.setDefaultValues(baseMapCur.getMzStart(), baseMapCur.getMzEnd(), baseMapCur.getRtStart(), baseMapCur.getRtEnd());

        int result = JOptionPane.showConfirmDialog(
                mapPanel,                               // align the dialog relative to parent spectrum viewer
                gtd,                                    // the panel to show in the dialog
                "Go To", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Double mzStart = gtd.getMzStart();
            Double mzEnd = gtd.getMzEnd();
            Double rtStart = gtd.getRtStart();
            Double rtEnd = gtd.getRtEnd();
            if (mzStart != null && mzEnd != null && rtStart != null && rtEnd != null) {

                // Validating M/Z
                if (mzStart < baseMap0.getMzStart())
                    mzStart = baseMap0.getMzStart();
                if (mzStart >= baseMap0.getMzEnd()) {
                    NotifyDescriptor d = new NotifyDescriptor.Message("Low m/z must be smaller than max m/z in map");
                    DialogDisplayer.getDefault().notify(d);
                    return;
                }
                if (mzEnd > baseMap0.getMzEnd())
                    mzEnd = baseMap0.getMzEnd();
                if (mzEnd <= baseMap0.getMzStart()) {
                    NotifyDescriptor d = new NotifyDescriptor.Message("High m/z must be larger than min m/z in map");
                    DialogDisplayer.getDefault().notify(d);
                    return;
                }
                if (mzStart >= mzEnd) {
                    NotifyDescriptor d = new NotifyDescriptor.Message("Low m/z must be smaller than High m/z");
                    DialogDisplayer.getDefault().notify(d);
                    return;
                }


                // Validating RT
                if (rtStart < baseMap0.getRtStart())
                    rtStart = baseMap0.getRtStart();
                if (rtStart >= baseMap0.getRtEnd()) {
                    NotifyDescriptor d = new NotifyDescriptor.Message("Low RT must be smaller than the end of run");
                    DialogDisplayer.getDefault().notify(d);
                    return;
                }
                if (rtEnd > baseMap0.getRtEnd())
                    rtEnd = baseMap0.getRtEnd();
                if (rtEnd < baseMap0.getRtStart()) {
                    NotifyDescriptor d = new NotifyDescriptor.Message("High RT must be larger than the start of the run");
                    DialogDisplayer.getDefault().notify(d);
                    return;
                }
                if (rtStart >= rtEnd) {
                    NotifyDescriptor d = new NotifyDescriptor.Message("Low RT must be smaller than High RT");
                    DialogDisplayer.getDefault().notify(d);
                    return;
                }

                MzRtRegion mzRtInterval = new MzRtRegion(mzStart, mzEnd, rtStart, rtEnd);
                mapPanel.zoom(mzRtInterval);
            } else {
                NotifyDescriptor d = new NotifyDescriptor.Message("Incorrect format for Scan# or RT\nAll fields are required");
                DialogDisplayer.getDefault().notify(d);
            }
        }
    }
}
