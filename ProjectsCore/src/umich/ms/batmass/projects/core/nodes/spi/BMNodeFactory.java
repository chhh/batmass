/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.nodes.spi;

import java.lang.ref.WeakReference;
import java.util.Collection;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import umich.ms.batmass.nbputils.LayerUtils;
import umich.ms.batmass.projects.core.nodes.support.SingleFolderNodeList;
import umich.ms.batmass.projects.core.services.spi.ProjectSubfolderActionPathsProvider;
import umich.ms.batmass.projects.core.services.spi.ProjectSubfolderProvider;
import umich.ms.batmass.projects.core.util.BMProjectUtils;

/**
 * Base class for implementing Project Subfolder Node views. To use that (to
 * be able to create a node of this type) you'll first need to register a 
 * {@link ProjectSubfolderProvider} in the lookup of a project type.<br/>
 *
 * Don't forget to annotate implementaions with:<br/>
 * {@code @BMProjectSubfolderType(type = "your_type") }<br/>
 * 
 * Use {@link NodeFactory.Registration } annotation to register into project's 
 * lookup.<br/>
 *
 * See {@code NodeFactoryLCMSFiles }source code for an example of correct
 * registration and type annotation (it's in Projects (BatMass) module).
 *
 * @param <T>
 *
 * @author Dmitry Avtonomov
 */
public abstract class BMNodeFactory<T extends ProjectSubfolderProvider> implements NodeFactory {

    public static String TYPE_ANY = "batmass-nodefactory-any";

    private WeakReference<Project> projectRef;

    /**
     * Type of this factory/subfolder, used to construct paths in layer.
     * @return
     */
    public final String getType() {
        return BMProjectUtils.getSubfolderType(this.getClass());
    }

    /**
     * Default layer path for action registration.
     * @param type typically you should get this from {@link BMProjectUtils#getSubfolderType(java.lang.Class) }
     * @return
     */
    public static String getLayerSubfolderActionsPath(String type) {
        return LayerUtils.getLayerPath("ProjectSubfolders", type, "Actions");
    }
    
    /**
     * Default layer path for action registration for this NodeFactory type.
     * @return 
     */
    public String getLayerSubfolderActionsPath() {
        return LayerUtils.getLayerPath("ProjectSubfolders",
                BMProjectUtils.getSubfolderType(this.getClass()), "Actions");
    }

    /** Which {@link ProjectSubfolderProvider} to look up in project's lookup
     * @return  */
    public abstract Class<T> getProjectSubfolderProviderClass();

    /** The name of the node representing a sub-folder
     * @return  */
    public abstract String getDisplayName();

    /** Paths in layer from which all sub-folders will get their actions
     * @return  */
    public String[] getActionPaths() {

        // The default action paths for this
        String[] layerPaths = new String[]{
                BMNodeFactory.getLayerSubfolderActionsPath(this.getType()),
                BMNodeFactory.getLayerSubfolderActionsPath(BMNodeFactory.TYPE_ANY)
            };

        Project project = projectRef.get();
        if (project != null) { // technically it should never be null
            Collection<? extends ProjectSubfolderActionPathsProvider> pathProviders =
                    projectRef.get().getLookup().lookupAll(ProjectSubfolderActionPathsProvider.class);

            String[] aggregateLayerMappedPaths = BMProjectUtils
                    .aggregateLayerMappedPaths(
                            pathProviders, getProjectSubfolderProviderClass(), layerPaths);
            return aggregateLayerMappedPaths;
        }
        return layerPaths;
    }

    
    /**
     * If you don't like the default behavior of created nodes, you should override 
     * this method.
     * @param p
     * @return
     */
    @Override
    public NodeList<?> createNodes(Project p) {
        projectRef = new WeakReference<>(p);
        NodeList<?> errorNode = beforeCreateNodesCheck(p);
        if (errorNode != null) {
            return errorNode;
        }

        return new SingleFolderNodeList(p, getProjectSubfolderProviderClass(),
                getDisplayName(), getActionPaths());
    }

    /**
     * Convenience implementation, that checks that the project has a proper
     * ProjectSubfolderProvider in its lookup. Extend this class, override
     * {@link #createNodes(org.netbeans.api.project.Project) } and first call
     * <code>super.createNodes(p)</code>. If you get a non-null result - return it
     * immediately, if you get null - create a proper NodeList.
     * <b>WARNING:</b><br/>
     * Never return null in your implementation!!!
     * @param p
     * @return null, if no errors were found in Project, or a NodeList, that you
     * should return, in case of errors.
     */
    public NodeList<?> beforeCreateNodesCheck(Project p) {
        ProjectSubfolderProvider folderProvider = p.getLookup()
                .lookup(getProjectSubfolderProviderClass());

        if (folderProvider == null) {
            AbstractNode errorNode = new AbstractNode(Children.LEAF);
            Class<T> fp = getProjectSubfolderProviderClass();

            String errMsgShort = String.format("ERROR: An implementation of %s "
                    + "can't be found in project's lookup.", fp.getCanonicalName());
            errorNode.setDisplayName(errMsgShort);

            String errMsgLong = String.format("%s You must register a %s in your"
                    + "project's lookup. Use @ProjectServiceProvider annotation for that. "
                    + "Or drag&drop an instance in layer.xml from an existing project type, "
                    + "e.g. 'Projects/umich-ms-batmass-projects-types-proteomics-proteomicsproject/Lookup'"
                    + " to your project type similar .../Lookup folder.",
                    errMsgShort, fp.getSimpleName());
            Exceptions.printStackTrace(new IllegalStateException(errMsgLong));
            // Safety net - return a NodeList to make the error state clearly visible.
            // Also, we're not allowed to return null from this method.
            return NodeFactorySupport.fixedNodeList(errorNode);
        }
        return null;
    }
}
