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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All compounds from an Agilent .cef file.
 * @author Dmitry Avtonomov
 */
public class AgilentCompounds {
    List<AgilentCompound> compounds;

    public AgilentCompounds() {
        compounds = new ArrayList<>();
    }

    public AgilentCompounds(int startSize) {
        compounds = new ArrayList<>(startSize);
    }

    public List<AgilentCompound> getCompounds() {
        return compounds;
    }

    public int size() {
        return compounds.size();
    }

    public boolean isEmpty() {
        return compounds.isEmpty();
    }

    public boolean add(AgilentCompound e) {
        return compounds.add(e);
    }

    public void splitCompoundsByAdduct() {
        if (compounds.isEmpty())
            return;

        // maps from molecular signature string to the peak
        // signature like: {2M+3Na+[-H2O]}+4
        // means: dimer, positively charged with 3 'Na' ions, with a loss of water, 4th isotopic peak.
        // the thing in curly braces is the unuique identifier for the ion, that we're putting to the map
        HashMap<String, List<AgilentMSPeak>> map;
        List<AgilentCompound> tmp = new ArrayList<>(compounds.size());
        for (AgilentCompound c : compounds) {
            map = new HashMap<>();
            for(AgilentMSPeak p : c.getPeaks()) {
                IonId ionId = p.parseIonSignature();
                if (ionId == null)
                    throw new IllegalStateException("Ion signature did not match the regexp in AgilentMSPeak");
                p.setIonId(ionId);
                String id = ionId.getMolId();
                List<AgilentMSPeak> msPeaks = map.get(id);
                if (msPeaks == null) {
                    msPeaks = new ArrayList<>();
                    map.put(id, msPeaks);
                } else {
                    int a = 1;
                }
                msPeaks.add(p);
            }
            Set<Map.Entry<String, List<AgilentMSPeak>>> entries = map.entrySet();
            for (Map.Entry<String, List<AgilentMSPeak>> id2peaks : entries) {
                AgilentCompound cc = c.cloneWithoutPeaks();
                List<AgilentMSPeak> peakList = id2peaks.getValue();
                Collections.sort(peakList);
                cc.addAll(peakList);
                tmp.add(cc);
            }
        }
        compounds = tmp;
    }
}
