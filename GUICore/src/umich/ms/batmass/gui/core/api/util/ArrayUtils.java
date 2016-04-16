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
package umich.ms.batmass.gui.core.api.util;

import java.util.Arrays;

/**
 * Utilities for operations on arrays.
 * @author Dmitry Avtonomov <dmitriy.avtonomov@gmail.com>
 */
public abstract class ArrayUtils {
    private ArrayUtils(){};
    
    /**
     * Calculate the value for desired quantile using Parabolic Prediction algorithm.<br/>
     * Taken from: http://www.cs.wustl.edu/~jain/papers/ftp/psqr.pdf<br/>
     * Does not modify data or make copies of it, quantile is calculated in a single pass.
     * @param x
     * @param p desired quantile
     * @return double[3]: [0] min value, [1] quantile value, [2] max value
     */
    public static double[] calcQuantileValue(double[] x, double p) {
        if (x.length < 5)
            throw new IllegalArgumentException("Length of array must be at least 5");
        // [0] min value in X, [1] quantile value of X, [2] max value of X
        double[] result = {Double.POSITIVE_INFINITY, Double.NaN, Double.NEGATIVE_INFINITY};
        double[] q = Arrays.copyOfRange(x, 0, 5);
        Arrays.sort(q);
        // q[] has been sorted, so we can start tracking Min/Max values
        result[0] = q[0];
        result[2] = q[4];
        //double q1 = q[0], q2 = q[1], q3 = q[2], q4 = q[3], q5 = q[4];
        //int n1 = 1, n2 = 2, n3 = 3, n4 = 4, n5 = 5;
        int[] n = {0, 1, 2, 3, 4}; // marker locations
        double[] nPrime = {1d, 1d+2d*p, 1d+4d*p, 3d+2d*p, 5d}; // desired marker locations
        double[] dn = {0d, p/2d, p, (1d+p)/2d, 1d}; // increment in desired marker locations
        double[] d = new double[5];
        double[] qPrime = new double[5];
        
        int j, i, k;
        double xj;
        
        for (j = 5; j < x.length; j++) {
            xj = x[j];
            
            if (xj < result[0]) {
                result[0] = xj;
            } else if (xj > result[2]) {
                result[2] = xj;
            }
            
            // find where the new observation falls
            if (xj < q[0]) {
                k = 0;
            } else if (xj < q[1]) {
                k = 0;
            } else if (xj < q[2]) {
                k = 1;
            } else if (xj < q[3]) {
                k = 2;
            } else if (xj < q[4]) {
                k = 3;
            } else {
                q[4] = xj;
                k = 3;
            }
            
            // increment positions of markers k+1 through 4
            for (i = k+1; i < 5; i++) {
                n[i]++;
            }
            // update desired positions for all markers
            for (i = 0; i < 5; i++) {
                nPrime[i] = nPrime[i] + dn[i];
            }
            
            // adjust height of markers 2-4 if necessary (if markers are numbered 1-5)
            for (i = 1; i < 4; i++) {
                d[i] = nPrime[i] - n[i];
                if (  (d[i] >= 1  && n[i+1] - n[i] >  1)  || (d[i] <= -1 && n[i-1] - n[i] < -1)) {
                    d[i] = Math.signum(d[i]);
                    if (d[i] == 0) {
                        d[i] = 1;
                    }
                    // 'P squared' formula, PP, Parabolic Prediction
                    qPrime[i] = q[i] + (d[i] / (n[i+1]-n[i-1])) * 
                                    ((n[i]-n[i-1]+d[i]) * (q[i+1]-q[i]) / (n[i+1]-n[i]) + 
                                     (n[i+1]-n[i]+d[i]) * (q[i]-q[i-1]) / (n[i]-n[i-1])
                                    );
                    if (q[i-1] < qPrime[i] && qPrime[i] < q[i+1]) {
                        q[i] = qPrime[i];
                    } else {
                        // if PP formula didn't work (qPrime[i]) is not 
                        // between q[i-1] and q[i+1], use linear formula
                        q[i] = q[i] + d[i] * ( (q[i+(int)d[i]] - q[i]) / (n[i+(int)d[i]] - n[i]) );
                    }
                    
                    n[i] = n[i] + (int)d[i];
                }
            }
        }
        // return the middle prediction point
        result[1] = q[2];
        return result;
    }
}
