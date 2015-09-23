package umich.ms.batmass.filesupport.files.types.agilent.cef.model;

/**
 * Created by dmitriya on 2015-09-23.
 */
public class IonId {
    public static final int CHARGE_UNKNOWN = Integer.MIN_VALUE;
    public String molId = "";
    public int mCount = 1;
    public int z = CHARGE_UNKNOWN;
    public String zCarrier;
    public String adduct = "";
    int isotopeNumber = 0;

    public int getmCount() {
        return mCount;
    }

    public String getMolId() {
        return molId;
    }

    public void setMolId(String molId) {
        this.molId = molId;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getzCarrier() {
        return zCarrier;
    }

    public void setzCarrier(String zCarrier) {
        this.zCarrier = zCarrier;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public int getIsotopeNumber() {
        return isotopeNumber;
    }

    public void setIsotopeNumber(int isotopeNumber) {
        this.isotopeNumber = isotopeNumber;
    }
}
