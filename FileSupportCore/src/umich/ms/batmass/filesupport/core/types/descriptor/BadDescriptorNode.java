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
package umich.ms.batmass.filesupport.core.types.descriptor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Image;
import java.util.Collection;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.filesystems.MIMEResolver;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author Dmitry Avtonomov
 */
public class BadDescriptorNode extends AbstractNode {
    private final TYPE type;
    private final String text;
    @StaticResource
    private static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/core/resources/warning_round_16px.png";
    private static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);
    private static final String ERROR_FONT_COLOR = "B00000";

    /** Type of error, that happened, when creating descriptor node */
    public static enum TYPE {
        GENERIC_BAD("Unknown error"),
        NO_PARSER("No parser"),
        NO_RESOLVER("No file type resolvers"),
        ORIGINAL_FILE_MISSING("Original file missing"),
        CORRUPT_DESCRIPTOR("Corrupt descriptor"),
        NO_DATALOADER("No DataLoader claimed this filetype");
        public String msg;
        private TYPE(String msg) {
            this.msg = msg;
        }
        public String getMessage() {
            return msg;
        }
    };

    
    public BadDescriptorNode(TYPE type, String text) {
        super(Children.LEAF);
        this.type = type;
        this.text = text;
        setIconBaseWithExtension(ICON_BASE_PATH);
        setShortDescription(type.getMessage());

        // TODO: what is that doing here?
        Collection<? extends MIMEResolver> lookupAll = Lookup.getDefault().lookupAll(MIMEResolver.class);
        for (MIMEResolver mimeRes : lookupAll) {
            int a = 1;
        }
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuilder sb = new StringBuilder(String.format("<font color='%s'>", ERROR_FONT_COLOR));
        sb.append(text);
        String msg = type.getMessage();
        if (msg != null && !msg.isEmpty()) {
            sb.append(" (");
            sb.append(msg);
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public Image getIcon(int type) {
        return ICON.getImage();
    }
}
