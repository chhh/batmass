/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.viewers.spectrum;

import java.awt.BorderLayout;
import java.nio.file.Paths;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.TopComponent;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.batmass.gui.management.UnloadableLCMSData;
import umich.ms.batmass.gui.viewers.spectrum.components.SpectraViewerComponent;
import umich.ms.batmass.gui.viewers.util.LCMSDataUtils;
import umich.ms.batmass.nbputils.SwingHelper;
import umich.ms.datatypes.LCMSData;
import umich.ms.datatypes.LCMSDataSubset;
import umich.ms.datatypes.scancollection.IScanCollection;


/**
 *
 * @author dmitriya
 */
@TopComponent.Description(
        preferredID = "ViewSpectraTopComponent",
        iconBase=SpectraViewerTopComponent.ICON_PATH,
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(
        mode = "editor",
        openAtStartup = false)
//@ActionID(
//        category = "Window",
//        id = "umich.gui.viewers.SpectraViewerTopComponent")
//@ActionReference(
//        path = "Menu/Window")
@TopComponent.OpenActionRegistration(
        displayName = "#SpectraViewerTopComponent")
@NbBundle.Messages({
    "SpectraViewerTopComponent=Spectrum Viewer 2.0"
})
public class SpectraViewerTopComponent extends BMTopComponent {
    @StaticResource
    public static final String ICON_PATH = "umich/ms/batmass/gui/resources/view_spectrum_16.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    private IScanCollection scans;
    private boolean scansAutoloadOriginal;
    private LCMSData data;
    private SpectraViewerComponent spectraViewer;
    

    protected static final LCMSDataSubset INITIAL_SUBSET = LCMSDataSubset.STRUCTURE_ONLY;

    /**
     * Creates new form ViewSpectrumTopComponent
     */
    public SpectraViewerTopComponent() {
        super();
        initComponentsManually();
    }


    public void setData(final LCMSData data) {
        if (this.data != null) {
            // if the data is being changed to a new one
            this.data.getScans().isAutoloadSpectra(scansAutoloadOriginal);
        }

        this.data = data;
        scansAutoloadOriginal = data.getScans().isAutoloadSpectra();
        data.getScans().isAutoloadSpectra(true);

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
                initSpectrumPanel(scans);
                SpectraViewerTopComponent.this.makeBusy(false);
                UnloadableLCMSData unloadable = new UnloadableLCMSData(data);
                addToLookup(unloadable);
                requestActive();
            }
        };

        SwingHelper.invokeOnEDT(preDataLoaded);
        SpectraViewerTopComponent.this.makeBusy(true);
        RequestProcessor.Task loadDataTask = LCMSDataUtils
                .loadData(data, INITIAL_SUBSET, SpectraViewerTopComponent.this, false);
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

    private void initComponentsManually() {
        removeAll();
        setLayout(new BorderLayout());
        setFocusable(true);

        setIcon(ICON.getImage());
        setKeybindings();
        if (data != null) {
            String fileName = Paths.get(data.getSource().getName()).getFileName().toString();
            setDisplayName(fileName);
        }
    }

    private void initSpectrumPanel(IScanCollection scans) {
        spectraViewer = new SpectraViewerComponent(scans);
        add(spectraViewer, BorderLayout.CENTER);
        revalidate();
    }

    private void setKeybindings() {
        if (spectraViewer != null) {
            InputMap im = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            ActionMap am = this.getActionMap();
            im.setParent(spectraViewer.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT));
            am.setParent(spectraViewer.getActionMap());
        }
    }

//    public boolean overlayFeatures(ProcessingUmpireFeatures puf) {
//        if (scans == null)
//            throw new IllegalStateException("You must set the ScanCollection first using .setScanCollection() method");
//        if (spectraViewer == null)
//            return false;
//
//        // get the clusters and convert their RTs to scan numbers
//        // also sort them by scan numbers and then by mass
//        ArrayList<PeakCluster> peakClusters = puf.getPeakClusters();
//        ArrayList<PeakClusterContainer> clusters = new ArrayList<>();
//        float startRt, endRt;
//        int startScanNum, endScanNum;
//        for (PeakCluster pc : peakClusters) {
//            startRt = pc.startRT;
//            endRt = pc.endRT;
//
//            startScanNum = Integer.MIN_VALUE;
//            List<IScan> scansByRtClosest = scans.getScansByRtClosest((double)startRt);
//            if (scansByRtClosest != null && !scansByRtClosest.isEmpty()) {
//                startScanNum = scansByRtClosest.get(0).getNum();
//            } else {
//                OutputWndPrinter.printErr("DEBUG",
//                        String.format("no matching scans were found for PeakCluster[%d] start rt: %.4f",
//                        pc.Index, pc.startRT));
//            }
//            endScanNum = Integer.MIN_VALUE;
//            scansByRtClosest = scans.getScansByRtClosest((double)endRt);
//            if (scansByRtClosest != null && !scansByRtClosest.isEmpty()) {
//                endScanNum = scansByRtClosest.get(0).getNum();
//            } else {
//                OutputWndPrinter.printErr("DEBUG",
//                        String.format("no matching scans were found for PeakCluster[%d] end rt: %.4f",
//                        pc.Index, pc.endRT));
//            }
//            if (startScanNum != Integer.MIN_VALUE && endScanNum != Integer.MIN_VALUE) {
//                clusters.add(new PeakClusterContainer(startScanNum, endScanNum, pc));
//            } else {
//                OutputWndPrinter.printErr("DEBUG",
//                        String.format("start scan num / end scan num were INt.MIN_VAL for PeakCluster[%d]",
//                        pc.Index));
//            }
//        }
//        Collections.sort(clusters, new Comparator<PeakClusterContainer>() {
//            @Override
//            public int compare(PeakClusterContainer o1, PeakClusterContainer o2) {
//                return Integer.compare(o1.startScanNum, o2.startScanNum);
//            }
//        });
//        Collections.sort(clusters, new Comparator<PeakClusterContainer>() {
//            @Override
//            public int compare(PeakClusterContainer o1, PeakClusterContainer o2) {
//                return Double.compare(o1.peakCluster.mz[0], o2.peakCluster.mz[0]);
//            }
//        });
//
//
//        spectraViewer.setClusters(clusters);
//        spectraViewer.redisplayCurrentSpectrum();
//        return true;
//    }

//    public static class PeakClusterContainer {
//        public int startScanNum;
//        public int endScanNum;
//        public PeakCluster peakCluster;
//
//        public PeakClusterContainer(int startScanNum, int endScanNum, PeakCluster peakCluster) {
//            this.startScanNum = startScanNum;
//            this.endScanNum = endScanNum;
//            this.peakCluster = peakCluster;
//        }
//    }

    // These are some useful methods, but I now find that I don't need to rebind
    // keys all the time.
    // Keeping it here for educational purposes.

    @Override
    protected void componentActivated() {
        super.componentActivated();
        if (data != null)
            setKeybindings();
    }

    @Override
    protected void componentOpened() {
        super.componentOpened();
        if (data != null)
            setKeybindings();
    }

    @Override
    protected void componentShowing() {
        super.componentShowing();
        if (data != null)
            setKeybindings();
    }

    @Override
    protected void componentClosed() {
        super.componentClosed();
        // restore spectra-autoloading feature
        if (data != null && data.getScans() != null) {
            data.getScans().isAutoloadSpectra(scansAutoloadOriginal);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
