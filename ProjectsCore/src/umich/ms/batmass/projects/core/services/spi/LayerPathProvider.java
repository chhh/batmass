/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.projects.core.services.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A generic layer path provider annotation, annotate a class with a no-argument
 * constructor with it, and an instance of that object will be installed into
 * the system-filesystem.
 * @author Dmitry Avtonomov
 */
public interface LayerPathProvider {
    /**
     * These are the paths, that users of this Provider will receive.
     * @return 
     */
    public String[] getPaths();

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    @interface Registration {
        /**
         * The path in the layer where your instances will be created.
         * @return
         */
        String[] paths();
        int position() default Integer.MAX_VALUE;
    }
}
