/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain;

/**
 * To be used as data-type for UmpireIdData container.
 * @author Dmitry Avtonomov
 */
public class UmpireIds {

    private final UmpireId[] ids;

    public UmpireIds(UmpireId[] ids) {
        this.ids = ids;
    }

    public UmpireId[] getIds() {
        return ids;
    }
}
