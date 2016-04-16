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
package umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain;

import MSUmpire.PSMDataStructure.PSM;
import MSUmpire.PSMDataStructure.PepIonID;

/**
 * Umpire ID bean. To be used with Outline View.
 * @author Dmitry Avtonomov
 */
public class UmpireId {
    private String modSeq;
    private String tppSeq;
    private float weight;
    /** Top PSM charge. */
    private byte charge;
    /** Top PSM m/z. */
    private float mz;
    private float maxProb;
    private boolean decoy;
    private byte missedCleavages;
    private UmpirePSM[] psms;
    

    public static UmpireId create(PepIonID ion) {
        UmpireId id = new UmpireId();

        PSM psmTop = ion.GetBestPSM();

        id.weight = ion.NeutralPrecursorMz();
        id.charge = (byte)psmTop.Charge;
        id.mz = psmTop.GetObsrIsotopicMz(0);
        id.modSeq = ion.ModSequence;
        id.tppSeq = ion.TPPModSeq;
        id.maxProb = ion.MaxProbability;
        id.decoy = ion.IsDecoy > 0;

        int size = ion.GetPSMList().size();
        id.psms = new UmpirePSM[size];
        for (int i = 0; i < size; i++) {
            id.psms[i] = UmpirePSM.create(ion.GetPSMList().get(i));
        }

        return id;
    }

    public String buildKey() {
        return modSeq + "_" + Integer.toString(charge);
    }

    public String getModSeq() {
        return modSeq;
    }

    public String getTppSeq() {
        return tppSeq;
    }

    public float getWeight() {
        return weight;
    }

    public byte getCharge() {
        return charge;
    }

    public float getMz() {
        return mz;
    }

    public float getMaxProb() {
        return maxProb;
    }

    public boolean isDecoy() {
        return decoy;
    }

    public byte getMissedCleavages() {
        return missedCleavages;
    }

    public UmpirePSM[] getPsms() {
        return psms;
    }

    
}
