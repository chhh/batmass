/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.outline.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Collection;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;
import org.netbeans.swing.outline.Outline;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import umich.ms.batmass.gui.core.api.BMComponentJPanel;
import umich.ms.batmass.gui.core.api.comm.eventbus.ViewerLinkSupport;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.batmass.nbputils.SwingHelper;

/**
 *
 * @author Dmitry Avtonomov
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OutlineComponent extends BMComponentJPanel 
    implements LookupListener, ExplorerManager.Provider {

    // main display components
    protected OutlineToolbar toolbar;
    protected OutlineTable table;

    // message bus
    protected ViewerLinkSupport linkSupport;

    // data to be displayed
    protected Lookup.Result<ChildFactory> dataResult;
    protected ExplorerManager em;
    protected Node root;
    protected Node rootDefault;
    protected Children kids;

    public OutlineComponent() {
        super();
        constructor();
    }

    public OutlineComponent(Lookup lookup) {
        super(lookup);
        constructor();
    }

    /** To be called in constructor only. */
    private void constructor() {
        ic.add(ic); // needed for D&D linking between viewers
        initComponents();

        em = new ExplorerManager();
        rootDefault = new AbstractNode(Children.LEAF);
        rootDefault.setDisplayName("root");
        root = rootDefault;
        em.setRootContext(root);
        Outline outline = table.getOutline();
        outline.setRootVisible(false);
        outline.setPopupUsedFromTheCorner(true);
        outline.setFullyNonEditable(true);
        outline.setDefaultRenderer(Node.Property.class, new CustomOutlineCellRenderer());
        TableColumnModel columnModel = outline.getColumnModel();
        ETableColumn column = (ETableColumn) columnModel.getColumn(0);
        ((ETableColumnModel) columnModel).setColumnHidden(column, true);

        // listening for data in our lookup
        dataResult = lkp.lookupResult(ChildFactory.class);
        dataResult.addLookupListener(this);
    }


    /**
     * This method is automatically called on EDT.
     */
    private void initComponents() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                removeAll();
                setLayout(new BorderLayout());

                table = new OutlineTable();
                table.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                JScrollPane scrollPane = new JScrollPane(table);
                add(scrollPane, BorderLayout.CENTER);

                linkSupport = new ViewerLinkSupport(
                        Collections.singleton(OutlineComponent.this), // highlight components
                        Collections.singleton(table.getBusHandler()), // subscribers (have @Handler methods to recieve messages)
                        Collections.singleton(table.getBusHandler()),
                        OutlineComponent.this
                );

                toolbar = new OutlineToolbar(linkSupport);
                add(toolbar, BorderLayout.NORTH);
            }
        };
        SwingHelper.invokeOnEDTSynch(runnable);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        initComponents();
        Collection<? extends ChildFactory> childFactories = dataResult.allInstances();
        if (childFactories.isEmpty()) {
            em.setRootContext(rootDefault);
            toolbar.setActivated(false);
            return; // have nothing to display
        }
        if (childFactories.size() > 1) {
            throw new IllegalStateException("Outline Component recieved more than one ChildFactory in its lookup, should not happen.");
        }

        table.setPropertyColumns(
                "mzMono", "m/z mono",
                "charge", "Charge",
                "mzLo", "m/z lo",
                "mzHi", "m/z hi",
                "rtLo", "RT lo",
                "rtHi", "RT hi",
                "identified", "Identified",
                "name", "Name");

        ChildFactory childFactory = childFactories.iterator().next();
        kids = Children.create(childFactory, true);
        root = new AbstractNode(kids);
        em.setRootContext(root);

        TableColumnModel columnModel = table.getOutline().getColumnModel();
        ETableColumn column = (ETableColumn) columnModel.getColumn(0);
        ((ETableColumnModel) columnModel).setColumnHidden(column, true);
        table.getOutline().setDefaultRenderer(Node.Property.class, new CustomOutlineCellRenderer());

        // TODO: ACHTUNG: never checked if the selected nodes are actually proxied
        // by the top component
        //Put the Nodes into the Lookup of the TopComponent,
        //so that the Properties window will be synchronized:
        Lookup explorerManagerLookup = ExplorerUtils.createLookup(em, getActionMap());
        Container parent = getParent();
        if (parent instanceof BMTopComponent) {
            BMTopComponent tc = (BMTopComponent)parent;
            tc.setProxiedLookup(explorerManagerLookup);
        }
        
        toolbar.setActivated(true);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }
}
