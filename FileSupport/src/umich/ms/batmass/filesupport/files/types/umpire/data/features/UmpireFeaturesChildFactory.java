/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesChildFactory extends ChildFactory<UmpireFeature> {

    protected final List<UmpireFeature> list;

    public UmpireFeaturesChildFactory(List<UmpireFeature> list) {
        this.list = list;
    }

    
    @Override
    protected boolean createKeys(List<UmpireFeature> toPopulate) {

        // TODO: WARNING: ACHTUNG: DEBUG: REMOVE: this was here only because OutlineView was super slow with 1M rows to be displayed.
        toPopulate.addAll(list.subList(0, 10000));

        return true;
    }

    @Override
    protected UmpireFeatureNode createNodeForKey(UmpireFeature f) {
        try {

            UmpireFeatureBean bean = new UmpireFeatureBean(f);
            UmpireFeatureNode node = new UmpireFeatureNode(bean);
            return node;
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }


}
