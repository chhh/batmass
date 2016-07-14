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
package umich.ms.batmass.diaumpire.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import umich.ms.batmass.filesupport.core.util.DelimitedFiles;
import umich.ms.batmass.nbputils.OutputWndPrinter;

/**
 * A simple array-list storage for parsed LCMS features from Umpire PeakCluster csv files.
 * @author Dmitry Avtonomov
 */
public class UmpireSeIsoClusters {
    public static String COL_NAME_RT_LO = "StartRT";
    public static String COL_NAME_RT_HI = "EndRT";
    public static String COL_NAME_SCAN_NUM_LO = "StartScan";
    public static String COL_NAME_SCAN_NUM_HI = "EndScan";
    public static String COL_NAME_CHARGE = "Charge";
    public static String COL_NAME_MZ1 = "mz1";
    public static String COL_NAME_MZ2 = "mz2";
    public static String COL_NAME_MZ3 = "mz3";
    public static String COL_NAME_MZ4 = "mz4";
    public static String COL_NAME_PEAK_HEIGHT = "PeakHeight1";
    public static String COL_NAME_PEAK_AREA = "PeakArea1";
    
    protected List<UmpireSeIsoCluster> clusters;

    public UmpireSeIsoClusters() {
        clusters = new ArrayList<>();
    }

    public List<UmpireSeIsoCluster> getClusters() {
        return clusters;
    }
    
    /**
     * Factory method to create UmpireIsoClusters object from a file.
     * @param path the file to parse data from
     * @return 
     */
    public static UmpireSeIsoClusters create(Path path) throws IOException {
        if (!Files.exists(path))
            throw new IllegalArgumentException("File path for Umpire-SE file does not exist.");

        // first read the header to figure out which column indexes we need
        String[] headers = null;
        try (InputStream is = new FileInputStream(path.toFile())) {
            headers = DelimitedFiles.readDelimitedHeader(is, ',');
        }
        int[] colMapping = new int[headers.length];
        Arrays.fill(colMapping, -1);
        findHeaderIndex(headers, COL_NAME_RT_LO, colMapping, 0);
        findHeaderIndex(headers, COL_NAME_RT_HI, colMapping, 1);
        findHeaderIndex(headers, COL_NAME_SCAN_NUM_LO, colMapping, 2);
        findHeaderIndex(headers, COL_NAME_SCAN_NUM_HI, colMapping, 3);
        findHeaderIndex(headers, COL_NAME_CHARGE, colMapping, 4);
        findHeaderIndex(headers, COL_NAME_MZ1, colMapping, 5);
        findHeaderIndex(headers, COL_NAME_MZ2, colMapping, 6);
        findHeaderIndex(headers, COL_NAME_MZ3, colMapping, 7);
        findHeaderIndex(headers, COL_NAME_MZ4, colMapping, 8);
        findHeaderIndex(headers, COL_NAME_PEAK_HEIGHT, colMapping, 9);
        findHeaderIndex(headers, COL_NAME_PEAK_AREA, colMapping, 10);
        //UmpireSeNumberParser parser = new UmpireSeNumberParser(colMapping);
        UmpireSeSlowNumberParser parser = new UmpireSeSlowNumberParser(colMapping);
        
        UmpireSeIsoClusters result = new UmpireSeIsoClusters();
        List<UmpireSeIsoCluster> clusters = result.getClusters();
        int cnt = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line = br.readLine(); // skip the first line
            while ((line = br.readLine()) != null) {
                //DelimitedFiles.readLineOfNumbers(line, ',', '.', parser);
                DelimitedFiles.readLineOfNumbersSlow(line, ',', parser);
                UmpireSeIsoCluster p = parser.cluster;
                cnt++;
                for (int i = 0; i < p.mz.length; i++) {
                    if (p.mz[i] <= 0)
                        continue;
                    UmpireSeIsoCluster c = new UmpireSeIsoCluster();
                    c.charge = p.charge;
                    c.mz[0] = p.mz[i];
                    c.peakArea = p.peakArea;
                    c.peakHeight = p.peakHeight;
                    c.rtHi = p.rtHi;
                    c.rtLo = p.rtLo;
                    c.scanNumHi = p.scanNumHi;
                    c.scanNumLo = p.scanNumLo;
                    clusters.add(c);
                }
            }
        }
        
        OutputWndPrinter.printOut("DIA-Umpire", 
                String.format("DIA-Umpire feature loader, loaded %d LCMS features as single traces "
                        + "from %d original isotopic clusters.", result.clusters.size(), cnt));
        return result;
    }
    
    /**
     * Find the index of a string within an array of strings.
     * @param headers
     * @param header
     * @return -1 if the header was not found in the array of headers
     */
    private static void findHeaderIndex(String[] headers, String header, int[] colMapping, int map) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(header)) {
                colMapping[i] = map;
                return;
            }
        }
        throw new IllegalStateException(String.format(
                "Header '%s' not found among column headers of DIA Umpire PeakCluster file.", 
                header));
    }
}
