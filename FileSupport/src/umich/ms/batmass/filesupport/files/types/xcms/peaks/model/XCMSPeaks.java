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
package umich.ms.batmass.filesupport.files.types.xcms.peaks.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

/**
 *
 * @author Dmitry Avtonomov
 */
public class XCMSPeaks {
    protected List<XCMSPeak> peaks;

    public XCMSPeaks() {
        peaks = new ArrayList<>();
    }

    public List<XCMSPeak> getPeaks() {
        return peaks;
    }

    public boolean add(XCMSPeak e) {
        return peaks.add(e);
    }

    public void add(int index, XCMSPeak element) {
        peaks.add(index, element);
    }

    public XCMSPeak get(int index) {
        return peaks.get(index);
    }

    public int size() {
        return peaks.size();
    }

    public Iterator<XCMSPeak> iterator() {
        return peaks.iterator();
    }

    /**
     * Parse XCMS peaks from the file, which you can create from R after running
     * XCMS feature finding. <br/>
     * Example:<br/>
     * {@code xs <- xcmsSet(files = file_mzml, method = "massifquant", prefilter = c(1, 10000), peakwidth = c(5, 500), ppm = 20, criticalValue = 1.0, consecMissedLimit = 0, withWave = 0, nSlaves=4)} <br/>
     * {@code peaks <- ixs@peaks[1:7108,1:9]} <br/>
     * {@code write.table(peaks, sep = "\t",file = "D:/projects/XCMS/peaks.xcms.csv")}
     * @param path
     * @return
     */
    public static XCMSPeaks create(Path path) throws IOException {
        if (!Files.exists(path))
            throw new IllegalArgumentException("File path for XCMS peaks does not exist.");

        XCMSPeaks peaks = new XCMSPeaks();
        BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
        String[] header = {};
        CSVFormat format = CSVFormat.newFormat(',')
                .withHeader()
                .withIgnoreSurroundingSpaces()
                .withAllowMissingColumnNames()
                .withQuoteMode(QuoteMode.NON_NUMERIC)
                .withQuote('"');
        CSVParser parser = new CSVParser(reader, format);
        String val;
        for (final CSVRecord r : parser) {
            XCMSPeak p = new XCMSPeak();
            val = r.get(0);
            p.setRowNum(Integer.parseInt(val));
            val = r.get("mz");
            p.setMz(Double.parseDouble(val));
            val = r.get("mzmin");
            p.setMzMin(Double.parseDouble(val));
            val = r.get("mzmax");
            p.setMzMax(Double.parseDouble(val));
            val = r.get("rt");
            p.setRt(Double.parseDouble(val));
            val = r.get("rtmin");
            p.setRtMin(Double.parseDouble(val));
            val = r.get("rtmax");
            p.setRtMax(Double.parseDouble(val));
            val = r.get("into");
            p.setInto(Double.parseDouble(val));
            val = r.get("maxo");
            p.setMaxo(Double.parseDouble(val));
            val = r.get("sample");
            p.setSample(val);
            
            // these are optional and are only added by further R package 
            // called 'CAMERA' processing
            try {
                val = getRecordValueForColName(r, "isotopes");
                p.setIsotopes(val);
            } catch (IllegalArgumentException e) {
            }
            try {
                val = r.get("adduct");
                p.setAdduct(val);
            } catch (IllegalArgumentException e) {
                p.setAdduct("");
            }
            try {
                val = r.get("pcgroup");
                p.setPcgroup(Integer.parseInt(val));
            } catch (IllegalArgumentException e) {
            }
            
            peaks.add(p);
        }
        
        return peaks;
    }
    
    private static String getRecordValueForColName(CSVRecord r, String colName) {
        try {
            return r.get(colName);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return null;
        }
    }
}
