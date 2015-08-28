/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import MSUmpire.DIA.DIAPack;
import MSUmpire.LCMSBaseStructure.LCMSPeakDIAMS2;
import MSUmpire.PSMDataStructure.FragmentPeak;
import MSUmpire.PSMDataStructure.PSM;
import MSUmpire.PSMDataStructure.PepIonID;
import MSUmpire.PeakDataStructure.PeakCluster;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.util.Exceptions;
import org.xml.sax.SAXException;
import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.nbputils.OutputWndPrinter;
import umich.ms.util.DoubleRange;

/**
 * Parser for DIA-Umpire features.
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesSource implements DataSource<Features<UmpireFeature>> {
    /** Path to the file/directory with features. */
    private final URI uri;
    private final Path path;

    public UmpireFeaturesSource(URI uri) {
        this.uri = uri;
        path = Paths.get(uri);
    }

    @Override
    public Features<UmpireFeature> load() {
        Features<UmpireFeature> features = new Features<>();

        String baseName = path.toAbsolutePath().getFileName().toString().replaceFirst("_Peak$", "");
        Path mzxmlPath = Paths.get(path.toAbsolutePath().getParent().toString(), baseName + ".mzxml");
        boolean exists = Files.exists(mzxmlPath);
        if (!exists) {
            Exceptions.printStackTrace(new IllegalStateException("mzxml does not exist in the same folder as the seriazlized files folder."));
            return features;
        }


        int numThreads = Runtime.getRuntime().availableProcessors();
        numThreads = numThreads > 2 ? numThreads - 1 : 1;
        // read peak clusters
        DIAPack diaPack = null;
        try {
            // preparations
            diaPack = new DIAPack(mzxmlPath.toAbsolutePath().toString(), numThreads);
            diaPack.LoadDIASetting();
            diaPack.LoadParams();
            diaPack.BuildStructure();


            

            // reading MS1 peak clusters
            diaPack.ms1lcms.ReadPeakCluster();
            // adding MS1 peak clusters to the Features data structure
            if (diaPack.ms1lcms.PeakClusters.size() > 0) {
                ArrayList<UmpireFeature> ms1Feats = new ArrayList<>(diaPack.ms1lcms.PeakClusters.size());
                for (PeakCluster pc : diaPack.ms1lcms.PeakClusters) {
                    UmpireFeature uf = UmpireFeature.create(pc);
                    ms1Feats.add(uf);
                }
                features.addAll(ms1Feats, 1, null);
            } else {
                Exceptions.printStackTrace(new IllegalStateException(
                        "DIA Umpire MS1 feature list size is 0."));
            }
            
            
            
            // reading identifications, if any
            diaPack.ReadSerializedLCMSID();
            if (diaPack.IDsummary != null) {

                OutputWndPrinter.printOut("DEBUG", "ID SUMMARY WAS NOT NULL");

                int a;
                for (PepIonID pepIonID : diaPack.IDsummary.GetPepIonList().values()) {

                    ArrayList<PSM> psms = pepIonID.GetPSMList();
                    if (psms.size() > 1) {
                        OutputWndPrinter.printOut("DEBUG", String.format("PepIonID index#%d had more than 1 PSM", pepIonID.Index));
                    }
                    int b = 1;

                    for (PeakCluster peakcluster : pepIonID.MS1PeakClusters) {
                        a = 1;
                    }
                    for (PeakCluster peakcluster : pepIonID.MS2UnfragPeakClusters) {
                        a = 1;
                    }
                    for (FragmentPeak fragment : pepIonID.FragmentPeaks) {
                        a = 1;
                    }
                }

            } else {
                OutputWndPrinter.printErr("DEBUG", "ID SUMMARY WAS NULL");
            }
            

            
            // read unfragmented precursors
            if (diaPack.DIAWindows != null) {
                for (LCMSPeakDIAMS2 diaWindow : diaPack.DIAWindows) {
                    diaWindow.ReadPeakCluster();

                    if (diaWindow.PeakClusters.size() > 0) {
                        ArrayList<UmpireFeature> ms2Feats = new ArrayList<>(diaWindow.PeakClusters.size());
                        for (PeakCluster pc : diaWindow.PeakClusters) {
                            UmpireFeature uf = UmpireFeature.create(pc);
                            ms2Feats.add(uf);
                        }
                        DoubleRange range = new DoubleRange((double)diaWindow.DIA_MZ_Range.getX(), (double)diaWindow.DIA_MZ_Range.getY());
                        features.addAll(ms2Feats, 2, range);
                    }
                }
            }

        } catch (IOException | InterruptedException | ExecutionException | ParserConfigurationException | SAXException | DataFormatException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return features;
    }

    @Override
    public URI getOriginURI() {
        return uri;
    }
    
}
