/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.projects.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for registering stuff in the layer.xml file. Annotated class must
 * have a no-arguments constructor.
 * @author dmitriya
 * @deprecated use simple {@link LayerRegistration} annotation instead.
 */
@Deprecated
public interface LayerInstance {
    /**
     * Registers a .instance file in the layer.xml. This means you'll need to 
     * have a no-arguments constructor on the target class.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    @interface Registration {
        String[] paths();
        int position() default Integer.MAX_VALUE;
    }
}