/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.spectrum.todelete;

import MSUmpire.PeakDataStructure.PeakCluster;

/**
 *
 * @author Dmitry Avtonomov
 */
public class PeakClusterContainer {
    public int startScanNum;
    public int endScanNum;
    public PeakCluster peakCluster;

    public PeakClusterContainer(int startScanNum, int endScanNum, PeakCluster peakCluster) {
        this.startScanNum = startScanNum;
        this.endScanNum = endScanNum;
        this.peakCluster = peakCluster;
    }
    
}
