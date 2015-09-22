/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.agilent.cef.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import umich.ms.fileio.filetypes.agilent.cef.jaxb.CEF;
import umich.ms.fileio.filetypes.agilent.cef.jaxb.Compound;
import umich.ms.fileio.filetypes.agilent.cef.jaxb.Location;
import umich.ms.fileio.filetypes.agilent.cef.jaxb.P;
import umich.ms.fileio.filetypes.agilent.cef.jaxb.RTRange;
import umich.ms.fileio.filetypes.agilent.cef.jaxb.Spectrum;

/**
 * Factory for features detected by Agilent MassHunter, stored as .cef files.
 * @author Dmitry Avtonomov
 */
public class AgilentCefFile {
    Path path;

    public AgilentCefFile(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public AgilentCompounds create() throws IOException {
        try {
            if (!Files.exists(path))
                throw new IllegalStateException("File path for Agilent .cef does not exist.");
            
            AgilentCompounds comps = new AgilentCompounds();
            // declaring what to parse
            JAXBContext ctx = JAXBContext.newInstance(CEF.class);
            // run the parser
            Unmarshaller unmarshaller  = ctx.createUnmarshaller();
            Object unmarshalled = unmarshaller.unmarshal(path.toFile());
            // use the unmarshalled object
            CEF cef = (CEF) unmarshalled;
            
            List<Compound> compList = cef.getCompoundList().getCompound();
            Location l;
            Spectrum s;
            RTRange r;
            List<P> ps;
            for (Compound c : compList) {
                AgilentCompound ac = new AgilentCompound();
                l = c.getLocation();
                ac.setMass(l.getM());
                ac.setRt(l.getRt());
                ac.setAbMax(l.getY());
                ac.setAbTot(l.getV());
                s = c.getSpectrum();
                r = s.getRTRanges().getRTRange();
                ac.setRtLo(r.getMin());
                ac.setRtHi(r.getMax());
                ps = c.getSpectrum().getMSPeaks().getP();
                for (P p : ps) {
                    AgilentMSPeak peak = new AgilentMSPeak();
                    peak.setMz(p.getX());
                    peak.setRt(p.getRt());
                    peak.setAbMax(p.getY());
                    peak.setAbTot(p.getV());
                    peak.setZ(p.getZ());
                    peak.setIonDescription(p.getS());
                    ac.add(peak);
                }
                comps.add(ac);
            }
            
            AgilentCompounds compsSplitByAdduct = new AgilentCompounds();
            for (AgilentCompound ac : comps.getCompounds()) {
                
            }
            return compsSplitByAdduct;
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
    }
}
