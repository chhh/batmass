/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpireid.data;

import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpireIds;
import umich.ms.batmass.data.core.api.DataContainer;
import umich.ms.batmass.data.core.api.DataSource;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireIdData extends DataContainer<UmpireIds> {

    public UmpireIdData(DataSource<UmpireIds> source) {
        super(source);
    }
}
