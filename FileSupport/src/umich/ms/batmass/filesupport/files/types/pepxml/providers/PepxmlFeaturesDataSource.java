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
package umich.ms.batmass.filesupport.files.types.pepxml.providers;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.data.core.api.DefaultDataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.filesupport.files.types.pepxml.data.PepxmlFeature;
import umich.ms.fileio.exceptions.FileParsingException;
import umich.ms.fileio.filetypes.pepxml.PepXmlParser;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.MsmsPipelineAnalysis;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.MsmsRunSummary;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.SpectrumQuery;

/**
 *
 * @author Dmitry Avtonomov
 */
public class PepxmlFeaturesDataSource extends DefaultDataSource<Features<PepxmlFeature>> {

    public PepxmlFeaturesDataSource(URI origin) {
        super(origin);
    }

    @Override
    public Features<PepxmlFeature> load() throws DataLoadingException {
        Features<PepxmlFeature> features = new Features<>();
        try {
            Path path = Paths.get(uri).toAbsolutePath();
            MsmsPipelineAnalysis msms = PepXmlParser.parse(path);
            if (msms.getMsmsRunSummary().isEmpty())
                throw new DataLoadingException("No MsmsRunSummary was found in pep xml file.");
            if (msms.getMsmsRunSummary().size() > 1)
                throw new DataLoadingException("More than 1 MsmsRunSummary was found in pep xml file, not supported.");
            
            MsmsRunSummary sum = msms.getMsmsRunSummary().get(0);
            
            Float rt = sum.getSpectrumQuery().get(0).getRetentionTimeSec();
            if (rt == null)
                throw new DataLoadingException("Pep xml file had no retention time information.");
            
            
            for (SpectrumQuery q : sum.getSpectrumQuery()) {
                PepxmlFeature feature = PepxmlFeature.create(q);
                features.add(feature, 1, null);
            }
            return features;
            
        } catch (FileParsingException ex) {
            throw new DataLoadingException(ex);
        }
    }
    
}
