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
package umich.ms.batmass.gui.viewers.treetable;

import java.awt.BorderLayout;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.api.progress.BaseProgressUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Exceptions;
import static org.openide.windows.TopComponent.PERSISTENCE_NEVER;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.data.core.lcms.features.data.TreeTableModelData;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.batmass.gui.viewers.treetable.components.TreeTableComponent;
import umich.ms.batmass.nbputils.SwingHelper;

/**
 * This was supposed to be a viewer based on Outline from NetBeans.
 * Not implemented.
 * @author Dmitry Avtonomov
 */
@ConvertAsProperties(dtd = "-//umich.ms.batmass.gui.viewers.treetable//TreeTableTopComponent//EN", autostore = false)
public class TreeTableTopComponent extends BMTopComponent {
    protected TreeTableComponent outlineComponent;

    public TreeTableTopComponent() {
        super();
        initComponents();
        this.setFocusable(true);
    }


    @SuppressWarnings({"rawtypes"})
    public void setData(final TreeTableModelData<?> data) {
        // remove whatever data we had
        removeDataFromLookup(TreeTableModelData.class);

        // preparations on EDT before loading data
        final Runnable preDataLoaded;
        preDataLoaded = new Runnable() {
            @Override public void run() {
                makeBusy(true);
                initComponents();
            }
        };

        final AtomicBoolean isDataLoadSuccess = new AtomicBoolean(true);
        // post-loading on EDT
        // undo the busy state of the top component and activate it
        final Runnable postDataLoaded = new Runnable() {
            @Override public void run() {
                if (!isDataLoadSuccess.get()) {
                    // if the data hasn't been loaded successfully
                    // just close the component
                    TreeTableTopComponent.this.close();
                    return;
                }

                URI uri = data.getSource().getOriginURI();
                Path path = Paths.get(uri);
                setDisplayName(path.getFileName().toString());
                outlineComponent.addToLookup(data.create());


                makeBusy(false);
                requestActive();
            }
        };

        String progressHandleName = data.getSource().getOriginURI().toString();
        final ProgressHandle ph = ProgressHandle.createHandle(progressHandleName);
        

        // the loading step, off-EDT
        final Runnable loadData = new Runnable() {
            @Override public void run() {

                try {
                    data.load(outlineComponent);
                } catch (DataLoadingException ex) {
                    Exceptions.printStackTrace(ex);
                    isDataLoadSuccess.set(false);
                }

                ph.finish();
                SwingHelper.invokeOnEDT(postDataLoaded);
            }
        };


        SwingHelper.invokeOnEDTSynch(preDataLoaded);
        String dialogTitle = "Loading data";
        BaseProgressUtils.runOffEventThreadWithProgressDialog(loadData, dialogTitle, ph, false, 0, 300);
        ph.start();

    }


    private void initComponents() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                removeAll();
                setLayout(new BorderLayout());

                outlineComponent = new TreeTableComponent();
                add(outlineComponent, BorderLayout.CENTER);
                revalidate();
            }
        };
        SwingHelper.invokeOnEDTSynch(runnable);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    void writeProperties(Properties p) {

    }

    void readProperties(Properties p) {

    }
}
