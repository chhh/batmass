/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.spi.filetypes;

import javax.swing.ImageIcon;
import org.openide.nodes.AbstractNode;
import umich.ms.batmass.filesupport.core.actions.importing.BMFileFilter;

/**
 * Maps a file-type to a specific category.<br/>
 * Interface, which should be shared by other services, providing capabilities
 * for the same types of files. E.g. MzXML parser provider and MzXML node info
 * provider should preferably share the same instance of this interface.
 * @author Dmitry Avtonomov
 */
public interface FileTypeResolver {
    /** Represents any file-type in the system. Used in layer registrations. */
    public static final String TYPE_ANY = "batmass-type-any";
    /** Represents any file-category in the system. Used in layer registrations. */
    public static final String CATEGORY_ANY = "batmass-category-any";
    
    /** This path is where NodeInfos should be registered. */
    public static final String LAYER_REGISTRATION_PATH = "BatMass/FileTypeResolvers";
    
    /**
     * THe filter that will be used in FileChooser for this resolver.
     * @return 
     */
    public BMFileFilter getFileFilter();
    
    /**
     * Reports if this resolver supports only single files, or can accept whole directories.
     * @return 
     */
    boolean isFileOnly();
    
    /**
     * Category of the file. E.g. LCMSFiles or PeptideIdentifications
     * @return 
     */
    String getCategory();
    
    /**
     * The concrete type of file, must be some unique name.
     * @return 
     */
    String getType();

    /**
     * Needed to set base icons for the nodes.<br/>
     * Example:<br/>
     * {@code "umich/ms/batmass/filesupport/resources/features.png"} corresponds
     * to the file {@code features.png} which is in package {@code umich.ms.batmass.filesupport.resources}
     * @return absolute path to the icon base in the source files
     * @see AbstractNode#setIconBaseWithExtension(java.lang.String) 
     */
    String getIconPath();

    /**
     * The icon to be used for this particular file-type. It will be used in the
     * file chooser, for example. And if no IconProvider is installed for the
     * node representing this file-type, this icon will be used for the node.
     * @return
     */
    ImageIcon getIcon();

    /**
     * Check if this resolver matches the signature of another one.
     * @param other
     * @return
     */
    boolean matches(FileTypeResolver other);
}
