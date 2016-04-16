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
package umich.ms.batmass.gui.viewers.spectrum.actions;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import umich.ms.batmass.gui.core.api.util.RequestFocusListener;
import umich.ms.batmass.gui.viewers.spectrum.components.SpectraViewerComponent;
import umich.ms.batmass.nbputils.OutputWndPrinter;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scancollection.IScanCollection;

/**
 *
 * @author dmitriya
 */
public class GoToScanAction extends AbstractAction {
    @StaticResource
    final static String ICON_PATH = "umich/ms/batmass/gui/viewers/spectrum/icons/icon_goto_scan.png";
    final static String ACTION_ID = "GOTO_SCAN_1D_ACTION";
    final static KeyStroke ACCELERATOR = Utilities.stringToKey("D-G");

    private final WeakReference<SpectraViewerComponent> viewerRef;

    public GoToScanAction(SpectraViewerComponent viewer) {
        super(null);
        viewerRef = new WeakReference<>(viewer);

        KeyStroke accelKey = ACCELERATOR;
        putValue(Action.ACCELERATOR_KEY, accelKey);
        StringBuilder tooltip = new StringBuilder();
        tooltip.append(NbBundle.getMessage(GoToScanAction.class, "GoToScanAction.tooltip")); // NOI18N)
        tooltip.append(" (");
        tooltip.append(Utilities.keyToString(accelKey));
        tooltip.append(")");
        putValue(Action.SHORT_DESCRIPTION, tooltip.toString());
        ImageIcon icon = ImageUtilities.loadImageIcon(ICON_PATH, false);
        putValue(Action.LARGE_ICON_KEY, icon);
        putValue(Action.SMALL_ICON, icon);
    }

    private SpectraViewerComponent getViewer() {
        return viewerRef.get();
    }

    /**
     * @return the value corresponding to {@link Action#SMALL_ICON} property of Action.
     */
    public static String getIconPath() {
        return ICON_PATH;
    }

    /**
     * @return the value, corresponding to {@link Action#NAME} property of Action.
     */
    public static String getActionID() {
        return ACTION_ID;
    }

    public static KeyStroke getAcceleratorKey() {
        return ACCELERATOR;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        SpectraViewerComponent viewer = getViewer();
        IScanCollection scans = viewer.getScans();

        GoTo1DDialog gtd = new GoTo1DDialog(scans);
        gtd.getFieldScanNum().addAncestorListener(new RequestFocusListener());
        int curScanNum = viewer.getViewport().getScanNum();
        IScan scan = scans.getScanByNum(curScanNum);

        gtd.setDefaultValues(
                scan.getNum(), scan.getRt(),
                viewer.getSpectrumPanel().getXAxisZoomRangeLowerValue(),
                viewer.getSpectrumPanel().getXAxisZoomRangeUpperValue());

        int result = JOptionPane.showConfirmDialog(
                viewer.getSpectrumPanel(), // align the dialog relative to parent spectrum viewer
                gtd,                       // the panel to show in the dialog
                "Go To", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Integer scanNum = gtd.getScanNum();
            Double rt = gtd.getRtInMinutes();
            Double mzStart = gtd.getMzStart();
            Double mzEnd = gtd.getMzEnd();

            IScan nextScan = null;
            if (scanNum != null) {
                nextScan = scans.getScanByNumClosest(scanNum);
                nextScan = findClosestScanNumMatchingMsLevelCriteria(nextScan, null);
            } else if (rt != null) {
                List<IScan> scansByRtClosest = scans.getScansByRtClosest(rt);
                if (!scansByRtClosest.isEmpty()) {
                    Integer msLevel = scan.getMsLevel();
                    IScan closestScan = scansByRtClosest.get(0);
                    if (closestScan == null) {
                        NotifyDescriptor d = new NotifyDescriptor.Message("Something awful happened in GoTo action");
                        DialogDisplayer.getDefault().notify(d);
                        return;
                    }

                    closestScan = findClosestScanNumMatchingMsLevelCriteria(closestScan, rt);
                    nextScan = closestScan;
                }
            } else {
                NotifyDescriptor d = new NotifyDescriptor.Message("Incorrect format for Scan# or RT");
                DialogDisplayer.getDefault().notify(d);
                return;
            }
            if (nextScan == null) {
                OutputWndPrinter.printOut("DEBUG", String.format("GoTo action: could not go to scan: num# %d, rt: %.2f."
                        + " Got null when searching for scans.", scanNum, rt));
                return;
            }
            scanNum = nextScan.getNum();

            viewer.showSpectrum(scanNum);
            if (mzStart != null || mzEnd != null) {
                viewer.zoom(mzStart, mzEnd);
            }
        }
    }

    /**
     *
     * @param closestScan
     * @param rt can be null, then matching will only be done by scanNum
     * @return
     */
    private IScan findClosestScanNumMatchingMsLevelCriteria(IScan closestScan, Double rt) {

        SpectraViewerComponent viewer = getViewer();
        IScanCollection scans = viewer.getScans();
        int msLevelMaxAllowed = viewer.getMsLevelMaxAllowed();

        if (closestScan.getMsLevel() <= msLevelMaxAllowed) {
            return closestScan;
        }
        // if we were unlucky to hit ms scan that is not allowed by MS Level restrictions,
        // need to scan abckwards and forwards until we find something suitable
        int closestScanNum = closestScan.getNum();
        int backScanNum = closestScanNum;
        int fwdScanNum = closestScanNum + 1;
        IScan backScan, fwdScan;
        do {
            backScan = scans.getScanByNumLower(backScanNum);
            if (backScan != null && backScan.getMsLevel() <= msLevelMaxAllowed) {
                break;
            }
            backScanNum--;
        } while (backScan != null);
        do {
            fwdScan = scans.getScanByNumUpper(fwdScanNum);
            if (fwdScan != null && fwdScan.getMsLevel() <= msLevelMaxAllowed) {
                break;
            }
            fwdScanNum++;
        } while (fwdScan != null);
        if (fwdScan != null && backScan != null) {
            if (rt != null && (Math.abs(fwdScan.getRt() - rt) < Math.abs(backScan.getRt() - rt))) {
                closestScan = fwdScan;
            } else if (rt == null &&
                    (Math.abs(fwdScan.getNum() - closestScan.getNum()) < Math.abs(backScan.getNum() - closestScan.getNum()))) {
                closestScan = fwdScan;
            } else {
                closestScan = backScan;
            }
        } else if (fwdScan != null) {
            closestScan = fwdScan;
        } else if (backScan != null) {
            closestScan = backScan;
        }

        return closestScan;
    }
}
