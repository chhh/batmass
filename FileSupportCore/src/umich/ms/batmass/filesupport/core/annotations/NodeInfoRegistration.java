/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.filesupport.core.spi.nodes.FileNodeInfo;
import umich.ms.batmass.projects.core.type.BMProject;

/**
 * Used to register new {@link FileNodeInfo} into the layer.
 * @author Dmitry Avtonomov
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NodeInfoRegistration {
    /**
     * The value is not currently used, install a single node-info for a specific
     * file type in specific category and if you want project-type specific
     * actions and capabilities, just install them into project specific folders
     * for actions and capability providers.
     * <br/>
     * The specific project type for which the node-info will be registered.
     * Default is any project type.
     * @return lowercase string
     *
     * @see NodeCapabilityRegistration
     */
    String projectType() default BMProject.TYPE_ANY;
    /**
     * The specific type of the file. E.g. 'mzxml'. The complete specific type
     * is actually identified by a combination of {@link #fileCategory() } and
     * {@link #fileType() }.
     * @return lowercase string
     */
    String fileType() default FileTypeResolver.TYPE_ANY;
    /**
     * Category of the file. Please check what's currently available in the
     * system already, to figure out proper category names. Category names
     * should be all lowercase!<br/>
     * E.g. 'mzxml' files are mapped to 'lcms' category.
     * @return lowercase string
     */
    String fileCategory() default FileTypeResolver.CATEGORY_ANY;
    /**
     * Optional path extensions, as to where inside the default directory
     * to put instances of this class. Don't use this for now, just use the
     * default.
     * @return
     */
    String[] paths() default {};
    int position() default Integer.MAX_VALUE;
}