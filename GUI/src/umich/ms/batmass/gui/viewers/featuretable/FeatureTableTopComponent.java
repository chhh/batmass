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
package umich.ms.batmass.gui.viewers.featuretable;

import java.awt.BorderLayout;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ImageIcon;
import javax.swing.table.TableModel;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.BaseProgressUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import umich.ms.batmass.data.core.api.DataLoadingException;
import umich.ms.batmass.data.core.lcms.features.data.FeatureTableModelData;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.batmass.gui.viewers.featuretable.components.FeatureTableComponent;
import umich.ms.batmass.nbputils.SwingHelper;

/**
 * A tabular viewer component, that is based on ETable from NetBeans. Supports
 * multi-sorting and column selection. Filtering rows is only supported from code,
 * there is no mouse/keyboard events or GUI for that.
 * @author Dmitry Avtonomov
 */
@ConvertAsProperties(dtd = "-//umich.ms.batmass.gui.viewers.featuretable//FeatureTableTopComponent//EN", autostore = false)
public class FeatureTableTopComponent extends BMTopComponent {

    protected FeatureTableComponent featureTableComponent;

    @StaticResource
    private static final String ICON_PATH = "umich/ms/batmass/gui/resources/list.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_PATH, false);

    public FeatureTableTopComponent() {
        super();
        initComponents();
        setIcon(ICON.getImage());
        this.setFocusable(true);
    }


    /**
     * Should not be called from EDT.
     * @param data
     */
    @SuppressWarnings({"rawtypes"})
    public void setData(final FeatureTableModelData<?> data) {
        // remove whatever data we had
        removeFromLookup(FeatureTableModelData.class);

        // preparations on AWT thread, before fiddling with data
        final Runnable preDataLoaded;
        preDataLoaded = new Runnable() {
            @Override public void run() {
                makeBusy(true);
                initComponents();
            }
        };

        final AtomicBoolean isDataLoadSuccess = new AtomicBoolean(true);
        // undo the busy state of the top component and activate it
        final Runnable postDataLoaded = new Runnable() {
            @Override public void run() {
                if (!isDataLoadSuccess.get()) {
                    // if the data hasn't been loaded successfully
                    // just close the component
                    FeatureTableTopComponent.this.close();
                    return;
                }
                URI uri = data.getSource().getOriginURI();
                Path path = Paths.get(uri);
                setDisplayName(path.getFileName().toString());
                TableModel tm = data.create();
                featureTableComponent.addToLookup(tm);
                makeBusy(false);
                requestActive();
            }
        };

        String progressHandleName = data.getSource().getOriginURI().toString();
        final ProgressHandle ph = ProgressHandle.createHandle(progressHandleName);

        final Runnable loadData = new Runnable() {
            @Override public void run() {
                try {
                    data.load(featureTableComponent);
                } catch (DataLoadingException ex) {
                    Exceptions.printStackTrace(ex);
                    isDataLoadSuccess.set(false);
                } finally {
                    ph.finish();
                }
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

                featureTableComponent = new FeatureTableComponent();
                add(featureTableComponent, BorderLayout.CENTER);
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
