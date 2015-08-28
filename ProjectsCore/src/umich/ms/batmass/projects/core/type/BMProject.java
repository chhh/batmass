/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.core.type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import umich.ms.batmass.nbputils.LayerUtils;
import umich.ms.batmass.nbputils.PathUtils;
import umich.ms.batmass.projects.core.services.spi.LayerPathProvider;
import umich.ms.batmass.projects.core.services.spi.ProjectServicesPathProvider;
import umich.ms.batmass.projects.core.type.defaultoperations.BMProjectDeleteOperation;
import umich.ms.batmass.projects.core.type.defaultoperations.BMProjectMoveOrRenameoperation;
import umich.ms.batmass.projects.core.util.BMProjectUtils;

/**
 * This should be used as a base-class for Project types that deal with MSFiles
 * as their main source of info.
 * Annotate your projects with @BMProjectType annotation AND specify a final
 * static field TYPE. The field is needed.
 *
 * @author dmitriya
 */
public abstract class BMProject implements Project {

    protected final FileObject projectDir;
    protected final ProjectState projectState;
    protected final InstanceContent ic;
    protected volatile Lookup lkp;
    /** Private lock, to avoid deadlocks in subclasses */
    private final Object syncLock = new Object();
    /** Property name for storing project display name in project's .properties file. */
    public static final String PROP_NAME = "project.name"; //NOI18N
    /** Property name for storing project description in project's .properties file. */
    public static final String PROP_DESCRIPTION = "project.description"; //NOI18N
    /** This name is to be used to register stuff in layer.xml that should be used by
     all project types. */
    public static final String TYPE_ANY = "batmass-project-any"; //NOI18N
    

    @SuppressWarnings("LeakingThisInConstructor")
    public BMProject(FileObject projectDir, ProjectState projectState) {
        this.projectDir = projectDir;
        this.projectState = projectState;
        this.ic = new InstanceContent();

        Lookup baseLookup = new AbstractLookup(ic);
        ic.add(this);
        ic.add(projectState);
        ic.add(loadProperties());
        ic.add(new BMProjectInfo(this));
        ic.add(new BMProjectActionProvider(this));
        ic.add(new BMProjectDeleteOperation(this));
        ic.add(new BMProjectMoveOrRenameoperation(this));
        
        // TODO: project properties should be editable, we need a customizer.
        // an old tutorial is available at: https://blogs.oracle.com/gridbag/entry/project_properties_gui_for_custom
//        ic.add(new CustomizerProvider() {
//
//            @Override
//            public void showCustomizer() {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        });
        
        this.lkp = baseLookup;
        // extension point for adding other possible service locations in layer.
        // register a LayerPathProvider in Projects/PROJECT_TYPE/Lookup via
        // @ProjectServiceProvider annotation.
        List<String> servicePaths = new ArrayList<>();
        Lookup anyPrjSvcsLkp = Lookups.forPath(getLayerServicesPath(TYPE_ANY));
        Lookup thisPrjSvcsLkp = Lookups.forPath(getLayerServicesPath(getType()));

        // WARNING: ACHTUNG: services found in previous lookups won't be available until
        // this call to .createCompositeLookup() because they are registered lazily!!!
        Lookup lkpWithServices = LookupProviderSupport
                .createCompositeLookup(baseLookup, new ProxyLookup(anyPrjSvcsLkp, thisPrjSvcsLkp));

        List<LayerPathProvider> pathProviders = new ArrayList<>();
        pathProviders.addAll(lkpWithServices.lookupAll(ProjectServicesPathProvider.class));
        for (LayerPathProvider pathProvider : pathProviders) {
            servicePaths.addAll(Arrays.asList(pathProvider.getPaths()));
        }

        // preparing to merge 2 base lookups + all addon lookups
        int lkpsCnt = servicePaths.size() + 2;
        Lookup[] lkps = new Lookup[lkpsCnt];
        lkps[0] = anyPrjSvcsLkp;
        lkps[1] = thisPrjSvcsLkp;
        for (int i=0; i < servicePaths.size(); i++) {
            lkps[i+2] = Lookups.forPath(servicePaths.get(i));
        }

        this.lkp = LookupProviderSupport.createCompositeLookup(baseLookup, new ProxyLookup(lkps));
    }

    /**
     * Gets the Configuration corresponding to the .properties file
     * ({@link #getProjectPropsFileName() }) stored in {@link #getProjectPropsDirectoryPath() }.
     *
     * @return
     */
    public PropertiesConfiguration getConfig() {
        PropertiesConfiguration config = lkp.lookup(PropertiesConfiguration.class);
        return config;
    }

    protected final void addToLookup(Object inst) {
        ic.add(inst);
    }

    public final void removeFromLookup(Object inst) {
        ic.remove(inst);
    }

