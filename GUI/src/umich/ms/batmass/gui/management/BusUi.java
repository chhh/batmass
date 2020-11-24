/*
 * Copyright 2020 chhh.
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
package umich.ms.batmass.gui.management;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ProgressMonitor;
import net.engio.mbassy.bus.IMessagePublication;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.bus.publication.SyncAsyncPostCommand;
import net.engio.mbassy.listener.Handler;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import umich.ms.batmass.gui.core.api.comm.eventbus.AbstractBusPubSub;
import umich.ms.batmass.gui.messages.MsgProgressUi;
import umich.ms.batmass.nbputils.OutputWndPrinter;

/**
 *
 * @author chhh
 */
public class BusUi {
    private static final Logger LOG = Logger.getLogger(BusUi.class.getName());
    public static final MBassador<Object> BUS;
    private static final BusLocalHandler handler;
    
    private static final String TOPIC = BusUi.class.getSimpleName();
    
    static {
        
        IBusConfiguration busConf = new BusConfiguration()
                .addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                .addFeature(Feature.AsynchronousMessageDispatch.Default())
                .setProperty(Properties.Handler.PublicationError, new IPublicationErrorHandler() {
                    @Override
                    public void handleError(final PublicationError error) {
                        LOG.log(Level.SEVERE, "Mbassador pub/sub error: " + error.getMessage(), error.getCause());
                        OutputWndPrinter.printErr(BusUi.class.getSimpleName(), error.getMessage());
                    }
                });
        // TODO: sub ourselves to the bus
        BUS = new MBassador<>(busConf);
        handler = new BusLocalHandler();
        BUS.subscribe(handler);
    }

    public static IMessagePublication publishAsync(Object message) {
        return BUS.publishAsync(message);
    }

    public static IMessagePublication publishAsync(Object message, long timeout, TimeUnit unit) {
        return BUS.publishAsync(message, timeout, unit);
    }

    public static void publish(Object message) {
        BUS.publish(message);
    }

    public static SyncAsyncPostCommand<Object> post(Object message) {
        return BUS.post(message);
    }

    public static boolean unsubscribe(Object listener) {
        return BUS.unsubscribe(listener);
    }

    public static void subscribe(Object listener) {
        BUS.subscribe(listener);
    }
    
    
    public static class BusLocalHandler extends AbstractBusPubSub {
        private ConcurrentHashMap<String, AggregateProgressHandle> mapHandles = new ConcurrentHashMap<>();
        private ConcurrentHashMap<String, ProgressMonitor> mapMonitors = new ConcurrentHashMap<>();
        private ConcurrentHashMap<String, ProgressContributor> mapContribs = new ConcurrentHashMap<>();
        private final Object lock = new Object();

        @Handler
        public void onMsgProgressUi(MsgProgressUi m) {
            synchronized(lock) {
                OutputWndPrinter.printOut(TOPIC, "Got MsgProgressUi: " + m.toString());                
                final String pcId = m.category + m.task;
                ProgressContributor pc = mapContribs.get(pcId);
                if (pc != null) { // contributor was there, just update
                    pc.progress(String.format("Time1@%d", System.nanoTime()), m.progress);
                
                } else { // no contributor yet
                    pc = AggregateProgressFactory.createProgressContributor(m.task);
                    mapContribs.put(pcId, pc);
                    
                    AggregateProgressHandle aph = mapHandles.get(m.category);
                    
                    if (aph == null) { // category DOESN'T exist
                        ProgressContributor[] contribs = new ProgressContributor[] {pc};
                        aph = AggregateProgressFactory.createHandle(m.category, contribs, null, null);
                        mapHandles.put(m.category, aph);
                        aph.setInitialDelay(0);
                        aph.start();
                        
                        pc.progress(String.format("Time2@%d", System.nanoTime()), m.progress);
                        
                    } else { // category exists
                        aph.addContributor(pc);
                        pc.progress(String.format("Time3@%d", System.nanoTime()), m.progress);
                    }
                }
                if (pc != null && m.progress == 100) {
                    pc.finish();
                }
            }
        }
    }
    
//    public static class AggregateProgressMon implements org.netbeans.api.progress.aggregate.ProgressMonitor {
//        public final String id;
//
//        public AggregateProgressMon(String id) {
//            this.id = id;
//        }
//
//        @Override
//        public void started(ProgressContributor pc) {
//            OutputWndPrinter.printOut(TOPIC, "Progress Monitor started: id="+id;);
//        }
//
//        @Override
//        public void finished(ProgressContributor pc) {
//            OutputWndPrinter.printOut(TOPIC, "Progress Monitor finished: id="+id );
//        }
//
//        @Override
//        public void progressed(ProgressContributor pc) {
//            OutputWndPrinter.printOut(TOPIC, "Progress Monitor progressed: id="+id;);
//        }
//    }
}
