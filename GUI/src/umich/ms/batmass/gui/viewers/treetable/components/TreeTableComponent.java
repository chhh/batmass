/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.treetable.components;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import org.netbeans.swing.outline.RenderDataProvider;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import umich.ms.batmass.data.core.lcms.features.data.BMOutlineModel;
import umich.ms.batmass.gui.core.api.BMComponentJPanel;
import umich.ms.batmass.gui.core.api.comm.eventbus.ViewerLinkSupport;
import umich.ms.batmass.nbputils.SwingHelper;

/**
 *
 * @author Dmitry Avtonomov
 */
public class TreeTableComponent extends BMComponentJPanel implements LookupListener {

    // main display components
    protected TreeTableToolbar toolbar;
    protected TreeTable table;

    // message bus
    protected ViewerLinkSupport linkSupport;

    // data to be displayed
    protected Lookup.Result<BMOutlineModel> dataResult;

    public TreeTableComponent() {
        super();
        constructor();
    }

    public TreeTableComponent(Lookup lookup) {
        super(lookup);
        constructor();
    }

    /** To be called in constructor only. */
    private void constructor() {
        ic.add(ic); // needed for D&D linking between viewers
        initComponents();

        // listening for data in our lookup
        dataResult = lkp.lookupResult(BMOutlineModel.class);
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

                table = new TreeTable();
                table.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                JScrollPane scrollPane = new JScrollPane(table);
                add(scrollPane, BorderLayout.CENTER);

                linkSupport = new ViewerLinkSupport(
                        Collections.singleton(TreeTableComponent.this), // highlight components
                        Collections.singleton(table.getBusHandler()), // subscribers (have @Handler methods to recieve messages)
                        Collections.singleton(table.getBusHandler()),
                        TreeTableComponent.this
                );

                toolbar = new TreeTableToolbar(linkSupport);
                add(toolbar, BorderLayout.NORTH);
            }
        };
        SwingHelper.invokeOnEDTSynch(runnable);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        initComponents();
        Collection<? extends BMOutlineModel> tableModels = dataResult.allInstances();
        if (tableModels.isEmpty()) {
            return; // have nothing to display
        }
        if (tableModels.size() > 1) {
            throw new IllegalStateException("Tree Table recieved more than one OutlineModel in its lookup, should not happen.");
        }

        BMOutlineModel model = tableModels.iterator().next();
        RenderDataProvider renderDataProvider = model.getRenderDataProvider();
        if (renderDataProvider != null) {
            table.setRenderDataProvider(renderDataProvider);
        }
        table.setModel(model);
        toolbar.setActivated(true);
    }
}
