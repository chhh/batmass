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
package umich.ms.batmass.nbputils.actions;

import java.util.HashMap;
import java.util.StringTokenizer;
import org.openide.util.Utilities;

/**
 *
 * @author Dmitry Avtonomov
 */
public class ActionUtils {
    private static final HashMap<Character, String> keyCodeNameMap = new HashMap<>();
    static {
        keyCodeNameMap.put('A', "Alt");
        keyCodeNameMap.put('a', "Alt");
        keyCodeNameMap.put('C', "Ctrl");
        keyCodeNameMap.put('c', "Ctrl");
        keyCodeNameMap.put('M', "Meta");
        keyCodeNameMap.put('m', "Meta");
        keyCodeNameMap.put('S', "Shift");
        keyCodeNameMap.put('s', "Shift");
    }
    
    
    private ActionUtils() {}
    
    /**
     * Converts strings that <code>Utilities.stringToKey(String)</code> take as input
     * to human readable strings.
     * @param keyStrokeCode a string like "AS-K", which means "Alt+Shift+K", note that
     *  modifier 'D' is not allowed, at runtime it is converted to either 'C' or 'M', 
     *  depending on the system.
     * @return 
     */
    public static String fromNbKeyStrokeToHuman(String keyStrokeCode) {
        StringBuilder sb = new StringBuilder();
        int dashIdx = keyStrokeCode.indexOf('-');
        if (dashIdx > 0) {
            Character D = 'D';
            for (int i = 0; i < dashIdx; i++) {
                char nextToken = keyStrokeCode.charAt(i);
                String mapping = keyCodeNameMap.get(nextToken);
                if (mapping == null) {
                    if (D.equals(nextToken)) {
                        mapping = Utilities.isMac() ? "Cmd" : "Ctrl";
                    } else {
                        continue;
                    }
                }
                sb.append(mapping);
                sb.append("+");
            }
            sb.append(keyStrokeCode.substring(dashIdx+1, keyStrokeCode.length()));
            return sb.toString();
        }
        return keyStrokeCode;
    }
}
