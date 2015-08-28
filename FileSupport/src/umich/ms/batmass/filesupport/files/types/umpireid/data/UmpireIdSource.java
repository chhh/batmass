/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpireid.data;

import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpireId;
import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpireIds;
import MSUmpire.DIA.DIAPack;
import MSUmpire.PSMDataStructure.PepIonID;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.data.core.api.DefaultDataSource;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireIdSource extends DefaultDataSource<UmpireIds> {

    public UmpireIdSource(URI origin) {
        super(origin);
    }

    @Override
    public UmpireIds load() throws DataLoadingException {

        Path path = Paths.get(uri);
        path = path.toAbsolutePath();
        String baseName = path.getFileName().toString().replaceFirst("_LCMSID.serFS", "");
        Path mzxmlPath = Paths.get(path.getParent().toString(), baseName + ".mzxml");

        boolean exists = Files.exists(mzxmlPath);
        if (!exists) {
            throw new DataLoadingException(String.format(
                    "UmpireIdSource can't load data, mzXML does not exist in the same folder as the %s (%s)",
                    path.getFileName().toString(), path.getParent().toString()));
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


            // reading identifications
            diaPack.ReadSerializedLCMSID();
            if (diaPack.IDsummary != null) {

                //OutputWndPrinter.printOut("DEBUG", "ID SUMMARY WAS NOT NULL");
                int size = diaPack.IDsummary.GetPepIonList().size();
                UmpireId[] ids = new UmpireId[size];
                int i = 0;
                for (PepIonID pepIonID : diaPack.IDsummary.GetPepIonList().values()) {
                    ids[i] = UmpireId.create(pepIonID);
//                    ArrayList<PSM> psms = pepIonID.GetPSMList();
//                    if (psms.size() > 1) {
//                        OutputWndPrinter.printOut("DEBUG", String.format("PepIonID index#%d had more than 1 PSM", pepIonID.Index));
//                    }
//                    int b = 1;
//
//                    for (PeakCluster peakcluster : pepIonID.MS1PeakClusters) {
//                        a = 1;
//                    }
//                    for (PeakCluster peakcluster : pepIonID.MS2UnfragPeakClusters) {
//                        a = 1;
//                    }
//                    for (FragmentPeak fragment : pepIonID.FragmentPeaks) {
//                        a = 1;
//                    }
                    i++;
                }
                return new UmpireIds(ids);
            } else {
                throw new DataLoadingException("ID Summary was null");
            }
        } catch (IOException | InterruptedException | ExecutionException | ParserConfigurationException | SAXException | DataFormatException | SQLException ex) {
            throw new DataLoadingException(ex);
        } catch (Exception ex) {
            throw new DataLoadingException(ex);
        }
    }
}
