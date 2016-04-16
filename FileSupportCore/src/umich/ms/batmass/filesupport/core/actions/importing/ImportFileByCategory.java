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
package umich.ms.batmass.filesupport.core.actions.importing;

import java.awt.Component;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.loaders.DataFolder;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import umich.ms.batmass.filesupport.core.api.ExternalFileImporter;
import umich.ms.batmass.filesupport.core.api.FileTypeResolverUtils;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;

/**
 *
 * @author Dmitry Avtonomov
 */
public abstract class ImportFileByCategory
    extends AbstractAction
    implements LookupListener, ContextAwareAction {

    private Lookup context;
    private volatile Lookup.Result<DataFolder> lkpResult;

    public ImportFileByCategory() {
        this(Utilities.actionsGlobalContext());
    }

    public ImportFileByCategory(Lookup context) {
        this.context = context;
        putValue(Action.NAME, getActionName());
    }

    /**
     * This string will be used in the context menu for this action.
     * @return
     */
    public abstract String getActionName();
    /**
     * A more concrete type of {@link FileParser}, so that proper file filters
     * could be constructed for the filechooser.
     * @return
     */
    public abstract String getFileCategory();

    /**
     * It's in here, that we determine the activation condition. By default
     * a DataFolder must be in ActionsGlobalContext, which means, that a
     * DataNode for a folder must be selected.
     * Make sure we're running on EDT.
     */
    protected void init() {
        assert SwingUtilities.isEventDispatchThread() : "This shall be called only from AWT thread (EDT)";

        Lookup.Result<DataFolder> tmp = lkpResult;
        if (tmp == null) {
            synchronized (this) {
                tmp = lkpResult;
                if (tmp == null) {
                    // The thing we want to listen for the presence or absence of
                    // in the global selection
                    tmp = context.lookupResult(DataFolder.class);
                    lkpResult = tmp;
                    tmp.addLookupListener(this);
                    resultChanged(null);
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        init();
        return lkpResult.allInstances().size() == 1;
    }

    /**
     * 
     * @param fcBuilder
     * @return 
     */
    protected File[] showMultiOpenDialog(FileChooserBuilder fcBuilder) {
        JFileChooser chooser = fcBuilder.createFileChooser();
        BMFileView bmFileView = new BMFileView(getFileCategory());
        chooser.setFileView(bmFileView);
        chooser.setMultiSelectionEnabled(true);
        int result = chooser.showOpenDialog(findDialogParent());
        if (JFileChooser.APPROVE_OPTION == result) {
            File[] files = chooser.getSelectedFiles();
            return files == null ? new File[0] : files;
        } else {
            return null;
        }
    }

    /**
     * Tries to find an appropriate component as a parent for the file chooser.
     * @return some Component: the focus owner, the active window or the app frame
     */
    private Component findDialogParent() {
        Component parent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (parent == null) {
            parent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        }
        if (parent == null) {
            Frame[] f = Frame.getFrames();
            parent = f.length == 0 ? null : f[f.length - 1];
        }
        return parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        init();

        // get the folder on which the action was invoked
        Collection<? extends DataFolder> folders = lkpResult.allInstances();
        // getting a single folder like this is a bit excessive, as the action
        // is only enabled when there is just one one DataFolder in actionGlobalContext
        // just being on the safe side
        DataFolder folder;
        if (folders.isEmpty()) {
            return;
        }
        folder = folders.iterator().next();

        // this step polls the available parsers
        List<BMFileFilter> fileFilters = createFileFilters(getFileCategory());

        // build the file chooser
        FileChooserBuilder fcBuilder = new FileChooserBuilder(this.getClass())
                .setTitle(getActionName())
                .setApproveText("Add file(s)")
                .setFilesOnly(false);
        // set the file-filters for the file chooser
        for (FileFilter filter : fileFilters) {
            fcBuilder.addFileFilter(filter);
        }
        // The first FileNameExtensionFilter that we return is the one for all
        // file types, that have parsers.
        fcBuilder.setFileFilter(fileFilters.get(0));
        // TODO: this doesn't make any sense really, because eventually, no parser
        //       will be found for any imported file, other than those specified in FileFilters
        fcBuilder.setAcceptAllFileFilterUsed(true);

        File[] toAdd = showMultiOpenDialog(fcBuilder);
        if (toAdd == null) {
            return;
        }
        List<File> filesWithNoResolver = new ArrayList<>();
        for (File f : toAdd) {
            List<FileTypeResolver> resolvers = FileTypeResolverUtils.findTypeResolvers(f.getAbsolutePath(), getFileCategory());
            if (!resolvers.isEmpty()) {
                FileTypeResolver resolver = resolvers.get(0);
                try {
                    ExternalFileImporter.importFile(f, folder, resolver.getType(), resolver.getCategory());
                } catch (IOException | ConfigurationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                filesWithNoResolver.add(f);
            }

        }
        if (!filesWithNoResolver.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            msg.append("No installed FileTypeResolvers found for files:\n");
            int cnt = 0;
            for (File f: filesWithNoResolver) {
                msg.append("\t").append(f.getAbsolutePath()).append("\n");
                if (++cnt >= 10) {
                    msg.append("\t...");
                    break;
                }
            }
            NotifyDescriptor d = new NotifyDescriptor.Message(msg.toString());
            DialogDisplayer.getDefault().notify(d);
        }
    }

    private List<BMFileFilter> createFileFilters(String category) {
        List<FileTypeResolver> resolvers = FileTypeResolverUtils.getTypeResolvers(category);
        String baseDesc = getCategoryDisplayName();
        LinkedList<BMFileFilter> filters = new LinkedList<>();
        if (resolvers.isEmpty()) {
            filters.add(new BMFileFilter(null) {
                @Override public String getShortDescription() {
                    return  "UNDEFINED";
                }
                @Override public String getDescription() {
                    return "No resolvers were found for this filetype category";
                }
            });
            return filters;
        }
        
        for (FileTypeResolver resolver : resolvers) {
            filters.add(resolver.getFileFilter());
        }
        
        List<String> shortDescs = new ArrayList<>(filters.size());
        for (BMFileFilter bmff : filters) {
            shortDescs.add(bmff.getShortDescription());
        }
        Collections.sort(shortDescs);

        // creating the composite filter for all recognized files
        StringBuilder sb = new StringBuilder();
        sb.append(baseDesc).append(" (");
        sb.append(shortDescs.get(0));
        if (shortDescs.size() > 1) {
            for (int i = 1; i < shortDescs.size(); i++) {
                sb.append(", ").append(shortDescs.get(i));
            }
        }
        sb.append(")");
        
        List<IOFileFilter> ioFileFilters = new ArrayList<>(filters.size());
        for (BMFileFilter bmff : filters) {
            ioFileFilters.add(bmff.getFileFilter());
        }
        
        // add a filter that allows showing directories when "all supported files" option in the filechooser is selected
        ioFileFilters.add(new BMDirectoryFileFilter().getFileFilter());

        OrFileFilter orFileFilter = new OrFileFilter(ioFileFilters);
        final String allFiltersDesc = sb.toString();
        BMFileFilter combinedFilter = new BMFileFilter(orFileFilter) {
            @Override public String getShortDescription() {
                return allFiltersDesc;
            }
            
            @Override public String getDescription() {
                return "Any supported file";
            }
        };
        filters.addFirst(combinedFilter);
        return filters;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpResult.allInstances().isEmpty());
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        // TODO: WARNING: ACHTUNG: this is a very dirty hack, allowing for easier
        // inheritance, people won't need to provide
        @SuppressWarnings("rawtypes")
        Constructor<? extends ImportFileByCategory> constructor;
        try {
            constructor = this.getClass().getConstructor(Lookup.class);
            return constructor.newInstance(actionContext);
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            String msg = "In a subclass of ImportLCMSByResolver, you MUST provide"
                    + " a constructor taking a Lookup as its single parameter."
                    + "If you're a user of the application, please notify the "
                    + "developer.";
            Exceptions.printStackTrace(new IllegalStateException(msg, ex));
        }
        //return new this(actionContext);
        return null;
    }

    /**
     * How the category name should be presented in the file-chooser.
     * @return non-null
     */
    public abstract String getCategoryDisplayName();
}
