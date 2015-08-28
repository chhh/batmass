/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeatureNode extends BeanNode<UmpireFeatureBean> {
    
    public Children.Array children;

    public UmpireFeatureNode(UmpireFeatureBean bean) throws IntrospectionException {
        super(bean, Children.LEAF);
    }

    
}
