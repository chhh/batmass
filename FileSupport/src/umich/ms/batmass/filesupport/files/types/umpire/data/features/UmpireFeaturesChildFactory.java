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
