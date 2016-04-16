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
package umich.ms.batmass.gui.viewers.treetable.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.swing.outline.Outline;
import umich.ms.batmass.gui.core.api.comm.eventbus.AbstractBusPubSub;
import umich.ms.batmass.nbputils.OutputWndPrinter;

/**
 *
 * @author Dmitry Avtonomov
 */
public class TreeTable extends Outline {

    protected BusHandler busHandler;

    public TreeTable() {
        constructor();
    }

//    public TreeTable(OutlineModel mdl) {
//        super(mdl);
//        constructor();
//    }

    private void constructor() {
        busHandler = new BusHandler();
        setRootVisible(false);
//        setDefaultRenderer(Object.class, new CustomTreeTableCellRenderer());
//        setDefaultRenderer(Double.class, new CustomTreeTableCellRenderer());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = rowAtPoint(e.getPoint());
                int c = columnAtPoint(e.getPoint());
                OutputWndPrinter.printOut("TreeTable DEBUG", String.format("Click at (r:%d, c:%d)", r, c));
                
                if (e.isPopupTrigger() && e.getComponent() instanceof TreeTable) {
                    JPopupMenu popup = createPopupMenu();
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    protected JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("A popup menu item");
        //menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Another popup menu item");
        //menuItem.addActionListener(this);
        popup.add(menuItem);
        return popup;
    }

    @Override
    public boolean isPopupUsedFromTheCorner() {
        return true;
    }

    public BusHandler getBusHandler() {
        return busHandler;
    }

    public class BusHandler extends AbstractBusPubSub {

    }
}
