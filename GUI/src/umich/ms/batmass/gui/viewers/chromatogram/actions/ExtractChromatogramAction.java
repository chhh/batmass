/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.chromatogram.actions;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Utilities;
import umich.ms.batmass.gui.core.api.util.RequestFocusListener;
import umich.ms.batmass.gui.viewers.chromatogram.components.ChromatogramComponent;
import umich.ms.util.SpectrumUtils;

/**
 *
 * @author Dmitry Avtonomov
 */
public class ExtractChromatogramAction extends AbstractAction {

    private static final String ACTION_NAME = "XIC";
    private static final String SHORT_DESC = "Extract chromatogram";
    public static final String ACTION_ID = "XIC_CHROMATOGRAM_ACTION";
    public static final KeyStroke ACCELERATOR = Utilities.stringToKey("D-X");

    private final WeakReference<ChromatogramComponent> ref;

    public ExtractChromatogramAction(ChromatogramComponent component) {
        super(ACTION_NAME);
        this.ref = new WeakReference<>(component);

        putValue(Action.ACCELERATOR_KEY, ACCELERATOR);

//        ImageIcon icon = ImageUtilities.loadImageIcon(ICON_PATH, false);
//        putValue(Action.LARGE_ICON_KEY, icon);
//        putValue(Action.SMALL_ICON, icon);

        StringBuilder tooltip = new StringBuilder();
        tooltip.append(SHORT_DESC);
        tooltip.append(" (");
        tooltip.append(org.openide.util.Utilities.keyToString(ACCELERATOR));
        tooltip.append(")");
        putValue(Action.SHORT_DESCRIPTION, tooltip.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ChromatogramComponent comp = ref.get();
        if (comp == null || comp.getView() == null) {
            return;
        }

        ChromatogramComponent.Viewport view = comp.getView();

        XICDialog dialog = new XICDialog();
        dialog.getTxtMzLo().addAncestorListener(new RequestFocusListener());
        dialog.initFields(view);

        int result = JOptionPane.showConfirmDialog(
                comp,                               // align the dialog relative to parent spectrum viewer
                dialog,                                    // the panel to show in the dialog
                "Extract chromatogram", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Double mzLo = dialog.getMzLo();
            Double mzHi = dialog.getMzHi();
            Double rtLo = dialog.getRtLo();
            Double rtHi = dialog.getRtHi();
            Double mz = dialog.getMz();
            Double ppm = dialog.getPpm();
            Double deltaMz = dialog.getDeltaMz();

            if (mzLo == null && mzHi == null && mz == null) {
                ChromatogramComponent.Viewport newView = new ChromatogramComponent.Viewport(view.getMsLevel());
                comp.updateChromatogramView(newView);
                return;
            }

            ChromatogramComponent.Viewport newView = view.copy();

            newView.setRtLo(rtLo);
            newView.setRtHi(rtHi);

            if ((mzLo != null && mzHi == null) || (mzLo == null && mzHi != null)) {
                NotifyDescriptor d = new NotifyDescriptor.Message("If you provide m/z values, set both of them");
                DialogDisplayer.getDefault().notify(d);
                return;
            }
            if (mzLo != null && mzHi != null) {
                newView.setMzLo(mzLo);
                newView.setMzHi(mzHi);
                comp.updateChromatogramView(newView);
                return;
            }

            if (mz == null) {
                NotifyDescriptor d = new NotifyDescriptor.Message("You didn't provide an m/z range or a target m/z value with tolerance");
                DialogDisplayer.getDefault().notify(d);
                return;
            }

            if (ppm == null && deltaMz == null) {
                NotifyDescriptor d = new NotifyDescriptor.Message("You provided target m/z, but didn't provide a tolerance");
                DialogDisplayer.getDefault().notify(d);
                return;
            }

            if (ppm != null) {
                mzLo = mz - SpectrumUtils.ppm2amu(mz, ppm);
                mzHi = mz + SpectrumUtils.ppm2amu(mz, ppm);
            }
            if (deltaMz != null) {
                mzLo = mz - deltaMz;
                mzHi = mz + deltaMz;
            }
            newView.setMzLo(mzLo);
            newView.setMzHi(mzHi);
            comp.updateChromatogramView(newView);
        }
    }

}
