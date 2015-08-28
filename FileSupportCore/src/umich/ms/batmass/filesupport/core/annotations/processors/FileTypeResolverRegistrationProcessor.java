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
import umich.ms.batmass.filesupport.core.annotations.FileTypeResolverRegistration;
import umich.ms.batmass.filesupport.core.api.FileTypeResolverUtils;
import umich.ms.batmass.filesupport.core.spi.filetypes.FileTypeResolver;
import umich.ms.batmass.nbputils.LayerUtils;

/**
 *
 * @author Dmitry Avtonomov
 */
@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes("umich.ms.batmass.filesupport.core.annotations.FileTypeResolverRegistration")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FileTypeResolverRegistrationProcessor extends AbstractAnnotationProcessorPositioned {

    @Override
    protected boolean handleProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
            throws LayerGenerationException {

        Elements elements = processingEnv.getElementUtils();
        Set<? extends Element> elementsAnnotatedWith = roundEnv
                .getElementsAnnotatedWith(FileTypeResolverRegistration.class);
        for (Element e : elementsAnnotatedWith) {
            TypeElement clazz = (TypeElement) e;
            FileTypeResolverRegistration annotation = clazz
                    .getAnnotation(FileTypeResolverRegistration.class);
            String teName = elements.getBinaryName(clazz).toString();

            // ===========   CATEGORY   ==========
            
            String fileCategory = annotation.fileCategory();
            if (!LayerUtils.validateLayerDirName(fileCategory)) {
                String msg = String.format(
                        "File category (\"%s\") contained illegal characters. Allowed"
                        + "characters are alphanumerics plus underscore and dash."
                        + "Check LayerUtils.validateLayerDirName() method for"
                        + "further info.", fileCategory);
                throw new LayerGenerationException(msg, e);
            }
            if (fileCategory.equals(FileTypeResolverUtils.getFileCategoryAny())) {
                String msg = String.format(
                        "File category (\"%s\") is not allowed to be \"%s\".",
                        fileCategory, FileTypeResolverUtils.getFileCategoryAny());
                throw new LayerGenerationException(msg, e);
            }

            // ===========   TYPE   ==========

            String fileType = annotation.fileType();
            if (!LayerUtils.validateLayerDirName(fileType)) {
                String msg = "File type contained illegal characters. Allowed"
                        + "characters are alphanumerics plus underscore and dash."
                        + "Check LayerUtils.validateLayerDirName() method for"
                        + "further info.";
                throw new LayerGenerationException(msg, e);
            }
            if (fileCategory.equals(FileTypeResolverUtils.getFileTypeAny())) {
                String msg = String.format(
                        "File type (\"%s\") is not allowed to be \"%s\".",
                        fileCategory, FileTypeResolverUtils.getFileTypeAny());
                throw new LayerGenerationException(msg, e);
            }

            // ===========   .instance file   ==========

            String fileName = LayerUtils.getLayerFriendlyInstanceName(teName);
            if (!LayerUtils.validateLayerFileName(fileName)) {
                String msg = String.format(
                        "Instance file name (%s) contained illegal characters. Allowed"
                        + "characters are alphanumerics plus underscore and dash"
                        + "and dot. Check LayerUtils.validateLayerDirName() "
                        + "method for further info.", fileName);
                throw new LayerGenerationException(msg, e);
            }


            // writing to standard path
            String defaultPath = LayerUtils.getLayerPath(FileTypeResolver.LAYER_REGISTRATION_PATH,
                    fileCategory, fileType, fileName);
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