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

/**
 *
 * @author Dmitry Avtonomov
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface FileTypeResolverRegistration {
    
    /**
     * Category of the file. Please check what's currently available in the
     * system already, to figure out proper category names. Category names
     * should be all lowercase!<br/>
     * E.g. 'mzxml' files are mapped to 'lcms' category.
     * @return lowercase string
     */
    String fileCategory();
    /**
     * The specific type of the file. E.g. 'mzxml'. The complete specific type
     * is actually identified by a combination of {@link #fileCategory() } and
     * {@link #fileType() }.
     * @return lowercase string
     */
    String fileType();
    /**
     * Optional path extensions, as to where inside the default directory
     * to put instances of this class. Don't use this for now, just use the
     * default.
     * @return
     */
    String[] paths() default {};
    /**
     * This parameter doesn't mean much for file type resolvers. All of them will
     * be queried anyway.
     * @return
     */
    int position() default Integer.MAX_VALUE;
}