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
package umich.ms.batmass.gui.core.api.comm.dnd;

import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import org.openide.util.Lookup;

/**
 * A draggable JButton, which can be dropped onto itself or other similar buttons.
 * @author Dmitry Avtonomov
 */
public class DnDButton extends JToggleButton {
//    @StaticResource
//    private static final String ICON_PATH_LINK = "umich/gui/viewers/scancollection2d/icons/icon_link.png";
//    public static Cursor LINK_CURSOR;
//    static {
//        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        //Image image = Toolkit.getDefaultToolkit().getImage(ICON_PATH_LINK);
//        ImageIcon imageIcon = ImageUtilities.loadImageIcon(ICON_PATH_LINK, false);
//        Point hotSpot = new Point(0, 0);
//        LINK_CURSOR = toolkit.createCustomCursor(imageIcon.getImage(), hotSpot, "Link");
//    }
    public static interface Link {
        public void link(Lookup.Provider lkpProviderOurs, Lookup.Provider lkpProviderTheirs);
    }

    protected Lookup.Provider lkpProvider;
    protected Link linkFunction;

    //====================================================
    //====================================================
    //=======
    //=======       Constructors
    //=======
    //====================================================
    //====================================================


    public DnDButton(Lookup.Provider lkpProvider) {
        init(lkpProvider);
    }

    public DnDButton(Lookup.Provider lkpProvider, Icon icon) {
        super(icon);
        init(lkpProvider);
    }

    public DnDButton(Lookup.Provider lkpProvider, String text) {
        super(text);
        init(lkpProvider);
    }

    public DnDButton(Lookup.Provider lkpProvider, String text, Icon icon) {
        super(text, icon);
        init(lkpProvider);
    }

    private void init(final Lookup.Provider lkpProvider) {
        this.lkpProvider = lkpProvider;
        this.linkFunction = new Link() {
            @Override
            public void link(Lookup.Provider lkpProviderOurs, Lookup.Provider lkpProviderTheirs) {}
        };

        DragGestureRecognizer dgr = DragSource.getDefaultDragSource()
                .createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_LINK, new DragGestureHandler());
        DropTarget dropTarget = new DropTarget(this, DnDConstants.ACTION_LINK, new DropTargetHandler(), true);
        this.setDropTarget(dropTarget);
    }

    public Link getLinkFunction() {
        return linkFunction;
    }

    public void setLinkFunction(Link linkFunction) {
        this.linkFunction = linkFunction;
    }

    //====================================================
    //====================================================
    //=======
    //=======       Transferable interface
    //=======
    //====================================================
    //====================================================

    public static class LookupProviderTransferable implements Transferable {
        public static final DataFlavor DATA_FLAVOR = new LookupProviderDataFlavor();
        protected Lookup.Provider lkpProvider;

        public LookupProviderTransferable(Lookup.Provider lkpProvider) {
            this.lkpProvider = lkpProvider;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DATA_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DATA_FLAVOR.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            Object value = isDataFlavorSupported(flavor) ? lkpProvider : null;
            if (value == null) {
                throw new UnsupportedFlavorException(flavor);
            }
            return value;
        }

        /**
     * This was a bad idea, if the component, where the button sits, gets relocated
     * then the parent might change, changing the underlying Lookup.Provider.
     * @param root the first Container to be checked, i.e. if {@code root} is itself
     * a Lookup.Provider, then it will be returned.
     * @return the first Lookup.Provider found, while bubbling up the JComponent
     * hierarchy.
     */
        private Lookup.Provider findLookupProvidingParent(Container root) {
            if (root == null) {
                return null;
            }
            if (root instanceof Lookup.Provider)
                return (Lookup.Provider)root;

            return findLookupProvidingParent(root.getParent());
        }
    }


    //====================================================
    //====================================================
    //=======
    //=======       DragSourceListener interface
    //=======
    //====================================================
    //====================================================
    protected class DragSourceHandler implements DragSourceListener {
        @Override
        public void dragEnter(DragSourceDragEvent dsde) {
        }

        @Override
        public void dragOver(DragSourceDragEvent dsde) {
        }

        @Override
        public void dropActionChanged(DragSourceDragEvent dsde) {
        }

        @Override
        public void dragExit(DragSourceEvent dse) {
            //setCursor(DragSource.DefaultLinkNoDrop);
        }

        @Override
        public void dragDropEnd(DragSourceDropEvent dsde) {
            //when the drag finishes, then repaint the DnDButton
            //so it doesn't look like it has still been pressed down
            ButtonModel m = DnDButton.this.getModel();
            m.setPressed(false);
            m.setRollover(false);
            m.setSelected(false);
            revalidate();
            repaint();
        }
    }
    

    
    //====================================================
    //====================================================
    //=======
    //=======       DragGestureListener interface
    //=======
    //====================================================
    //====================================================
    protected class DragGestureHandler implements DragGestureListener {
        @Override
        public void dragGestureRecognized(DragGestureEvent dge) {
            Transferable t = new LookupProviderTransferable(lkpProvider);
            DragSource source = dge.getDragSource();
            source.startDrag(dge, null, t, new DragSourceHandler());
        }
    }

    


    //====================================================
    //====================================================
    //=======
    //=======       DropTargetListener interface
    //=======
    //====================================================
    //====================================================
    protected class DropTargetHandler implements DropTargetListener {

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {}
        @Override
        public void dragOver(DropTargetDragEvent dtde) {}
        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {}
        @Override
        public void dragExit(DropTargetEvent dte) {}

        @Override
        public void drop(DropTargetDropEvent dtde) {
            Transferable t = dtde.getTransferable();
            Lookup.Provider lkpProviderOther = tryGetDroppedLookupProvider(t);
            if (lkpProviderOther != null) {
                dtde.acceptDrop(DnDConstants.ACTION_LINK);
                linkFunction.link(lkpProvider, lkpProviderOther);
                return;
            }
            dtde.rejectDrop();
        }
     
        private Lookup.Provider tryGetDroppedLookupProvider(Transferable t) {
            DataFlavor acceptedFlavor = LookupProviderTransferable.DATA_FLAVOR;
            if (t.isDataFlavorSupported(acceptedFlavor)) {
                try {
                    Object transferData = t.getTransferData(acceptedFlavor);
                    if (transferData instanceof Lookup.Provider) {
                        Lookup.Provider lkpProviderOther = (Lookup.Provider) transferData;
                        Lookup.Provider lkpProviderThis = DnDButton.this.lkpProvider;
                        if (!lkpProviderThis.equals(lkpProviderOther)) {
                            return lkpProviderOther;
                        }
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
            return null;
        }
    }
}
