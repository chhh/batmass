/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
