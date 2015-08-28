/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.testing.custommimeresolver.abc;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.util.lookup.ServiceProvider;

/**
 * Test a custom MIMEResolver.
 * @author dmitriya
 */
@ServiceProvider(service = MIMEResolver.class)
public class ABCMimeResolver extends MIMEResolver {

    private final static String mime = "application/x-abc";
    private final static String ext = ".abc.abc";
    
    public ABCMimeResolver() {
        this(mime);
    }

    public ABCMimeResolver(String... mimeTypes) {
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
