/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.types.descriptor;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import umich.ms.batmass.filesupport.core.api.FileTypeResolverUtils;
import umich.ms.batmass.filesupport.core.api.NodeInfoUtils;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.filesupport.core.spi.nodes.CapabilityProvider;
import umich.ms.batmass.filesupport.core.spi.nodes.FileNodeInfo;
import umich.ms.batmass.nbputils.lookup.LookupUtils;

@Messages({
    "LBL_FileDescriptor_LOADER=Files of FileDescriptor"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_FileDescriptor_LOADER",
        mimeType = "application/file-descriptor",
        extension = {"file_desc"}
)
@DataObject.Registration(
        mimeType = "application/file-descriptor",
        //iconBase = "umich/ms/batmass/filesupport/core/resources/question_square_16px.png",
        displayName = "#LBL_FileDescriptor_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/file-descriptor/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/application/file-descriptor/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/application/file-descriptor/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/application/file-descriptor/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class FileDescriptorDataObject extends MultiDataObject {
    
    FileDescriptor descriptor;
    InstanceContent ic;
    Lookup lkp;

    public FileDescriptorDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        descriptor = null;
        ic = new InstanceContent();
        lkp = new ProxyLookup(super.getLookup(), new AbstractLookup(ic));
        //registerEditor("application/file-descriptor", false);
        
        // we are not creating a FileDescriptor object by reading from primary file
        // here, because the descriptor might be broken, so we postpone that
        // until node creation in .createNodeDelegate()
        // This DataObject will not be used before that anyway.
    }

    public FileDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public Lookup getLookup() {
        return lkp;
    }

    public void addToLookup(Object o) {
        ic.add(o);
    }

    public void removeFromLookup(Object o) {
        ic.remove(o);
    }
    
    @Override
    protected int associateLookup() {
        return 1;
    }

    @Override
    protected Node createNodeDelegate() {
        FileDescriptor desc;
        // read the descriptor file
        try {
            desc = FileDescriptor.readFromFile(FileUtil.toFile(getPrimaryFile()));
        } catch (ConfigurationException ex) {
            Exceptions.printStackTrace(ex);
            return new BadFileDescriptorNode(
                    this,
                    BadFileDescriptorNode.TYPE.CORRUPT_DESCRIPTOR, 
                    FileUtil.getFileDisplayName(this.getPrimaryFile()));
        }
        // we finally set the descriptor we've read
        descriptor = desc;
        String descOrigFileName = descriptor.getPath().getFileName().toString();
        
        
        // check if we have an installed type resolvers
        String fileType = descriptor.getFileType();
        String fileCategory = descriptor.getFileCategory();
        List<FileTypeResolver> resolvers =
                FileTypeResolverUtils.getTypeResolvers(fileCategory, fileType);
        if (resolvers.isEmpty()) {
            return new BadFileDescriptorNode(
                    this,
                    BadFileDescriptorNode.TYPE.NO_RESOLVER, 
                    descOrigFileName);
        }
        FileTypeResolver ftr = resolvers.get(0);
        if (resolvers.size() > 1) {
            String msg = String.format("When loading DataObject for descriptor "
                    + "at [%s] [type=%s, category =%s] more than one resolver "
                    + "was found, which is currently not supported. Will use the "
                    + "first resolver from the list.",
                    descriptor.getPath(), fileType, fileCategory);
            Exceptions.printStackTrace(new IllegalStateException(msg));
        }

        Project project = FileOwnerQuery.getOwner(this.getPrimaryFile());
        List<FileNodeInfo> nodeInfos = NodeInfoUtils.findNodeInfos(ftr);

        // add capabilities from nodeInfos to the lookup of this DataObject
        for (FileNodeInfo info : nodeInfos) {
            List<String> capabilityProviderPaths = info.getCapabilityProviderPaths(project);
            for (String layerPath : capabilityProviderPaths) {
                Lookup capProvsLkp = LookupUtils.getLookupForPath(layerPath);
                Collection<? extends CapabilityProvider> capProvs = capProvsLkp
                        .lookupAll(CapabilityProvider.class);
                for (CapabilityProvider capProv : capProvs) {
                    capProv.addCapabilitiesToLookup(ic, descriptor);
                }
            }
        }
        
        return new FileDescriptorNode(this, nodeInfos, ftr, project);
    }

}
