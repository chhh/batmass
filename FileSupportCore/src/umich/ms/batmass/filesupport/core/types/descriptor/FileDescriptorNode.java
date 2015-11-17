/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.types.descriptor;

import java.awt.Image;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.filesupport.core.spi.nodes.FileNodeInfo;
import umich.ms.batmass.gui.core.api.tc.BMTopComponent;
import umich.ms.batmass.nbputils.SwingHelper;
import umich.ms.batmass.nbputils.nodes.NodeSubmenuUtils;

/**
 * A node representing a {@link FileDescriptor}. Handles all the generic
 * operations, like constructing context menus etc.
 * @author Dmitry Avtonomov
 */
public class FileDescriptorNode extends DataNode {
    private final WeakReference<Project> project;
    private final List<FileNodeInfo> nodeInfos;
    private final FileTypeResolver fileTypeResolver;
    private final InstanceContent ic;
    private final ChildFactory<Path> childFactory;
    
    @StaticResource
    public static final String ICON_BASE_PATH_UNKNOWN_FILE = "umich/ms/batmass/filesupport/core/resources/question_square_16px.png";
    public static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH_UNKNOWN_FILE, false);

//    /**
//     * Using this constructor, a new separate instance content will be created
//     * in addition to the standard {@link DataNode} lookup, so you could put
//     * something in the lookup of only the node, but not the DataObject.
//     * @param dobj
//     */
//    public FileDescriptorNode(FileDescriptorDataObject dobj) {
//        this(dobj, new InstanceContent());
//    }

    /**
     * Using this constructor, a new separate instance content will be created
     * in addition to the standard {@link DataNode} lookup, so you could put
     * something in the lookup of only the node, but not the DataObject.
     * @param dobj
     * @param nodeInfos action paths will be taken from here
     * @param fileTypeResolver the resolver is needed to get the icon from it
     * @param project can be null, if you don't want project-type specific actions
     */
    public FileDescriptorNode(FileDescriptorDataObject dobj, List<FileNodeInfo> nodeInfos, 
            FileTypeResolver fileTypeResolver, Project project) {
        this(dobj, nodeInfos, fileTypeResolver, project, new InstanceContent());
    }

    /**
     * Using this constructor you can share InstanceContent of the lookup, used
     * by this Node, with InstanceContent of DataObject lookup.
     * @param dobj
     * @param ic this node's lookup will be a merge of the original
     * {@link DataNode} lookup and this instance content.
     */
    private FileDescriptorNode(FileDescriptorDataObject dobj, List<FileNodeInfo> nodeInfos,
            FileTypeResolver fileTypeResolver, Project project, InstanceContent ic) {
        super(dobj, Children.LEAF, new ProxyLookup(dobj.getLookup(), new AbstractLookup(ic)));
        this.fileTypeResolver = fileTypeResolver;
        this.project = new WeakReference<>(project);
        this.nodeInfos = nodeInfos;
        this.ic = ic;
        FileDescriptor desc = dobj.getDescriptor();
        if (desc != null) {
            ic.add(desc);
        }

        // this doesn't work, because the icons come from other modules and their paths are not resolved properly
        //this.setIconBaseWithExtension(fileTypeResolver.getIconPath());

        childFactory = new FileDescriptorNodeChildFactory();
        this.setChildren(Children.create(childFactory, false));
    }

    /**
     * Overridden, because {@code this.setIconBaseWithExtension(path) } doesn't work, when set in the constructor.
     * This is because the icons are likely located in other modules, and their paths don't resolve properly.
     * @param type
     * @return
     */
    @Override
    public Image getIcon(int type) {
        ImageIcon icon = fileTypeResolver.getIcon();
        if (icon == null) {
            icon = ICON;
        }
        return icon.getImage();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getHtmlDisplayName() {
        return getDataObject().getDescriptor().getPath().getFileName().toString();
    }

    /**
     * You can use this to put/remove items from the Node's lookup.
     *
     * @return
     */
    public InstanceContent getInstanceContent() {
        return ic;
    }

    /**
     * Convenience method to access lookup of the node.
     *
     * @param obj
     */
    public void addToLookup(Object obj) {
        ic.add(obj);
    }

    /**
     * Convenience method to access lookup of the node.
     *
     * @param obj
     */
    public void removeFromLookup(Object obj) {
        ic.remove(obj);
    }

    @Override
    public FileDescriptorDataObject getDataObject() {
        return (FileDescriptorDataObject) super.getDataObject();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] defaultActions = super.getActions(context);
        Project p = project.get();
        ArrayList<String> actionPaths = new ArrayList<>(20);
        if (nodeInfos != null) {
            for (FileNodeInfo info : nodeInfos) {
                List<String> infoPaths = info.getActionPaths(p);
                actionPaths.addAll(infoPaths);
            }
        } else {
            Exceptions.printStackTrace(new IllegalStateException(
                    "NodeInfos were null when creating a FileDescriptorNode"));
        }
        String[] paths = actionPaths.toArray(new String[actionPaths.size()]);
        Action[] createdActions = NodeSubmenuUtils.createActions(context, defaultActions, paths);
        return createdActions;
    }



    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();


        Sheet.Set setLinkedFile = Sheet.createPropertiesSet();
        setLinkedFile.setDisplayName("Linked file properties");
        setLinkedFile.setName("linked_file_properties");
        final FileDescriptor desc = getDataObject().getDescriptor();
        PropertySupport.ReadOnly<String> propOrigPath = new PropertySupport.ReadOnly<String>(
                "file_path", String.class, "Path to original file", "This is the path this descriptor links to") {
            @Override public String getValue() throws IllegalAccessException, InvocationTargetException {
                return desc.getPath().toAbsolutePath().toString();
            }
        };
        PropertySupport.ReadOnly<String> propSize = new PropertySupport.ReadOnly<String>(
                "file_size", String.class, "Size of original file", "The size of the file this descriptor links to") {
            @Override public String getValue() throws IllegalAccessException, InvocationTargetException {
                return String.format("%,.2f MB", (double)(desc.getSize()) / (1024 * 1024));
            }
        };
        

        setLinkedFile.put(propOrigPath);
        setLinkedFile.put(propSize);
        sheet.put(setLinkedFile);


        Sheet.Set setDescriptor = Sheet.createPropertiesSet();
        setDescriptor.setDisplayName("Descriptor properties");
        setDescriptor.setName("descriptor_properties");
        PropertySupport.ReadOnly<String> propUID = new PropertySupport.ReadOnly<String>(
                "desc_uid", String.class, "Descriptor UID", "Unique ID assigned to the descriptor at creation time") {
            @Override public String getValue() throws IllegalAccessException, InvocationTargetException {
                return desc.getUID();
            }
        };
        PropertySupport.ReadOnly<String> propDescPath = new PropertySupport.ReadOnly<String>(
                "desc_path", String.class, "Path to descriptor", "The path to the descriptor file") {
            @Override public String getValue() throws IllegalAccessException, InvocationTargetException {
                FileObject fo = getDataObject().getPrimaryFile();
                return fo.toURI().toASCIIString();
            }
        };
        

        setDescriptor.put(propDescPath);
        setDescriptor.put(propUID);
        sheet.put(setDescriptor);


