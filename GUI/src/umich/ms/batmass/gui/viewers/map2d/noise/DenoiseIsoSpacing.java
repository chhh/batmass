/*
 * Copyright 2020 chhh.
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
package umich.ms.batmass.gui.viewers.map2d.noise;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import umich.ms.batmass.gui.core.api.util.ArrayUtils;
import umich.ms.batmass.nbputils.notifications.MessageUtil;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.exceptions.FileParsingException;

/**
 *
 * @author chhh
 */
public class DenoiseIsoSpacing implements IAbMzRtTransform {
    public static final String NAME = "IsoSpacing";
    private final TreeMap<Integer, Double> mapRtToThreshold;
    private double curThreshold = Double.NaN;

    public DenoiseIsoSpacing(TreeMap<Integer, Double> mapRtToThreshold) {
        this.mapRtToThreshold = mapRtToThreshold;
    }
    
    
    
    public static DenoiseIsoSpacing from(NavigableMap<Integer, IScan> scansByRtSpanAtMsLevel) {
        TreeMap<Integer, Double> map = new TreeMap<>();
        for (Map.Entry<Integer, IScan> entry : scansByRtSpanAtMsLevel.entrySet()) {
            IScan scan = entry.getValue();
            ISpectrum spec;
            try {
                spec = scan.fetchSpectrum();
            } catch (FileParsingException ex) {
                MessageUtil.showException("DenoiseIsoSpacing fetching spectra", ex);
                break;
            }
            double threshold = findDenoiseThreshold(spec.getMZs(), spec.getIntensities());
            map.put(entry.getKey(), threshold);
        }
        return new DenoiseIsoSpacing(map);
    }

    
    @Override
    public double apply(double mz, double ab) {
        return curThreshold < ab ? ab : 0.0;
    }

    @Override
    public void configure(IScan scan) {
        Double thresh = mapRtToThreshold.get(scan.getNum());
        if (thresh != null)
            curThreshold = thresh;
    }
    
    private static double findDenoiseThreshold(double[] mz, double[] ints) {
        double background = Double.NaN;
        if (mz.length < 10) {
            return background;
        }
        double quantile = 0.5;      
//        double[] copyOfInts = Arrays.copyOf(ints, ints.length);
//        Arrays.sort(copyOfInts);
//        double lower = copyOfInts[0];
//        double upper = copyOfInts[(int) (copyOfInts.length * quantile)];
        double[] calcedQuantile = ArrayUtils.calcQuantileValue(ints, quantile);
        double lower = calcedQuantile[0];
        double upper = calcedQuantile[1];
        double interval = (upper - lower) / 20d;

        int count1, count2, count3, count4, noise;
        double ratio = 1.2d;

        for (double bk = lower; bk < upper; bk += interval) {
            count1 = 0;
            count2 = 0;
            count3 = 0;
            count4 = 0;
            noise = 0;
            background = bk;
            
            int len = mz.length;
            double dist;
            for (int idxLo = 0; idxLo < len-1; idxLo++) {
                if (ints[idxLo] < background) {
                    continue;
                }
                for (int idxHi = idxLo + 1; idxLo < len; idxLo++) {
                    if (idxHi < background) {
                        continue;
                    }
                    dist = mz[idxHi] - mz[idxLo];
                    if (dist < 0.23 || dist > 1.05 ) {
                        noise++;
                        break;
                    }
                    
                    if (ints[idxLo] > ints[idxHi]) {
                        if (       dist > 0.24 && dist < 0.26) {
                            count1++;
                            break;
                        } else if (dist > 0.30 && dist < 0.36) {
                            count2++;
                            break;
                        } else if (dist > 0.45 && dist < 0.55) {
                            count3++;
                            break;
                        } else if (dist > 0.95 && dist < 1.05) {
                            count4++;
                            break;
                        }
                    } 
                }
            }
            if (noise < (count1 + count2 + count3 + count4) * ratio) {
                break;
            }
        }
        return background;
    }
}
