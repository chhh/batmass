/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.filesupport.core.types.descriptor;

import java.awt.Image;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Dmitry Avtonomov
 */
public class BadFileDescriptorNode extends DataNode {
    private final InstanceContent ic;
    private String text;
    private TYPE type;

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
    
    @StaticResource
    private static final String ICON_BASE_PATH = "umich/ms/batmass/filesupport/core/resources/warning_round_16px.png";
    private static final ImageIcon ICON = ImageUtilities.loadImageIcon(ICON_BASE_PATH, false);
    private static final String ERROR_FONT_COLOR = "B00000";

    




    public BadFileDescriptorNode(FileDescriptorDataObject dobj, TYPE type, String text) {
        this(dobj, type, text, new InstanceContent());
    }

    /**
     * Using this constructor you can share InstanceContent of the lookup, used
     * by this Node, with InstanceContent of DataObject lookup.
     * @param dobj
     * @param ic this node's lookup will be a merge of the original
     * {@link DataNode} lookup and this instance content.
     */
    private BadFileDescriptorNode(FileDescriptorDataObject dobj, TYPE type, String text, InstanceContent ic) {
        super(dobj, Children.LEAF, new ProxyLookup(dobj.getLookup(), new AbstractLookup(ic)));
        this.ic = ic;
        this.type = type;
        this.text = text;
        FileDescriptor desc = dobj.getDescriptor();
        if (desc != null) {
            ic.add(desc);
        }
        this.setChildren(Children.LEAF);
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
