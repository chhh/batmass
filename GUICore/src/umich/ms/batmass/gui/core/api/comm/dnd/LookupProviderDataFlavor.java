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
package umich.ms.batmass.gui.core.api.comm.dnd;

import java.awt.datatransfer.DataFlavor;
import org.openide.util.Lookup;

/**
 * Used in drag and drop to transfer instances of Lookup providers.
 * @author Dmitry Avtonomov
 */
public class LookupProviderDataFlavor extends DataFlavor {
    public static final String MIME = "java/LookupProvider";

    public LookupProviderDataFlavor() {
        super(Lookup.Provider.class, MIME);
    }
}
