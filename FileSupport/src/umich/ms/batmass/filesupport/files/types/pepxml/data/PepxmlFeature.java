/*
 * Copyright 2017 Dmitry Avtonomov.
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
package umich.ms.batmass.filesupport.files.types.pepxml.data;

import java.awt.Color;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSFeature2D;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.SpectrumQuery;

/**
 *
 * @author Dmitry Avtonomov
 */
public class PepxmlFeature extends AbstractLCMSFeature2D<PepxmlTrace> {
    
    private static double rtTolInSeconds = 3;
    private static final double P_MASS = 1.00727647;

    private SpectrumQuery query;
    
    public PepxmlFeature(PepxmlTrace[] traces, int charge) {
        super(traces, charge);
    }

    public PepxmlFeature(PepxmlTrace[] traces) {
        super(traces);
    }

    public SpectrumQuery getQuery() {
        return query;
    }

    public void setQuery(SpectrumQuery query) {
        this.query = query;
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }

    @Override
    public Float getOpacity() {
        return null;
    }
    
    
    public static PepxmlFeature create(SpectrumQuery q)  {
        double mz = (q.getPrecursorNeutralMass() + q.getAssumedCharge() * P_MASS) / q.getAssumedCharge();
        double rt = q.getRetentionTimeSec();
        double rtLo = (rt - rtTolInSeconds) / 60d;
        double rtHi = (rt + rtTolInSeconds) / 60d;
        PepxmlTrace trace = new PepxmlTrace(mz, rtLo, rtHi);
        int charge = q.getAssumedCharge();
        
        PepxmlFeature feature = new PepxmlFeature(new PepxmlTrace[]{trace}, charge);
        feature.query = q;
        
        return feature;
    }
}
