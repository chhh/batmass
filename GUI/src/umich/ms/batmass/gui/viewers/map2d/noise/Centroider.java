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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author chhh
 */
public class Centroider {

    enum State {
        UP, DOWN, FLAT
    }

    public static class Peak {

        public int idxLo = -1;
        public int idxHi = -1;
        public int idxTopLo = -1;
        public int idxTopHi = -1;
        public double valLo = Double.NEGATIVE_INFINITY;
        public double valHi = Double.NEGATIVE_INFINITY;
        public double valTop = Double.NEGATIVE_INFINITY;

        public float noise = Float.NaN;

        public int numNonZeroPts = 0;
        public int idxLoNonZero = -1;
        public int idxHiNonZero = -1;

        public double mzInterpolated;
    }
    
    public static class PeakMz
    {
        public double mz;
        public double intensity;
        public double noise;
        public double mzHi;
        public double mzLo;
        public int numPts;
    }

    public static List<PeakMz> detectPeaks(int from, int to, double[] mzs, double[] abs, int minPts, double cutoff) {
        if (to - from < 3) {
            return Collections.EMPTY_LIST;
        }
        if (mzs.length != abs.length) {
            throw new RuntimeException("M/z array must be of the same length as the abundances array");
        }

        List<PeakMz> peaks = new ArrayList<>(Math.min(100, (to-from)/30));
        Peak p = null;
        State s0 = State.FLAT;

        double[] x = new double[3];
        double[] y = new double[3];
        double[] parabola = new double[3];

        for (int i = from; i < to - 1; i++) {
            double diff = abs[i + 1] - abs[i];

            if (p == null) {
                if (diff > 0) {
                    p = new Peak();
                    p.idxLo = i;
                    p.valLo = abs[i];
                    s0 = State.UP;
                }
            } else {
                State s1;
                if (diff > 0) {
                    s1 = State.UP;
                } else if (diff < 0) {
                    s1 = State.DOWN;
                } else {
                    s1 = State.FLAT;
                }

                switch (s0) {
                    case UP:
                        if (s1 == State.FLAT) // if we flat out, it might be the maximum (but saturated detector)
                        {
                            if (p.idxTopLo < 0) {
                                p.idxTopLo = i;
                                p.idxTopHi = i;
                            } else {
                                p.idxTopHi = i;
                            }

                            p.valTop = abs[i];
                        } else if (s1 == State.DOWN) {
                            p.idxTopLo = i;
                            p.idxTopHi = i;
                            p.valTop = abs[i];
                        }
                        break;

                    case DOWN:
                        if (s1 == State.UP || s1 == State.FLAT) // go up or flat after going down
                        {
                            // finalize the current peak and possibly add it to the list of detected peaks
                            p.idxHi = i;
                            p.valHi = abs[i];
                            if (!finalizePeak(p, minPts, cutoff))
                                continue;
                            if (p.numNonZeroPts == 1) {
                                p.mzInterpolated = mzs[p.idxTopLo];
                            } else {
                                fitParabolaToPeak(p, x, y, parabola, mzs, abs);
                            }
                            PeakMz peakMz = new PeakMz();
                            peakMz.mz = p.mzInterpolated;
                            if (Double.isNaN(peakMz.mz)) {
                                continue;
                            }
                            peakMz.intensity = p.valTop;
                            //peakMz.mzWidth = Double.NaN; // undefined
                            //peakMz.mzWidth = mzs[p.idxHiNonZero] - mzs[p.idxLoNonZero];
                            peakMz.mzHi = mzs[p.idxHiNonZero];
                            peakMz.mzLo = mzs[p.idxLoNonZero];
                            peakMz.numPts = p.numNonZeroPts;
                            peakMz.noise = p.noise;
                            peaks.add(peakMz);
                            // end the current peak
                            p = null;
                        }
                        if (s1 == State.UP) // if we actually went up, then start a new peak
                        {
                            p = new Peak();
                            p.idxLo = i;
                            p.valLo = abs[i];
                        }
                        // else we're still going down, nothing to do

                        break;

                    case FLAT:
                        if (s1 == State.UP) // if we go up after staying flat, reset the apex data
                        {
                            p.idxTopLo = -1;
                            p.idxTopHi = -1;
                            p.valTop = Double.NEGATIVE_INFINITY;
                        } else if (s1 == State.DOWN) // go down after staying flat, update apex hi index
                        {
                            p.idxTopHi = i;
                        } else // state == State.Flat
                        {
                            p.idxTopHi = i; // if we stay flat, extend the apex data
                        }
                        break;
                }
                s0 = s1;

                // TODO: The peak needs to stand out from an N point moving average, probably
            }
        }

        return peaks;
    }
    
    private static boolean finalizePeak(Peak p, int minNumPts, double cutoff) {
        if (p.valTop < cutoff) {
            return false;
        }
        
        int numPts = p.idxHi - p.idxLo + 1;
        p.idxLoNonZero = p.idxLo;
        p.idxHiNonZero = p.idxHi;
        if (p.valLo == 0) {
            numPts--;
            p.idxLoNonZero += 1;
        }
        if (p.valHi == 0) {
            numPts--;
            p.idxHiNonZero -= 1;
        }
        p.numNonZeroPts = numPts;
        return numPts >= minNumPts;
    }
    
    private static void fitParabolaToPeak(Peak p, double[] x, double[] y, double[] parabola, double[] mzs, double[] abs)
        {
            if (p.idxTopLo == p.idxTopHi)
            {
                // this is a peak with a single point at the top, all fine
                int lo = p.idxTopLo - 1;
                int hi = p.idxTopLo + 1;
                x[0] = mzs[lo];
                x[1] = mzs[p.idxTopLo];
                x[2] = mzs[hi];
                y[0] = abs[lo];
                y[1] = p.valTop;
                y[2] = abs[hi];
                PolynomialUtils.fitParabola(x, y, parabola);
                p.mzInterpolated = PolynomialUtils.parabolaVertexX(parabola[2], parabola[1]);
            }
            else
            {
//                throw new RuntimeException("Commented out code that required QR decomp");
                x = new double[4];
                y = new double[4];
                int lo = p.idxTopLo - 1;
                int hi = p.idxTopHi + 1;
                x[0] = mzs[lo];
                x[1] = mzs[p.idxTopLo];
                x[2] = mzs[p.idxTopHi];
                x[3] = mzs[hi];
                y[0] = abs[lo];
                y[1] = p.valTop;
                y[2] = p.valTop;
                y[3] = abs[hi];
                
                p.mzInterpolated = (x[1] + x[2]) / 2.0;
//                double[] poly2 = PolynomialUtils.Polynomial(x, y, 2);
//                p.mzInterpolated = PolynomialUtils.ParabolaVertexX(poly2[2], poly2[1]);
            }
        }
}
