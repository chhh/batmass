/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.viewers.featuretable.components;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import umich.ms.batmass.gui.core.api.BMComponentJPanel;
import umich.ms.batmass.gui.core.api.comm.eventbus.ViewerLinkSupport;
import umich.ms.batmass.nbputils.SwingHelper;

/**
 * A generic tabular viewer, which listens for {@link TableModel} in its lookup, and initiates
 * an ETable with this model.
 * @author Dmitry Avtonomov
 */
public class FeatureTableComponent extends BMComponentJPanel implements LookupListener {

    // main display components
    protected FeatureTableToolbar toolbar;
    protected FeatureTable table;

    // message bus
    protected ViewerLinkSupport linkSupport;

    // data to be displayed
    protected Lookup.Result<TableModel> dataResult;

    public FeatureTableComponent() {
        super();
        constructor();
    }

    public FeatureTableComponent(Lookup lookup) {
        super(lookup);
        constructor();
    }

    /** To be called in constructor only. */
    private void constructor() {
        ic.add(ic); // needed for D&D linking between viewers
        initComponents();

        // listening for data in our lookup
        dataResult = lkp.lookupResult(TableModel.class);
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

                table = new FeatureTable();
                table.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                JScrollPane scrollPane = new JScrollPane(table);
                add(scrollPane, BorderLayout.CENTER);

                linkSupport = new ViewerLinkSupport(
                        Collections.singleton(FeatureTableComponent.this), // highlight components
                        Collections.singleton(table.getBusHandler()), // subscribers (have @Handler methods to recieve messages)
                        Collections.singleton(table.getBusHandler()),
                        FeatureTableComponent.this
                );

                toolbar = new FeatureTableToolbar(linkSupport);
                add(toolbar, BorderLayout.NORTH);
            }
        };
        SwingHelper.invokeOnEDTSynch(runnable);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        initComponents();
        Collection<? extends TableModel> tableModels = dataResult.allInstances();
        if (tableModels.isEmpty()) {
            return; // have nothing to display
        }
        if (tableModels.size() > 1) {
            throw new IllegalStateException("Feature Table recieved more than one Table Model in its lookup, should not happen.");
        }

        TableModel model = tableModels.iterator().next();
        table.setModel(model);
        toolbar.setActivated(true);
    }

}
