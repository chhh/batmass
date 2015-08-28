/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.lcms.features.data;

import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;

/**
 * An ordinary OutlineModel, but with RenderDataProvider built-in.
 * @author Dmitry Avtonomov
 */
public interface BMOutlineModel extends OutlineModel {

    RenderDataProvider getRenderDataProvider();
}
