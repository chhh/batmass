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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;
import umich.ms.batmass.gui.core.exceptions.ViewerLinkingException;


/**
 * This is the glue, which links two viewers together, when a DnD button
 * is dragged from one viewer and dropped onto a DnD button of another viewer.
 * @author Dmitry Avtonomov
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DnDViewerLinker implements DnDButton.Link {
    private static final Logger LOG = Logger.getLogger(DnDViewerLinker.class.getName());

    @Override
    public void link(Lookup.Provider lkpProviderOurs, Lookup.Provider lkpProviderTheirs) {
                
        MBassador busOurs = lkpProviderOurs.getLookup().lookup(MBassador.class);
        MBassador busTheirs = lkpProviderTheirs.getLookup().lookup(MBassador.class);
        if (busOurs != null && busTheirs != null) {
            // already linked
            if (!busOurs.equals(busTheirs)) {
                String msg = "Linking between two separately linked groups is not allowed.";
                //Exceptions.printStackTrace(new ViewerLinkingException(msg));
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            }
            return;
        }
        MBassador<Object> bus = busOurs != null ? busOurs : busTheirs;
        if (bus == null) {
            // there was no bus set up yet, so we'll create one
            IBusConfiguration busConf = new BusConfiguration()
                    .addFeature(Feature.SyncPubSub.Default())
                    .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                    .addFeature(Feature.AsynchronousMessageDispatch.Default())
                    .setProperty(Properties.Handler.PublicationError, new IPublicationErrorHandler() {
                        @Override
                        public void handleError(final PublicationError error) {
                            LOG.log(Level.SEVERE,
                                    "Mbassador pub/sub error: " + error.getMessage(),
                                    error.getCause());
                        }
                    });
            bus = new MBassador<>(busConf);
        }
        InstanceContent icOurs = lkpProviderOurs.getLookup().lookup(InstanceContent.class);
        if (icOurs == null) {
            Exceptions.printStackTrace(new ViewerLinkingException("Our lookup did not contain"
                    + " an InstanceContent in it"));
            return;
        }
        InstanceContent icTheirs = lkpProviderTheirs.getLookup().lookup(InstanceContent.class);
        if (icTheirs == null) {
            Exceptions.printStackTrace(new ViewerLinkingException("Their lookup did not contain"
                    + " an InstanceContent in it"));
            return;
        }

        // now we have both InstanceContents
        icOurs.add(bus);
        icTheirs.add(bus);

        int a = 1;
    }

}
