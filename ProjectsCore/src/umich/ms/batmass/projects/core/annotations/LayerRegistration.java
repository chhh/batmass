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
 * have a no-arguments constructor. Same as {@link LayerInstance.Registration}.
 * 
 * @see LayerInstance.Registration
 * 
 * @author Dmitry Avtonomov
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface LayerRegistration {
    String[] paths();
    int position() default Integer.MAX_VALUE;
}
