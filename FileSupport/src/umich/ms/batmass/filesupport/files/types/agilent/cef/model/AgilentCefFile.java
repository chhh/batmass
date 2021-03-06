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
package umich.ms.batmass.filesupport.files.types.agilent.cef.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
    public static String GRP_MOL_IDENTITY = "grp_id"; 
    public static String GRP_M_COUNT = "grp_m_cnt";
    public static String GRP_Z_COUNT = "grp_z_cnt";
    public static String GRP_Z_CARRIER = "grp_z_crr";
    public static String GRP_ADDUCT = "grp_add";
    public static String GRP_ISOTOPE_NUM = "grp_iso_n";

//    public static Pattern RE_PEAK_DESCRIPTION = Pattern.compile(String.format(
//            "(?<%1$s>(?<%2$s>\\d*?M\\+(?<%3$s>\\d*?)(?<%4$s>[a-zA-Z\\d]+?)\\+?(?<%5s>\\[[^\\]]+?\\])\\+?(?<%6$s>\\d*)))",
//            GRP_MOL_IDENTITY, GRP_M_COUNT, GRP_Z_COUNT, GRP_Z_CARRIER, GRP_ADDUCT, GRP_ISOTOPE_NUM));

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

            List<Compound> compounds = cef.getCompoundList().getCompound();
            for (Compound c : compounds) {
                AgilentCompound ac = new AgilentCompound();
                Location l = c.getLocation();
                ac.setMass(l.getM());
                ac.setRt(l.getRt());
                ac.setAbMax(l.getY());
                if (l.getV() != null)
                    ac.setAbTot(l.getV());
                else if (l.getA() != null)
                    ac.setAbTot(l.getA());
                else
                    ac.setAbTot(0);
                Spectrum s = c.getSpectrum();
                if (s.getRTRanges() != null && s.getRTRanges().getRTRange() != null) {
                    RTRange r = s.getRTRanges().getRTRange();
                    ac.setRtLo(r.getMin());
                    ac.setRtHi(r.getMax());
                } else {
                    ac.setRtLo(l.getRt());
                    ac.setRtHi(l.getRt());
                }
                int chargeSign = +1; // default assumption
                if (s.getMSDetails() != null && s.getMSDetails().getP() != null) {
                    String polarity = s.getMSDetails().getP();
                    if ("-".equals(polarity.trim())) {
                        chargeSign = -1;
                    }
                }
                
                List<P> ps = c.getSpectrum().getMSPeaks().getP();
                for (P p : ps) {
                    AgilentMSPeak peak = new AgilentMSPeak();
                    peak.setMz(p.getX());
                    if (p.getRt() != null) {
                        peak.setRt(p.getRt());
                    } else {
                        peak.setRt(l.getRt());
                    }
                    peak.setAbMax(p.getY());
                    if (p.getV() != null) {
                        peak.setAbTot(p.getV());
                    } else {
                        peak.setAbTot(p.getY());
                    }
                    peak.setZ(p.getZ() * chargeSign);
                    peak.setIonDescription(p.getS());
                    ac.add(peak);
                }
                comps.add(ac);
            }

            return comps;
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
    }
}
