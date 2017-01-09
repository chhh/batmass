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

import java.util.ArrayList;
import java.util.List;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.components.featuretable.AbstractFeatureTableModel;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.AnalysisResult;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.InterprophetResult;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.NameValueType;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.PeptideprophetResult;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.SearchHit;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.SpectrumQuery;

/**
 *
 * @author Dmitry Avtonomov
 */
public class PepxmlTableModel extends AbstractFeatureTableModel {
    
    protected final List<SpectrumQuery> queries;
    protected String[] colNames;
    protected Class[] colTypes;
    protected ValueFetcher[] colFetchers;
    private static final double P_MASS = 1.00727647;
    public static final String PEP_PROPH = "peptideprophet";
    public static final String I_PROPH = "interprophet";
    
    interface ValueFetcher {
        Object fetch(SpectrumQuery q);
    }
    
    private SearchHit getFirstHit(SpectrumQuery q) {
        return q.getSearchResult().get(0).getSearchHit().get(0);
    }
            
    /**
     * 
     * @param queries Must be non-zero length.
     */
    public PepxmlTableModel(List<SpectrumQuery> queries) {
        if (queries.isEmpty())
            throw new IllegalStateException("Given SpectrumQuery list must be non-empty.");
        this.queries = queries;
        ArrayList<String> colN = new ArrayList<>();
        ArrayList<Class> colT = new ArrayList<>();
        ArrayList<ValueFetcher> colF = new ArrayList<>();
        
        colN.add("Sequence");
        colT.add(String.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return getFirstHit(q).getPeptide();
        }});
        
        colN.add("z");
        colT.add(Integer.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return q.getAssumedCharge();
        }});
        
        colN.add("m/z(obs)");
        colT.add(Double.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return (q.getPrecursorNeutralMass() + q.getAssumedCharge() * P_MASS) / q.getAssumedCharge();
        }});
        
        colN.add("M(obs)");
        colT.add(Double.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return q.getPrecursorNeutralMass();
        }});
        
        colN.add("M(calc)");
        colT.add(Double.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return getFirstHit(q).getCalcNeutralPepMass();
        }});
        
        colN.add("RT(min)");
        colT.add(Double.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return q.getRetentionTimeSec() / 60d;
        }});
        
        colN.add("Scan#");
        colT.add(Integer.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return (int)q.getStartScan();
        }});
        
        colN.add("ProtID");
        colT.add(String.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return getFirstHit(q).getProtein();
        }});
        
        colN.add("Ions Matched");
        colT.add(Integer.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return getFirstHit(q).getNumMatchedIons();
        }});
        
        colN.add("Ions Total");
        colT.add(Integer.class);
        colF.add(new ValueFetcher() {
            @Override public Object fetch(SpectrumQuery q) {
                return getFirstHit(q).getTotNumIons();
        }});
        
        SpectrumQuery q = queries.get(0);
        SearchHit sh = q.getSearchResult().get(0).getSearchHit().get(0);
        List<NameValueType> searchScores = sh.getSearchScore();
        for (int i = 0; i < searchScores.size(); i++) {
            NameValueType score = searchScores.get(i);
            final int index = i;
            colN.add(score.getName());
            colT.add(Double.class);
            colF.add(new ValueFetcher() {
                @Override public Object fetch(SpectrumQuery q) {
                    String valueStr = getFirstHit(q).getSearchScore().get(index).getValueStr();
                    return Double.parseDouble(valueStr);
            }});
        }
        
        List<AnalysisResult> analysisResults = sh.getAnalysisResult();
        for (int i = 0; i < analysisResults.size(); i++) {
            AnalysisResult analysisResult = analysisResults.get(i);
            final int index = i;
            switch (analysisResult.getAnalysis()) {
                case PEP_PROPH:
                    if (analysisResult.getAny() instanceof PeptideprophetResult) {
                        colN.add("PepProph");
                        colT.add(Double.class);
                        colF.add(new ValueFetcher() {
                            @Override public Object fetch(SpectrumQuery q) {
                                return ((PeptideprophetResult)getFirstHit(q).getAnalysisResult().get(index).getAny()).getProbability();
                        }});
                    } else {
                        throw new IllegalStateException(String.format("Found %s analysis result, and could not case it to PeptideprophetResult", PEP_PROPH));
                    }
                    break;
                case I_PROPH:
                    if (analysisResult.getAny() instanceof InterprophetResult) {
                        colN.add("iProph");
                        colT.add(Double.class);
                        colF.add(new ValueFetcher() {
                            @Override public Object fetch(SpectrumQuery q) {
                                return ((PeptideprophetResult)getFirstHit(q).getAnalysisResult().get(index).getAny()).getProbability();
                        }});
                    } else {
                        throw new IllegalStateException(String.format("Found %s analysis result, and could not case it to PeptideprophetResult", I_PROPH));
                    }
                    break;
            }
        }
        
        this.colNames = colN.toArray(new String[0]);
        this.colTypes = colT.toArray(new Class[0]);
        this.colFetchers = colF.toArray(new ValueFetcher[0]);
    }
    
    @Override
    public int getRowCount() {
        return queries.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SpectrumQuery q = queries.get(rowIndex);
        return colFetchers[columnIndex].fetch(q);
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return colNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return colTypes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public MzRtRegion rowToRegion(int row) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
