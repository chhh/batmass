/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.configuration.ConfigurationException;
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
        List<FileFilter> fileFilters = createFileFilters(getFileCategory());

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

    private List<FileFilter> createFileFilters(String category) {
        Set<String> allExts = FileTypeResolverUtils.getExtsSupportedByAllResolvers(category);
        LinkedList<FileFilter> filters = new LinkedList<>();
        String baseDesc = getCategoryDisplayName();

        if (allExts.isEmpty()) {
            filters.add(new FileNameExtensionFilter(baseDesc + " (No resolvers installed)", "unidentified"));
            return filters;
        }


        String[] allExtsArr = allExts.toArray(new String[allExts.size()]);
        Arrays.sort(allExtsArr);

        // separate filters for each extension
        for (String ext : allExtsArr)
            filters.add(new FileNameExtensionFilter(ext, ext));

        // creating the composite filter for all recognized files
        StringBuilder sb = new StringBuilder();
        sb.append(baseDesc).append(" (");
        sb.append(allExtsArr[0]);
        if (allExtsArr.length > 1) {
            for (int i = 1; i < allExtsArr.length; i++) {
                sb.append(", ").append(allExtsArr[i]);
            }
        }
        sb.append(")");
        FileNameExtensionFilter filterAllExts = new FileNameExtensionFilter(sb.toString(), allExtsArr);
        filters.addFirst(filterAllExts);
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
