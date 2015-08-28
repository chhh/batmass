/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.api.comm.eventbus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.InstanceContent;
import umich.ms.batmass.gui.core.api.comm.dnd.DnDButton;
import umich.ms.batmass.gui.core.api.comm.dnd.DnDViewerLinker;
import umich.ms.batmass.gui.core.api.comm.dnd.UnlinkButton;
import umich.ms.batmass.gui.core.api.comm.messages.MsgHighlight;
import umich.ms.batmass.gui.core.api.comm.messages.MsgNewMemberSubscribed;
import umich.ms.batmass.gui.core.api.comm.messages.MsgReportSelf;
import umich.ms.batmass.gui.core.api.comm.messages.MsgUnsubscribe;



/**
 *
 * @author Dmitry Avtonomov
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ViewerLinkSupport {
    @StaticResource
    private static final String ICON_PATH_LINK = "umich/ms/batmass/gui/core/resources/icon_link.png";
    public static final ImageIcon ICON_LINK = ImageUtilities.loadImageIcon(ICON_PATH_LINK, false);
    @StaticResource
    private static final String ICON_PATH_UNLINK = "umich/ms/batmass/gui/core/resources/icon_unlink.png";
    public static final ImageIcon ICON_UNLINK = ImageUtilities.loadImageIcon(ICON_PATH_UNLINK, false);
    
    /** The component, that will be used for highlighting */
    protected Collection<? extends JComponent> highlighters;
    /** When this viewer is linked to others with a bus objects from this list
     will be subscribed/unsubscribed to that bus. */
    protected List<Object> subscribers;
    protected Collection<? extends IBusPubSub> publishers;
    /** This lookup must contain an InstanceContent (IC), that IC will be used
     to store buses, we're subscribed to. Normally this will be the same thing
     as {@code component} parameter, it should just implement Lookup.Provider.*/
    protected Lookup.Provider lkpProvider;
    protected Lookup.Result<MBassador> lookupResultBus;
    
    // utility vars
    protected int responseCount = 0;
    protected Color previousHighlightColor = null;
    
    // these are the buttons that you can use in your compoenent
    protected DnDButton btnLinkDnD;
    protected JButton btnUnlink;


    /**
     *
     * @param highlighters can be null, JComponents, that will be used for highlighting
     * @param subscribers can be null, these objects will be subscribed to all linked buses
     * @param publishers can be null, these {@link IBusPubSub}s will get linked buses
     * added/removed, according to the interface, so that they could publish something there.
     * @param lkpProvider not null, this is the lookup provider, which stores the
     * linked buses and other stuff. This lookup MUST contain the {@link InstanceContent}
     * that backs up the lookup, as this class will add/remove linked buses to/from it.
     */
    public ViewerLinkSupport(
            Collection<? extends JComponent> highlighters,
            Collection<?> subscribers,
            Collection<? extends IBusPubSub> publishers,
            Lookup.Provider lkpProvider) {
        InstanceContent ic = lkpProvider.getLookup().lookup(InstanceContent.class);
        if (ic == null) {
            throw new IllegalArgumentException("The lookup of the Lookup.Provider "
                    + "(lkpProvider argument) must contain an InstanceContent that "
                    + "backs up that lookup.");
        }
        this.highlighters = highlighters == null ? Collections.EMPTY_LIST : highlighters;
        subscribers = subscribers == null ? Collections.EMPTY_LIST : subscribers;
        this.subscribers = new ArrayList<>(subscribers.size() + 1);
        for (Object o : subscribers) {
            this.subscribers.add(o);
        }
        this.subscribers.add(this); // add self to the list of subscribers
        this.publishers = publishers;
        this.lkpProvider = lkpProvider;


        // this will monitor new subscriptions (buses) added to the lookup
        lookupResultBus = lkpProvider.getLookup().lookupResult(MBassador.class);

        // Link button
        btnLinkDnD = new DnDButton(lkpProvider);
        ImageIcon iconLink = ICON_LINK;
        btnLinkDnD.setIcon(iconLink);
        btnLinkDnD.setToolTipText("Link this view to another one - drag and drop");
        btnLinkDnD.setLinkFunction(new DnDViewerLinker());
        btnLinkDnD.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    // this means that the mouse was pressed AND RELEASED over the button
                    // so this is NOT TRIGGERED, if you: press -> drag outside the button -> release
                    AbstractButton abstractButton = (AbstractButton) e.getSource();
                    ButtonModel buttonModel = abstractButton.getModel();
                    boolean doHighlight = buttonModel.isSelected();
                    publishToBuses(new MsgHighlight(doHighlight));
                }
            }
        });
        
        
        // Unlink button
        ImageIcon iconUnlink = ICON_UNLINK;
        btnUnlink = new UnlinkButton(iconUnlink);
        btnUnlink.setToolTipText("Unlink this view from all others");
        if (lookupResultBus.allInstances().size() > 0) {
            btnUnlink.setEnabled(true);
        } else {
            btnUnlink.setEnabled(false);
        }
        lookupResultBus.addLookupListener(new LookupListener() {
            @Override
            public void resultChanged(LookupEvent ev) {
                Collection<? extends MBassador> buses = lookupResultBus.allInstances();
                if (buses.size() > 0) {
                    btnUnlink.setEnabled(true);
                    for (MBassador bus : buses) {
                        eventbusSubscribe(bus);
                    }
                } else {
                    btnUnlink.setEnabled(false);
                    btnLinkDnD.setSelected(false);
                }
            }
        });
        btnUnlink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collection<? extends MBassador> buses = 
                        ViewerLinkSupport.this.lkpProvider.getLookup()
                        .lookupAll(MBassador.class);
                if (buses == null || buses.isEmpty()) {
                    btnUnlink.setEnabled(false); // just in case
                    return;
                }

                for (MBassador bus : buses) {
                    int numSubs = eventbusCountSubscribers(bus);
                    if (numSubs > 2) {
                        eventbusUnsubscribe(bus);
                    } else {
                        // if there are only 2 or less subscribers left, just tell
                        // everyone to unsubscribe
                        bus.publish(new MsgUnsubscribe(bus));
                    }
                }
            }
        });
    }

    /**
     * Gets the button, that can be drag-n-dropped to other similar buttons in 
 other highlighters to make links between the highlighters. The button can also
     * be pressed, as it's a toggle button, in which case all connected
     * {@link JComponent}s will get a highlight border (which might result in
 low performance, as all the linked highlighters will have to be revalidated
 to recalculate their internal sizes, as the border will eat space).
     * @return 
     */
    public DnDButton getBtnLinkDnD() {
        return btnLinkDnD;
    }

    /**
     * If the associated JComponent has been connected to others, clicking this
     * button will break all such links.
     * @return 
     */
    public JButton getBtnUnlink() {
        return btnUnlink;
    }
    
    
    
    private void publishToBuses(Object msg) {
        Collection<? extends MBassador> buses = lkpProvider.getLookup().lookupAll(MBassador.class);
        if (buses != null && !buses.isEmpty()) {
            for (MBassador bus : buses) {
                bus.publish(msg);
            }
        }
    }
    
    /**
     * Listens on {@link MBassador} message bus (subscription is done
     * in the toolbar creation).
     * @param m
     */
    @Handler
    public void eventbusHandleHighlightMsg(MsgHighlight m) {
        Border border;
        Color c = null;
        if (m.isDoHightlight()) {
            c = m.getColor();
        }

        // two cases when wa already have the correct border
        if (c == null) {
            if (previousHighlightColor == c)
                return;
        } else {
            if (c.equals(previousHighlightColor))
                return;
        }

        // create the border
        if (c != null) {
            border = BorderFactory.createMatteBorder(3, 3, 3, 3, c);
            previousHighlightColor = c;
        } else {
            border = BorderFactory.createEmptyBorder();
            previousHighlightColor = null;
        }

        for (JComponent component : highlighters) {
            component.setBorder(border);
            component.revalidate();
            component.repaint();
        }
    }

    @Handler
    public void eventbusHandlerReportSelfMsg(MsgReportSelf m) {
        if (m.isRequest()) {
            m.getBus().publish(new MsgReportSelf());
        } else {
            responseCount++;
        }
    }

    @Handler
    public void eventbusHandlerUnsubscribeMsg(MsgUnsubscribe m) {
        eventbusUnsubscribe(m.getBus());
    }

    @Handler
    public void eventbusHandlerNewMemberSubscribedMsg(MsgNewMemberSubscribed m) {
        if (btnLinkDnD.getModel().isSelected()) {
            m.getBus().publish(new MsgHighlight(true, previousHighlightColor));
        }
    }

    public void eventbusSubscribe(MBassador bus) {
        for (Object o : subscribers) {
            bus.subscribe(o);
        }
        for (IBusPubSub pub : publishers) {
            pub.addBus(bus);
        }
        bus.publish(new MsgNewMemberSubscribed(bus));
    }

    public void eventbusUnsubscribe(MBassador bus) {
        for (Object o : subscribers) {
            bus.unsubscribe(o);
        }
        for (IBusPubSub pub : publishers) {
            pub.removeBus(bus);
        }
        InstanceContent ic = lkpProvider.getLookup().lookup(InstanceContent.class);
        ic.remove(bus);
        eventbusHandleHighlightMsg(new MsgHighlight(false));
    }

    public int eventbusCountSubscribers(MBassador bus) {
        responseCount = 0;
        bus.publish(new MsgReportSelf(bus));
        int count = responseCount;
        responseCount = 0;
        return count;
    }
}
