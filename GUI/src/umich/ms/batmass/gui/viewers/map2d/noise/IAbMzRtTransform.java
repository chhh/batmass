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

import umich.ms.datatypes.scan.IScan;

/**
 * Intensity transformation for a single data point in a spectrum.
 * 'Ab' stands for Abundance. 'Intensity' is too inconvenient as it can't
 * be abbreviated to 2 letters 'in' or 3 letters 'int' which are both keywords.
 * @author chhh
 */
public interface IAbMzRtTransform {
    /**
     * Apply the transform
     * @param mz The m/z location of the data point to be transformed.
     * @param ab The intensity to be transformed.
     * @return New intensity. Could be 0 or NaN.
     */
    double apply(double mz, double ab);
    /**
     * Gives the transform impl a chance to prepare better for processing
     * mz/ab pairs from a given Scan. E.g. update internal state to match RT.
     * As processing is happening iteratively Scan by Scan, meaning RT by RT,
     * this is an optimization.
     */
    void configure(IScan scan);
}
