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
package umich.ms.batmass.filesupport.files.types.umpireid.data.tabledomain;

import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpirePSM;
import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpireId;
import umich.ms.batmass.filesupport.files.types.umpireid.data.modeldomain.UmpireIds;
import java.util.HashMap;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import umich.ms.batmass.nbputils.OutputWndPrinter;

/**
 *
 * @author Dmitry Avtonomov
 */
public class UmpireIdTreeModel { //implements TreeModel {

//    private final UmpireIds root;
//    private final HashMap<String, Integer> idToIndex;
//
//    public UmpireIdTreeModel(UmpireIds root) {
//        this.root = root;
//        this.idToIndex = new HashMap<>(root.getIds().length);
//        for (int i = 0; i < root.getIds().length; i++) {
//            UmpireId id = root.getIds()[i];
//            idToIndex.put(id.buildKey(), i);
//        }
//    }
//
//    @Override
//    public Object getRoot() {
//        return root;
//    }
//
//    @Override
//    public Object getChild(Object parent, int index) {
//        if (parent == root) {
//            return root.getIds()[index];
//        }
//
//        if (parent instanceof UmpireId) {
//            UmpireId id = (UmpireId) parent;
//            return id.getPsms()[index];
//        }
//
//        if (parent instanceof UmpirePSM) {
//            throw new IllegalStateException("UmpirePSMs are supposed to be nodes, "
//                    + "getChild() should not be called for them");
//        }
//
//        throw new IllegalStateException("This code should be never reached");
//    }
//
//    @Override
//    public int getChildCount(Object parent) {
//        if (parent == root) {
//            return root.getIds().length;
//        }
//
//        if (parent instanceof UmpireId) {
//            UmpireId id = (UmpireId) parent;
//            return id.getPsms().length;
//        }
//
//        if (parent instanceof UmpirePSM) {
//            return 0;
//        }
//
//        throw new IllegalStateException("This code should be never reached");
//    }
//
//    @Override
//    public boolean isLeaf(Object node) {
//        if (node == root) {
//            return false;
//        }
//
//        if (node instanceof UmpireId) {
//            UmpireId id = (UmpireId) node;
//            return id.getPsms().length == 0;
//        }
//
//        if (node instanceof UmpirePSM) {
//            return true;
//        }
//
//        throw new IllegalStateException("This code should be never reached");
//    }
//
//    @Override
//    public int getIndexOfChild(Object parent, Object child) {
//
//        // do we even need to implement this method?
//        OutputWndPrinter.printOut("DEBUG", "getIndexOfChild() called in UmpireIdTreeModel");
//
//        if (parent == null || child == null) {
//            return - 1;
//        }
//
//        if (parent == root && child instanceof UmpireId) {
//            UmpireId id = (UmpireId) child;
//            String key = id.buildKey();
//            Integer index = idToIndex.get(key);
//            if (index == null) {
//                throw new IllegalStateException("Can't find root node's child UmpireId object");
//            }
//            return index;
//        }
//
//        if (parent instanceof UmpireId && child instanceof UmpirePSM) {
//            UmpireId id = (UmpireId) parent;
//            UmpirePSM[] psms = id.getPsms();
//            for (int i = 0; i < psms.length; i++) {
//                if (psms[i] == child) {
//                    return i;
//                }
//            }
//            return -1;
//        }
//
//        if (parent instanceof UmpirePSM) {
//            throw new IllegalStateException("PSMs dont have children, should not get here.");
//        }
//
//        throw new IllegalStateException("This code should be never reached");
//    }
//
//
//    @Override
//    public void valueForPathChanged(TreePath path, Object newValue) {
//        //throw new UnsupportedOperationException("Modification not allowed.");
//    }
//
//    @Override
//    public void addTreeModelListener(TreeModelListener l) {
//        //throw new UnsupportedOperationException("Model won't change, listeners not supported.");
//    }
//
//    @Override
//    public void removeTreeModelListener(TreeModelListener l) {
//        //throw new UnsupportedOperationException("Model won't change, listeners not supported.");
//    }
//
//    protected class RootNode extends DefaultMutableTreeNode {
//
//        public RootNode() {
//        }
//
//        public RootNode(Object userObject) {
//            super(userObject);
//        }
//
//    }
}
