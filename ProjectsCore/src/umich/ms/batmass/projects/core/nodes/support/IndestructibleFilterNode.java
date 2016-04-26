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
package umich.ms.batmass.projects.core.nodes.support;

import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * A FilterNode with diasbled cut/copy/paste functions.
 * @author Dmitry Avtonomov
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
