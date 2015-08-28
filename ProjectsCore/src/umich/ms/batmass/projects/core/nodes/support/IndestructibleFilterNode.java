/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.nodes.support;

import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * A FilterNode with diasbled cut/copy/paste functions.
 * @author dmitriya
 */
public class IndestructibleFilterNode extends FilterNode {

    public IndestructibleFilterNode(Node original) {
        super(original);
    }

    public IndestructibleFilterNode(Node original, org.openide.nodes.Children children) {
        super(original, children);
    }

    public IndestructibleFilterNode(Node original, org.openide.nodes.Children children, Lookup lookup) {
        super(original, children, lookup);
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }
}