//        try {
//            FileDescriptorDataObject descDataObject = getDataObject();
//            DataObject linkedDataObj = DataObject.find(FileUtil.toFileObject(desc.getPath().toFile()));
//            Sheet.Set setOrig = Sheet.createPropertiesSet();
//            //setOrig.setValue("tabName", "Original properties");
//            setOrig.setDisplayName("Properties from the original node");
//            setOrig.setName("props_copied");
//            PropertySet[] propertySets = linkedDataObj.getNodeDelegate().getPropertySets();
//            for (PropertySet propertySet : propertySets) {
//                Property<?>[] properties = propertySet.getProperties();
//                for (Property<?> property : properties) {
//                    setOrig.put(property);
//                }
//            }
//            sheet.put(setOrig);
//        } catch (DataObjectNotFoundException ex) {
//            Exceptions.printStackTrace(ex);
//        }

        return sheet;
    }

    @Override
    public void destroy() throws IOException {
        // close all windows, that might be using data from this node
        Collection<? extends Object> inLookup = getLookup().lookupAll(Object.class);
        
        // iterate over all opened BMTopComponents
        Set<TopComponent> openedTCs = WindowManager.getDefault().getRegistry().getOpened();
        for (final TopComponent tc : openedTCs) {
            if (!(tc instanceof BMTopComponent)) {
                continue; // we don't care about TCs not from BatMass
            }

            Collection<? extends Object> tcDatas = tc.getLookup().lookupAll(Object.class);
            if (tcDatas.isEmpty()) {
                continue;
            }

            outerloop:
            for (Object o : inLookup) {
                for (Object tcData : tcDatas) {
                    if (o == tcData) {
                        // close this TC and move to checking the next one
                        SwingHelper.invokeOnEDT(new Runnable() {
                            @Override public void run() { tc.close(); }
                        });
                        break outerloop;
                    }
                }
            }
        }


        super.destroy();
    }

    
    
    public class FileDescriptorNodeChildFactory extends ChildFactory.Detachable<Path> {

        @Override
        protected boolean createKeys(List<Path> toPopulate) {
            FileDescriptor descriptor = FileDescriptorNode.this.getDataObject().getDescriptor();
            toPopulate.addAll(descriptor.getChildren());
            return true;
        }

        @Override
        protected Node createNodeForKey(Path key) {
            FileObject keyFo = FileUtil.toFileObject(key.toFile());
            try {
                DataObject dobj = DataObject.find(keyFo);
                return dobj.getNodeDelegate();
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return new BadDescriptorNode(
                        BadDescriptorNode.TYPE.NO_DATALOADER,
                        FileUtil.getFileDisplayName(keyFo));
            }
        }

        @Override
        protected void addNotify() {
            super.addNotify();
        }

        @Override
        protected void removeNotify() {
            super.removeNotify();
        }
    }
}
