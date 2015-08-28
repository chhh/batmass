/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.annotations.processors;

import javax.lang.model.element.Element;
import org.openide.filesystems.annotations.LayerBuilder;
import org.openide.filesystems.annotations.LayerGeneratingProcessor;
import org.openide.filesystems.annotations.LayerGenerationException;

/**
 * Just a handy class, that will print to console, whenever it writes files
 * to the layer. Written files must have a position attribute.
 * @author Dmitry Avtonomov
 */
public abstract class AbstractAnnotationProcessorPositioned extends LayerGeneratingProcessor {

    protected void writeLayerFile(String path, Element e, int position)
            throws LayerGenerationException {

        System.out.printf("LayerGeneratingProcessor[%s] writes to:\n"
                + "\t%s\n", this.getClass().getCanonicalName(), path);

        LayerBuilder.File f = layer(e)
                .file(path)
                .position(position);
        f.write();
    }
}
