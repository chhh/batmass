/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.testing.custommimeresolver.pepxml;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author dmitriya
 */
@ServiceProvider(service = MIMEResolver.class)
public class PepXMLMimeResolver extends MIMEResolver {

    private final static String mime = "application/x-pep-xml";
    private final static String ext = ".pep.xml";
    
    public PepXMLMimeResolver() {
        this(mime);
    }

    public PepXMLMimeResolver(String... mimeTypes) {
        super(mimeTypes);
    }
    
    @Override
    public String findMIMEType(FileObject fo) {
        if (fo.getNameExt().toLowerCase().endsWith(ext)) {
            return mime;
        }
        return null;
    }
}
