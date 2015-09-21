/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.openide.util.Exceptions;

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
    public static XCMSPeaks create(Path path) {
        if (!Files.exists(path))
            throw new IllegalArgumentException("File path for XCMS peaks does not exist.");
        
        XCMSPeaks peaks = new XCMSPeaks();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
            String[] header = {};
            CSVFormat format = CSVFormat.newFormat(',').withHeader().withIgnoreSurroundingSpaces().withAllowMissingColumnNames();
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
                val = r.get("isotopes");
                p.setIsotopes(val);
                val = r.get("adduct");
                p.setAdduct(val);
                val = r.get("pcgroup");
                p.setPcgroup(Integer.parseInt(val));
                peaks.add(p);
            }
            
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return peaks;
    }
}
