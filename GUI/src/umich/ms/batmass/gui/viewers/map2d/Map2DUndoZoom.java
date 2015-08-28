/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.map2d;

import java.lang.ref.WeakReference;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.viewers.map2d.components.Map2DPanel;
import umich.ms.batmass.nbputils.OutputWndPrinter;

/**
 *
 * @author Dmitry Avtonomov
 */
public class Map2DUndoZoom extends AbstractUndoableEdit {

    WeakReference<Map2DPanel> refPanel;
    WeakReference<Map2DTopComponent> refTc;
    MzRtRegion locOld;
    MzRtRegion locNew;

    public Map2DUndoZoom(Map2DPanel mapPanel, Map2DTopComponent mapTc) {
        this.refPanel = new WeakReference<>(mapPanel);
        this.refTc = new WeakReference<>(mapTc);
    }

    public MzRtRegion getLocOld() {
        return locOld;
    }

    public void setLocOld(MzRtRegion locOld) {
        this.locOld = locOld;
    }

    public MzRtRegion getLocNew() {
        return locNew;
    }

    public void setLocNew(MzRtRegion locNew) {
        this.locNew = locNew;
    }

    @Override
    public boolean canRedo() {
        return refPanel.get() != null;
    }

    @Override
    public void redo() throws CannotRedoException {
        //MessageUtil.info("redo() called in Map2DUndoRedo");
        Map2DPanel map = dereference(refPanel, "redo()", "Map2DPanel");
        Map2DTopComponent tc = dereference(refTc, "redo()", "Map2DTopComponent");
        if (map == null || tc == null) {
            return;
        }
        tc.setReactingToUndoRedo(true);
        map.zoom(locNew);
        tc.setReactingToUndoRedo(false);
    }

    @Override
    public boolean canUndo() {
        return refPanel.get() != null;
    }

    @Override
    public void undo() throws CannotUndoException {
        //MessageUtil.info("undo() called in Map2DUndoRedo");
        Map2DPanel map = dereference(refPanel, "undo()", "Map2DPanel");
        Map2DTopComponent tc = dereference(refTc, "undo()", "Map2DTopComponent");
        if (map == null || tc == null) {
            return;
        }
        tc.setReactingToUndoRedo(true);
        map.zoom(locOld);
        tc.setReactingToUndoRedo(false);
    }

    @Override
    public String getRedoPresentationName() {
        return this.locNew.toString();
    }

    @Override
    public String getUndoPresentationName() {
        return this.locOld.toString();
    }


    private <T> T dereference(WeakReference<T> ref, String methodName, String payloadName) {
        T payload = ref.get();
        if (payload == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName());
            sb.append(" ");
            sb.append(methodName);
            sb.append(" was called, but the ");
            sb.append(payloadName);
            sb.append(" weak ref was already null.");
            OutputWndPrinter.printOut("DEBUG", sb.toString());
        }
        return payload;
    }
}
