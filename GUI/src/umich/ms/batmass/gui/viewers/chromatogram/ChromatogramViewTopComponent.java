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
package umich.ms.batmass.gui.viewers.chromatogram;

import java.awt.BorderLayout;
import java.nio.file.Paths;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.TopComponent;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.batmass.gui.management.UnloadableLCMSData;
import umich.ms.batmass.gui.viewers.chromatogram.actions.ExtractChromatogramAction;
import umich.ms.batmass.gui.viewers.chromatogram.components.ChromatogramComponent;
import umich.ms.batmass.gui.viewers.util.LCMSDataUtils;
import umich.ms.batmass.nbputils.SwingHelper;
import umich.ms.datatypes.LCMSData;
import umich.ms.datatypes.LCMSDataSubset;
import umich.ms.datatypes.scancollection.IScanCollection;


/**
 *
 * @author Dmitry Avtonomov
 */

@TopComponent.Description(
        preferredID = "ViewScanCollectionTopComponent",
        iconBase=ChromatogramViewTopComponent.ICON_PATH,
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(
        mode = "editor",
        openAtStartup = false)
//@ActionID(
//        category = "Window",
//        id = "umich.ms.batmass.gui.viewers.chromatogram.ChromatogramViewTopComponent")
//@ActionReference(
//        path = "Menu/Window")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ChromatogramViewTopComponent")
@Messages({
    "CTL_ChromatogramViewTopComponent=Chromatogram Viewer"
})
public class ChromatogramViewTopComponent extends BMTopComponent {
    @StaticResource
    public static final String ICON_PATH = "umich/ms/batmass/gui/resources/view_chromatogram_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    protected ChromatogramComponent chromatoramPanel;

    private IScanCollection scans;
    private LCMSData data;
    private final LCMSDataSubset INITIAL_SUBSET = LCMSDataSubset.MS1_WITH_SPECTRA;

    public ChromatogramViewTopComponent() {
        initComponentsManually();
    }


    /**
     * Initiates the component. <b>MUST NOT BE CALLED FROM EDT.</b><br/>
     * @param data if required data wasn't loaded yet, it will be loaded
     */
    public void setData(final LCMSData data) {
        this.data = data;

        // this will set the name of the window before we try to load the data
        final Runnable preDataLoaded = new Runnable() {
            @Override
            public void run() {
                initComponentsManually();
            }
        };

        // when the data is loaded, everything else needs to be run on EDT.
        final Runnable onDataLoaded = new Runnable() {
            @Override
            public void run() {
                scans = data.getScans();
                if (scans == null) {
                    NotifyDescriptor.Message notice = new NotifyDescriptor.Message("There was no appropriate parser for this file to represent it in this view.");
                    DialogDisplayer.getDefault().notify(notice);
                    throw new NullPointerException("ScanCollection could not be obtained from the MSFile");
                }
                initComponentsManually();
                initChromatogramPanel();
                setScansForPanel(scans);
                ChromatogramViewTopComponent.this.makeBusy(false);
                UnloadableLCMSData unloadable = new UnloadableLCMSData(data);
                addToLookup(unloadable);
            }
        };

        SwingHelper.invokeOnEDT(preDataLoaded);
        ChromatogramViewTopComponent.this.makeBusy(true);
        RequestProcessor.Task loadDataTask = LCMSDataUtils
                .loadData(data, INITIAL_SUBSET, ChromatogramViewTopComponent.this, false);
        TaskListener loadDataTaskFinishedListener = new TaskListener() {
            @Override
            public void taskFinished(Task task) {
                SwingHelper.invokeOnEDT(onDataLoaded);
            }
        };

        // If the data was already loaded, thena the returned Task will be null.
        // In this case we can safely trigger taskFinished() manually.
        if (loadDataTask != null) {
            loadDataTask.addTaskListener(loadDataTaskFinishedListener);
        } else {
            loadDataTaskFinishedListener.taskFinished(null);
        }
    }

    /**
     * Expects that the scan collection was already loaded with data.
     */
    private void setScansForPanel(IScanCollection scans) {
        chromatoramPanel.setScanCollection(scans);
        revalidate();
    }

    private void initComponentsManually() {
        removeAll();
        setLayout(new BorderLayout());

        setIcon(ICON.getImage());
        setKeybindings();
        if (data != null) {
            String fileName = Paths.get(data.getSource().getName()).getFileName().toString();
            setDisplayName(fileName);
        }
    }

    private void initChromatogramPanel() {
        chromatoramPanel = new ChromatogramComponent();
        add(chromatoramPanel, BorderLayout.CENTER);
        revalidate();
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

    private void setKeybindings() {
//        InputMap im = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//        ActionMap am = this.getActionMap();

        if (chromatoramPanel == null) {
            return;
        }

        ExtractChromatogramAction xicAction = chromatoramPanel.getExtractChromatogramAction();
        addKeybindingAction(xicAction);

//        im.put(ExtractChromatogramAction.ACCELERATOR, ExtractChromatogramAction.ACTION_ID);
//        am.put(ExtractChromatogramAction.ACTION_ID, xicAction);
    }

    private void addKeybindingAction(Action a) {
        InputMap im = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = this.getActionMap();

        im.put((KeyStroke)a.getValue(Action.ACCELERATOR_KEY), a.getValue(Action.NAME));
        am.put(a.getValue(Action.NAME), a);
    }

    @Override
    protected void componentActivated() {
        setKeybindings();
    }

    @Override
    protected void componentShowing() {
        setKeybindings();
    }

    @Override
    protected void componentOpened() {
        setKeybindings();
    }


}