    /**
     * Default definition of a string project type name representation.
     * @return project type name
     * @deprecated use annotations instead and the complementary methods
     */
    @Deprecated
    public final String getProjectType() {
        return getClass().getSimpleName();
    }

    public final String getType() {
        return BMProjectUtils.getProjectType(this.getClass());
    };

    /**
     * The default path that is formed, when you register a @ProjectServiceProvider
     * with type=projectType.
     * @param projectType
     * @return
     */
    public static final String getLayerServicesPath(String projectType) {
        return LayerUtils.getLayerPath("Projects", projectType, "Lookup");
    };

    /**
     * The default path where you should register actions for project nodes.
     * If you need to register an action for all project types use
     * {@link #TYPE_ANY} as projectType.
     * @param projectType
     * @return
     */
    public static final String getLayerProjectActionsPath(String projectType) {
        return LayerUtils.getLayerPath("Projects", projectType, "Actions");
    }

    /**
     * Standard path where {@link NodeFactory}s registered with @NodeFactory.Registration
     * annotation (with projectType=your-type) are put.
     * @param projectType
     * @return
     */
    public static final String getLayerNodesPath(String projectType) {
        return LayerUtils.getLayerPath("Projects", projectType, "Nodes");
    }

    /**
     * Project type icon
     *
     * @return the ICON
     */
    public abstract ImageIcon getIcon();

    /**
     * A project is detected by presence of this folder. All project specific
     * data should be stored in it.
     *
     * @return relative path to directory in project structure, e.g.
     * "batmassproject".
     */
    protected abstract String getProjectPropsDirectoryPath();

    /**
     * Stores basic project properties. Whenever it is modified,
     * {@code projectState} should be changed using {@link ProjectState#markModified()
     * }.
     *
     * @return name of the properties file, e.g. "proteomics.properties".
     */
    protected abstract String getProjectPropsFileName();

    /**
     * Properties file, stores the basic project info (display name, description..).
     * If you're adding some new properties in subclasses, use public constants
     * for property names, preferrably with dot-notation (e.g. "project.some_prop").
     * @return
     */
    public FileObject getProjectPropertiesFile() {
        FileObject propFile = getProjectPropsDirectory().getFileObject(getProjectPropsFileName());
        if (propFile == null) {
            try {
                propFile = getProjectPropsDirectory().createData(getProjectPropsFileName());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return propFile;
    }

    /**
     * Sub-directory in project directory, where project specific meta-data is
     * to be stored.
     * @return
     */
    public FileObject getProjectPropsDirectory() {
        FileObject propsDir = getProjectDirectory().getFileObject(getProjectPropsDirectoryPath());
        if (propsDir == null) {
            try {
                propsDir = FileUtil.createFolder(getProjectDirectory(), getProjectPropsDirectoryPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return propsDir;
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    /**
     * Used only during project initialization. These properties are to be
     * provided in project's lookup.
     * @return
     */
    private PropertiesConfiguration loadProperties() {
        synchronized (syncLock) {
            FileObject projPropFob = projectDir.getFileObject(getProjectPropsDirectoryPath()).getFileObject(getProjectPropsFileName());

            if (projPropFob != null) {
                PropertiesConfiguration propConf;
                try {
                    propConf = new PropertiesConfiguration(FileUtil.toFile(projPropFob));
                    propConf.addConfigurationListener(new ConfigChangeListener(projectState));
                    return propConf;
                } catch (ConfigurationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return new PropertiesConfiguration();
        }
    }

    /**
     * Create/get a folder inside the project folder.
     * @param folderPath path relative to project directory (e.g. "lcms_files")
     * @param createIfNotExists should it be created if not yet exists?
     * @return null, if the directory could not be created or did not exist
     */
    public FileObject getProjectUtilityFolder(String folderPath, boolean createIfNotExists) {
        synchronized (syncLock) {
            FileObject res = projectDir.getFileObject(folderPath);
            if (res == null && createIfNotExists) {
                try {
                    res = FileUtil.createFolder(getProjectDirectory(), folderPath);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return res;
        }
    }

    protected class ConfigChangeListener implements ConfigurationListener {

        private final ProjectState projectState;

        ConfigChangeListener(ProjectState projectState) {
            this.projectState = projectState;
        }

        @Override
        public void configurationChanged(ConfigurationEvent event) {
            if (!event.isBeforeUpdate()) {
                // only display events after the modification was done
                projectState.markModified();

                // get the root node from LogicalViewProvider and update the project display name
                BMProjectLogicalView lvp = BMProject.this.getLookup().lookup(BMProjectLogicalView.class);
                BMProjectLogicalView.BMProjectNode rootNode = lvp.getRootNode();
                rootNode.setDisplayName(rootNode.getDisplayName());

//                NotifyDescriptor.Message msg = new NotifyDescriptor.Message("in ConfigChangeListener.configurationChanged()");
//                DialogDisplayer.getDefault().notify(msg);
            }
        }

    }
}