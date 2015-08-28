/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.annotations.processors;

import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.openide.filesystems.annotations.LayerGenerationException;
import org.openide.util.lookup.ServiceProvider;
import umich.ms.batmass.filesupport.core.annotations.NodeCapabilityRegistration;
import umich.ms.batmass.filesupport.core.spi.nodes.CapabilityProvider;
import umich.ms.batmass.nbputils.LayerUtils;


@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes("umich.ms.batmass.filesupport.core.annotations.NodeCapabilityRegistration")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class NodeCapabilityRegistrationProcessor extends AbstractAnnotationProcessorPositioned {

    @Override
    protected boolean handleProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
            throws LayerGenerationException {

        Elements elements = processingEnv.getElementUtils();
        Set<? extends Element> elementsAnnotatedWith = roundEnv
                .getElementsAnnotatedWith(NodeCapabilityRegistration.class);
        for (Element e : elementsAnnotatedWith) {
            TypeElement clazz = (TypeElement) e;
            NodeCapabilityRegistration annotation = clazz
                    .getAnnotation(NodeCapabilityRegistration.class);
            String teName = elements.getBinaryName(clazz).toString();


            String projectType = annotation.projectType();
            if (!LayerUtils.validateLayerDirName(projectType)) {
                String msg = String.format(
                        "Project type (\"%s\") contained illegal characters. Allowed"
                        + " characters are alphanumerics plus underscore and dash."
                        + " Check LayerUtils.validateLayerDirName() method for"
                        + " further info.", projectType);
                throw new LayerGenerationException(msg, e);
            }

            String fileCategory = annotation.fileCategory();
            if (!LayerUtils.validateLayerDirName(fileCategory)) {
                String msg = String.format(
                        "File category (\"%s\") contained illegal characters. Allowed"
                        + " characters are alphanumerics plus underscore and dash."
                        + " Check LayerUtils.validateLayerDirName() method for"
                        + " further info.", fileCategory);
                throw new LayerGenerationException(msg, e);
            }
            String fileType = annotation.fileType();
            if (!LayerUtils.validateLayerDirName(fileType)) {
                String msg = String.format(
                        "File type (\"%s\") contained illegal characters. Allowed"
                        + " characters are alphanumerics plus underscore and dash."
                        + " Check LayerUtils.validateLayerDirName() method for"
                        + " further info.", fileType);
                throw new LayerGenerationException(msg, e);
            }
            String fileName = LayerUtils.getLayerFriendlyInstanceName(teName);
            if (!LayerUtils.validateLayerFileName(fileName)) {
                String msg = String.format(
                        "Instance file (\"%s\") name contained illegal characters. Allowed"
                        + " characters are alphanumerics plus underscore and dash"
                        + " and dot. Check LayerUtils.validateLayerDirName() "
                        + " method for further info.", fileName);
                throw new LayerGenerationException(msg, e);
            }


            // writing to standard path
            String defaultPath = LayerUtils.getLayerPath(
                    CapabilityProvider.CAPABILITIES_LAYER_PATH_BASE,
                    projectType, fileType, fileCategory, fileName);
            writeLayerFile(defaultPath, e, annotation.position());


            // writing to non-standard paths provided in the annotation.
            String[] paths = annotation.paths(); // aditional registration paths
            for (String path : paths) {
                path = LayerUtils.getLayerPath(path, fileName);
                writeLayerFile(path, e, annotation.position());
            }
        }
        return true;
    }

}