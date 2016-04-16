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
