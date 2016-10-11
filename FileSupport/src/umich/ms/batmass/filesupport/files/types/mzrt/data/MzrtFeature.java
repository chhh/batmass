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
package umich.ms.batmass.filesupport.files.types.mzrt.data;

import java.awt.Color;
import org.apache.commons.csv.CSVRecord;
import umich.ms.batmass.data.core.lcms.features.AbstractLCMSFeature2D;
import umich.ms.batmass.filesupport.files.types.mzrt.model.MzrtBox;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MzrtFeature extends AbstractLCMSFeature2D<MzrtBox> {

    private Color color = null;
    private Float opacity = null;
    private CSVRecord csvRec;
    
    public MzrtFeature(MzrtBox[] traces, int charge, CSVRecord csvRec) {
        super(traces, charge);
        this.csvRec = csvRec;
    }

    public MzrtFeature(MzrtBox[] traces, CSVRecord csvRec) {
        super(traces);
        this.csvRec = csvRec;
    }

    @Override
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Float getOpacity() {
        return opacity;
    }

    public void setOpacity(Float opacity) {
        this.opacity = opacity;
    }

    
}
