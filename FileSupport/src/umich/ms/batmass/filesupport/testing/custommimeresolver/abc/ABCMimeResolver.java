/* 
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
