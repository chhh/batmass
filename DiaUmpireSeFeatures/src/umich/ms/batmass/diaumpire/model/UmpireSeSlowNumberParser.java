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
package umich.ms.batmass.diaumpire.model;

import umich.ms.batmass.filesupport.core.util.DelimitedFiles;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireSeSlowNumberParser extends DelimitedFiles.StringParsingDelegate {
    protected UmpireSeIsoCluster cluster;
    protected int[] colMapping;

    public UmpireSeSlowNumberParser(int[] colMapping) {
        cluster = new UmpireSeIsoCluster();
        this.colMapping = colMapping;
    }
    
    @Override
    public void parse(int idx, String s) {
        int mapping = colMapping[idx];
        switch (mapping) {
            case 0:
                cluster.setRtLo(Double.parseDouble(s));
                break;
            case 1:
                cluster.setRtHi(Double.parseDouble(s));
                break;
            case 2:
                cluster.setScanNumLo(Integer.parseInt(s));
                break;
            case 3: 
                cluster.setScanNumHi(Integer.parseInt(s));
                break;
            case 4:
                cluster.setCharge(Integer.parseInt(s));
                break;
            case 5:
                double[] mz = cluster.getMz();
                mz[0] = Double.parseDouble(s);
                break;
            case 6:
                mz = cluster.getMz();
                mz[1] = Double.parseDouble(s);
                break;
            case 7: 
                mz = cluster.getMz();
                mz[2] = Double.parseDouble(s);
                break;
            case 8:
                mz = cluster.getMz();
                mz[3] = Double.parseDouble(s);
                break;
            case 9:
                cluster.setPeakHeight(Double.parseDouble(s));
                break;
            case 10:
                cluster.setPeakArea(Double.parseDouble(s));
                break;
        }
    }
    
}
