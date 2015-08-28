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
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

/**
 * A unique textual identifier for the project type. Is used to register stuff
 * in the layer file specifically for this project type.
 * @author dmitriya
 */
@Retention(RetentionPolicy.RUNTIME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@Target(ElementType.TYPE)
@Documented
public @interface BMProjectType {
    String projectType() default "BMProject";
}
