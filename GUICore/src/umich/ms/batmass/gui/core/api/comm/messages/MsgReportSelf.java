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
package umich.ms.batmass.gui.core.api.comm.messages;

import net.engio.mbassy.bus.MBassador;

/**
 * This message serves to ask other subscribers on the bust to report themselves.
 * @author Dmitry Avtonomov
 */
@SuppressWarnings("rawtypes")
public class MsgReportSelf {
    protected final boolean isRequest;
    protected final MBassador bus;

    /**
     * Constructor for a request message.
     * @param bus can't be null
     */
    public MsgReportSelf(MBassador bus) {
        if (bus == null) {
            throw new IllegalArgumentException("This message is a request, "
                    + "the bus to reply to must be provided.");
        }
        this.isRequest = true;
        this.bus = bus;
    }

    /**
     * Constructor for a response message.
     */
    public MsgReportSelf() {
        this.isRequest = false;
        this.bus = null;
    }

    /**
     * A marker whether this message is a request or it's already a report.
     * @return if true, then this is a request to report yourself on this bus,
     * if false, this is just a response to such a request, no action should be
     * taken.
     */
    public boolean isRequest() {
        return isRequest;
    }

    public MBassador getBus() {
        return bus;
    }
}
