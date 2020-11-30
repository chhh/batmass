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

import java.util.Arrays;
import java.util.StringJoiner;

/**
 *
 * @author chhh
 */
public class Trace {
  double[] mzs;
  float[] abs;
  float[] rts;
  int[] scanNums;
  int countNonZeros;
  int zeroAbStretch;
  double abSum;
  double mzSum;
  double mzSumWeighted;
  double mzAvg;
  double mzAvgWeighted;
  float rtLo;
  float rtHi;
  int ptr = -1;

  public Trace(int initSize) {
    mzs = new double[initSize];
    abs = new float[initSize];
    rts = new float[initSize];
    scanNums = new int[initSize];
  }

  public final void add(double mz, float ab, int scanNum, float rt) {
    ensureCapacity(10);
    if (ptr < 0) {
      rtLo = rt;
      rtHi = rt;
    }
    ptr += 1;
    mzs[ptr] = mz;
    abs[ptr] = ab;
    rts[ptr] = rt;
    scanNums[ptr] = scanNum;

    if (ab > 0) {
      countNonZeros += 1;
      mzSum += mz;
      rtHi = rt;
    }
    abSum += ab;
    mzSumWeighted += mz * ab;
    mzAvg = mzSum / countNonZeros;
    mzAvgWeighted = mzSumWeighted / abSum;


  }

  public int size() {
    return ptr + 1;
  }

  public final void ensureCapacity(int extendBy) {
    if (mzs.length == ptr + 1) {
      int newLen = mzs.length + extendBy;
      mzs = Arrays.copyOf(mzs, newLen);
      abs = Arrays.copyOf(abs, newLen);
      rts = Arrays.copyOf(rts, newLen);
      scanNums = Arrays.copyOf(scanNums, newLen);
    }
  }
  public final void reset() {
    ptr = -1;
    abSum = 0;
    mzSum = 0;
    mzAvg = 0;
    mzAvgWeighted = 0;
    rtLo = Float.NaN;
    rtHi = Float.NaN;
  }

  public void restart(double mz, float ab, int scanNum, float rt) {
    reset();
    add(mz, ab, scanNum, rt);
  }

  private String makeId(double mz, int scanNum) {
    return String.format("%.5f@%d", mz, scanNum);
  }

  @Override
  public String toString() {
    if (ptr < 0) {
      return Trace.class.getSimpleName() + "[empty, capacity: " + mzs.length + "]";
    }
    return new StringJoiner(", ", Trace.class.getSimpleName() + "[", "]")
        .add(String.format("mz: %.4f", mzs[0]))
        .add(String.format("@t: %.2f", rts[0]))
        .add(String.format("@#: %d", scanNums[0]))
        .add(String.format("len: %d", ptr + 1))
        .add(String.format("capacity: %d", mzs.length))
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Trace trace = (Trace) o;

    if (Double.compare(mzs[0], trace.mzs[0]) != 0) return false;
    return scanNums[0] == trace.scanNums[0];
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(mzs[0]);
    result = (int) (temp ^ (temp >>> 32));
    result = 31 * result + scanNums[0];
    return result;
  }
}