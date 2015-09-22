/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.xcms.peaks.data;

import javax.swing.table.TableModel;
import umich.ms.batmass.data.core.api.DataSource;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.data.FeatureTableModelData;

/**
 *
 * @author Dmitry Avtonomov
 */
public class XCMSFeaturesTableModelData extends FeatureTableModelData<XCMSFeature>{

    public XCMSFeaturesTableModelData(DataSource<Features<XCMSFeature>> source) {
        super(source);
    }

    @Override
    public TableModel create() {
        Features<XCMSFeature> data = getData();
        if (data == null) {
            throw new IllegalStateException("You must have loaded the data from the data source before calling create()."
                    + "Use .load(Object user) on this object first.");
        }
        
        return new XCMSFeaturesTableModel(data.getMs1().getList());
    }

}
