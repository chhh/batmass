/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpireid.data.tabledomain;

import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpirePSM;
import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpireId;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.tree.TreeModel;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;
import umich.ms.batmass.data.core.lcms.features.data.BMOutlineModel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireIdOutlineModel extends DefaultOutlineModel implements BMOutlineModel {

    public UmpireIdOutlineModel(TreeModel treeModel, RowModel rowModel, boolean largeModel, String nodesColumnLabel) {
        super(treeModel, rowModel, largeModel, nodesColumnLabel);
    }

    @Override
    public RenderDataProvider getRenderDataProvider() {
        return new RenderDataProvider() {

            @Override
            public String getDisplayName(Object o) {
                if (o instanceof UmpireId) {
                    UmpireId id = (UmpireId) o;
                    return id.buildKey();
                }
                if (o instanceof UmpirePSM) {
                    UmpirePSM psm = (UmpirePSM) o;
                    return String.format("%.4f[%+d] @ %.2f(#%d)",
                            psm.getObservedPrecursorMz(), psm.getCharge(), psm.getRt(), psm.getScanNum());
                }
                
                return "UNKNOWN";
                //throw new IllegalStateException(String.format("Code should never reach this line. Class of o was: %s", o.getClass().getName()));
            }

            @Override
            public boolean isHtmlDisplayName(Object o) {
                return false;
            }

            @Override
            public Color getBackground(Object o) {
                return null;
            }

            @Override
            public Color getForeground(Object o) {
                return null;
            }

            @Override
            public String getTooltipText(Object o) {
                return null;
            }

            @Override
            public Icon getIcon(Object o) {
                return null;
            }
        };
    }
}
