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
package umich.ms.batmass.filesupport.files.types.mzrt.providers;

import java.awt.Color;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVRecord;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.filesupport.files.types.mzrt.data.MzrtFeature;
import umich.ms.batmass.data.core.api.DefaultDataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.filesupport.files.types.mzrt.data.MzrtFeatures;
import umich.ms.batmass.filesupport.files.types.mzrt.model.MzrtBox;
import umich.ms.batmass.filesupport.files.types.mzrt.model.MzrtFile;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MzrtFeaturesDataSource extends DefaultDataSource<Features<MzrtFeature>> {

    public MzrtFeaturesDataSource(URI origin) {
        super(origin);
    }

    @Override
    public MzrtFeatures load() throws DataLoadingException {
        
        
        Path path = Paths.get(this.getOriginURI());
        MzrtFile file = new MzrtFile(path);
        file.load();
        
        MzrtFeatures features = new MzrtFeatures(file);
        
        Map<String, Integer> header = file.getHeader();
        List<CSVRecord> records = file.getRecords();
        
        
        int[] idxs = file.getIndexesMzRtColorOpacity();
        for (CSVRecord record : records) {
            String mzLo = record.get(idxs[0]);
            String mzHi = record.get(idxs[1]);
            String rtLo = record.get(idxs[2]);
            String rtHi = record.get(idxs[3]);
            double mlo, mhi, rlo, rhi;
            try {
                mlo = Double.parseDouble(mzLo);
                mhi = Double.parseDouble(mzHi);
                rlo = Double.parseDouble(rtLo);
                rhi = Double.parseDouble(rtHi);
                double mz = (mlo + mhi) / 2;
                MzrtBox box = new MzrtBox(mz, rlo, rhi, mlo, mhi);
                
                MzrtFeature mzrtFeature = new MzrtFeature(new MzrtBox[]{box}, record);
                
                if (idxs[4] >=0) {
                    String colorStr = record.get(idxs[4]);
                    try {
                        Color color = Color.decode(colorStr);
                        mzrtFeature.setColor(color);
                    } catch (NumberFormatException ex) {
                        throw new DataLoadingException("Could not decode color string");
                    }
                }
                if (idxs[5] >=0) {
                    String opacityStr = record.get(idxs[5]);
                    float opacity = Float.parseFloat(opacityStr);
                    mzrtFeature.setOpacity(opacity);
                }
                
                features.add(mzrtFeature, 1, null);
                
            } catch (NumberFormatException ex) {
                throw new DataLoadingException(ex);
            }
        }
        
        return features;
    }
    
}
