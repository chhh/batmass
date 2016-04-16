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
package umich.ms.batmass.gui.viewers.map2d.todelete;

import MSUmpire.PeakDataStructure.PeakCluster;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import umich.ms.datatypes.LCMSData;

/**
 *
 * @author Dmitry Avtonomov
 */
public class ProcessingUmpireFeatures extends MSFileProcessing {
    private final String name = "MS1 Features";
    private ArrayList<PeakCluster> peakClusters;
    private LCMSData data;

    public ProcessingUmpireFeatures(ArrayList<PeakCluster> peakClusters, LCMSData data) {
        this.peakClusters = peakClusters;
        this.data = data;
    }

    @Override
    public String getProcessingName() {
        return name;
    }

    public ArrayList<PeakCluster> getPeakClusters() {
        return peakClusters;
    }

    @Override
    public LCMSData getParentFile() {
        return data;
    }


    /**
     * New method to read features from a serialized file.
     * @param msFile
     * @param pathToSerializedFeatures
     * @throws SQLException
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.util.zip.DataFormatException
     */
    public static void load(LCMSData data, Path pathToSerializedFeatures)
            throws SQLException, IOException, FileNotFoundException, InterruptedException,
            ExecutionException, ParserConfigurationException, SAXException, DataFormatException {

    }

    /**
     * Old method used to read features from a MySQL database.
     * @param msFile
     * @throws SQLException
     */
    @Deprecated
    public static void load(LCMSData data) throws SQLException {
        
    }
}
