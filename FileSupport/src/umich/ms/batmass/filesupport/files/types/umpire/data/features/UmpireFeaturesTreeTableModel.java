/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.files.types.umpire.data.features;

import javax.swing.tree.TreeModel;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RowModel;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireFeaturesTreeTableModel {

    protected TreeModel treeModel;
    protected RowModel rowModel;
    protected OutlineModel outlineModel;

    public UmpireFeaturesTreeTableModel(TreeModel treeModel, RowModel rowModel) {
        this.treeModel = treeModel;
        this.rowModel = rowModel;
        outlineModel = DefaultOutlineModel.createOutlineModel(treeModel, rowModel);
    }

    public OutlineModel getOutlineModel() {
        return outlineModel;
    }
}
