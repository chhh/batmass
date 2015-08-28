/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
