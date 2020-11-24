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

import java.util.logging.Level;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Logger;
import umich.ms.batmass.nbputils.OutputWndPrinter;

/**
 *
 * @author chhh
 */
public class EBus {
    private final EventBus bus;
    private static final String TOPIC = EBus.class.getSimpleName();

    public EBus() {
        
        Logger logger = new Logger() {
            @Override
            public void log(Level level, String msg) {
                if (level.intValue() > Level.INFO.intValue()) {
                    OutputWndPrinter.printErr(TOPIC, msg);
                } else {
                    OutputWndPrinter.printOut(TOPIC, msg);
                }
            }

            @Override
            public void log(Level level, String msg, Throwable th) {
                OutputWndPrinter.printErr(TOPIC, msg + "\nThrowable: " + th.getMessage());
            }
        };
        
        bus = EventBus.builder()
                .logger(logger)
                .build();
    }

    public void register(Object subscriber) {
        bus.register(subscriber);
    }

    public synchronized void unregister(Object subscriber) {
        bus.unregister(subscriber);
    }

    public void post(Object event) {
        bus.post(event);
    }

    public void postSticky(Object event) {
        bus.postSticky(event);
    }

    public <T> T getStickyEvent(Class<T> eventType) {
        return bus.getStickyEvent(eventType);
    }

    public <T> T removeStickyEvent(Class<T> eventType) {
        return bus.removeStickyEvent(eventType);
    }
    
    
}
