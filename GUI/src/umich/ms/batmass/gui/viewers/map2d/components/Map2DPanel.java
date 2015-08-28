/*
 * License placeholder
 */
package umich.ms.batmass.gui.viewers.map2d.components;

import MSUmpire.PeakDataStructure.PeakCluster;
import com.github.davidmoten.rtree.Entry;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import org.openide.util.Exceptions;
import umich.ms.batmass.data.core.lcms.features.Features;
import umich.ms.batmass.data.core.lcms.features.ILCMSFeature2D;
import umich.ms.batmass.gui.core.api.comm.eventbus.AbstractBusPubSub;
import umich.ms.batmass.gui.core.api.data.MzRtPoint;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.api.util.ScreenUtils;
import umich.ms.batmass.gui.core.api.util.color.ColorMap;
import umich.ms.batmass.gui.viewers.featuretable.messages.MsgFeatureClick;
import umich.ms.batmass.gui.viewers.map2d.events.ZoomEvent;
import umich.ms.batmass.gui.viewers.map2d.messages.MsgZoom1D;
import umich.ms.batmass.gui.viewers.map2d.messages.MsgZoom2D;
import umich.ms.batmass.gui.viewers.map2d.todelete.ProcessingUmpireFeatures;
import umich.ms.batmass.nbputils.OutputWndPrinter;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.exceptions.FileParsingException;
import umich.ms.util.DoubleRange;
import umich.ms.util.IntervalST;


/**
 *
 * @author Dmitry Avtonomov
 */
public class Map2DPanel extends JPanel {

    public final static DoubleRange OPT_DISPLAY_ALL_MZ_REGIONS =
            new DoubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    protected IScanCollection scans;
    protected ProcessingUmpireFeatures puf;
    private Features<ILCMSFeature2D<?>> features;
    /**
     * If this this map is larger than the available screen space, it will be
     * drawn fully and then down-sampled to fit the space. If it is smaller,
     * then it will first be drawn at it's full height (RT) and then from that
     * not-full-height picture build an interpolated full-height one using
     * bilinear or bicubic interpolations.
     */
    protected LinkedList<Map2DZoomLevel> zoomLevels;
    protected Map2DZoomLevel curZoomLevel = null; // 0 = fully zoomed-out
    protected MzRtRegion defaultViewport = null;
    /** When {@link #setDisplayedMzRegion(umich.ms.fileio.util.Interval1D) } 
     * is set to this value, all precursors will be shown at once. Or in case of
     * MS1 display, all measured m/z ranges will be shown in a single map.
     */
    
    protected Map2dMouseListener mouseHandler;
    protected EventListenerList map2DListeners;
    protected volatile boolean isZoomInProgress = false;
//    private static final Object zoomedInUpscalingAlgo = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
    protected Object zoomedInUpscalingAlgo = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
    protected IMap2DInfoDisplayer infoDisplayer;
    protected JToolTip tooltip = null;

    // options for the currently built map
    protected Map2DPanelOptions displayedOptions;
    // handling events/messages
    protected BusHandler busHandler;

    // these fields might go to the Options panel
    private static final double zoomCoef = 1.4d;
    protected static final Color gray50 = new Color(128, 128, 128, 128);
    protected static int MIN_ALLOWED_COMPONENT_PIXEL_SIZE = 10;
    

    public Map2DPanel() {
        constructorInit();
    }

    /**
     * Sets up datastructures and listeners, doesn't do anything about map
     * creation. However, it does set up listeners for component
     * Showing/Moving/Resizing/Hiding, Showing and Resizing do trigger
     * map creation/revalidation.
     */
    private void constructorInit() {
        Map2DPanelOptions opts = new Map2DPanelOptions();
        displayedOptions = opts;

        zoomLevels = new LinkedList<>();
        infoDisplayer = new Map2DInfoDisplayerDefault();

        // mouse
        mouseHandler = new Map2dMouseListener();
        map2DListeners = new EventListenerList();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
        map2DListeners.add(Map2DZoomEventListener.class, mouseHandler);
        
        // listener list has already been initialized, safe to add to it
        busHandler = new BusHandler();
        addZoomEventListener(busHandler);

        this.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                initMap();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
                initMap();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    public BusHandler getBusHandler() {
        return busHandler;
    }

    public Map2DPanelOptions getOptions() {
        return displayedOptions;
    }

    /**
     * Will create a copy of the options passed in.
     * @param newOptions
     */
    public void setOptions(Map2DPanelOptions newOptions) {
        displayedOptions = newOptions.copy();
    }

    /**
     * Set viewport to which this panel will be reset when a complete zoom-out is
     * requested.
     * @param viewport can be null, then the whole MS1 region will be set
     */
    public final void setDefaultViewport(MzRtRegion viewport) {
        if (viewport == null && scans != null) {
            // this should happen only when the TopComponent is opened for the first time
            TreeMap<Integer, IScan> scanMapAtLevel = scans.getMapMsLevel2index().get(1).getNum2scan();
            double rtStart = scanMapAtLevel.firstEntry().getValue().getRt();
            double rtEnd = scanMapAtLevel.lastEntry().getValue().getRt();
            double mzStart = Double.POSITIVE_INFINITY;
            double mzEnd = Double.NEGATIVE_INFINITY;
            IScan scan;
            for (Map.Entry<Integer, IScan> num2scan : scanMapAtLevel.entrySet()) {
                scan = num2scan.getValue();

                Double scanMzWindowLower = scan.getScanMzWindowLower();
                Double scanMzWindowUpper = scan.getScanMzWindowUpper();
                if (scanMzWindowLower == null || scanMzWindowUpper == null) {
                    // if we had such a bad scan, we'll then have to read the spectrum
                    boolean autoloadSpectraOrigVal = scans.isAutoloadSpectra();
                    scans.isAutoloadSpectra(true);
                    try {
                        ISpectrum spec = scan.fetchSpectrum();
                        if (spec != null) {
                            scanMzWindowLower = spec.getMinMZ();
                            scanMzWindowUpper = spec.getMaxMZ();
                        }
                    } catch (FileParsingException ex) {
                        Exceptions.printStackTrace(ex); // TODO: ahhhh, so bad
                    } finally {
                        scans.isAutoloadSpectra(autoloadSpectraOrigVal);
                    }
                }
                if (scanMzWindowLower != null && scanMzWindowLower < mzStart) {
                    mzStart = scanMzWindowLower;
                }
                if (scanMzWindowUpper != null && scanMzWindowUpper > mzEnd) {
                    mzEnd = scanMzWindowUpper;
                }

            }
            defaultViewport = new MzRtRegion(mzStart, mzEnd, rtStart, rtEnd);
        } else {
            defaultViewport = viewport;
        }
    }

    public void addZoomEventListener(Map2DZoomEventListener l) {
        map2DListeners.add(Map2DZoomEventListener.class, l);
    }

    public void removeZoomEventListener(Map2DZoomEventListener l) {
        map2DListeners.remove(Map2DZoomEventListener.class, l);
    }

    protected void fireZoomEvent(ZoomEvent e) {

        // TODO: this is a bad thing, this is set here in fireEvent()
        isZoomInProgress = e.isStart();

        // loop through each listener and pass on the event if needed
        Map2DZoomEventListener[] listeners = map2DListeners.getListeners(Map2DZoomEventListener.class);
        for (Map2DZoomEventListener listener : listeners) {
            listener.handleZoomEvent(e);
        }
    }

    public void setInfoDisplayer(IMap2DInfoDisplayer infoDisplayer) {
        this.infoDisplayer = infoDisplayer;
    }

    public IScanCollection getScans() {
        return scans;
    }

    public void setScans(IScanCollection scans) {
        this.scans = scans;
        initOptions();
    }

    public Map2DZoomLevel getCurrentZoomLevel() {
        return curZoomLevel;
    }

    public LinkedList<Map2DZoomLevel> getZoomLevels() {
        return zoomLevels;
    }

    public ProcessingUmpireFeatures getPuf() {
        return puf;
    }

    public void setPuf(ProcessingUmpireFeatures puf) {
        this.puf = puf;
    }
    
    public void setFeatures(Features<ILCMSFeature2D<?>> features) {
        this.features = features;
    }
    
    public Features<ILCMSFeature2D<?>> getFeatures() {
        return features;
    }


    /**
     * The main method that should be called to initialize building the map
     * and displaying the image.<br/>
     * Before calling this, you must add this Map2DPanel to a rendered container
     * and set the scans using {@link #setScans(umich.ms.datatypes.scancollection.IScanCollection) }.
     */
    public void initMap() {
        Container parent = this.getParent();
        if (parent == null || this.scans == null) {
            // the Map2D panel has not yet been added to any container, so we can't get the
            // available screen space, so we can't start building the map
            return;
        }

        Rectangle screenBounds = ScreenUtils.getScreenBounds(this);
        if (screenBounds.width < MIN_ALLOWED_COMPONENT_PIXEL_SIZE
            || screenBounds.height < MIN_ALLOWED_COMPONENT_PIXEL_SIZE) {
            OutputWndPrinter.printOut("Map2D",
                    String.format("Won't do initMap(), screen bounds too small:"
                    + " width=%d, height=%d, min allowed dimension=%d",
                    screenBounds.width, screenBounds.height, MIN_ALLOWED_COMPONENT_PIXEL_SIZE));
            return;
        }
        Map2DZoomLevel curZoomLvl = getCurrentZoomLevel();
        // if there was nothing shown yet, we build the first (default) zoom level
        if (curZoomLvl == null) {
            MzRtRegion mapDims = defaultViewport;
            curZoomLvl = new Map2DZoomLevel(0, scans, mapDims, screenBounds,
                    getOptions().getMsLevel(), getOptions().getMzRange(), getOptions().getDoDenoise());
            zoomLevels.add(curZoomLvl);
            curZoomLevel = curZoomLvl;
        }
        
        curZoomLvl.getAxes().rebuild(curZoomLvl.getAxes().getMapDimensions(), screenBounds);
        Rectangle curMapBox = curZoomLvl.getAxes().getMapReferenceFrame().getBounds();
        Rectangle baseMapImageSize = curZoomLvl.getBaseMap().getImageSize();
        curZoomLvl.rebuildMapAxesColors(screenBounds, scans, 
                getOptions().getMsLevel(), getOptions().getMzRange(), getOptions().getDoDenoise());

        /*
        // check if the available Map2DPanel area has changed (in pixels)
        curZoomLvl.getAxes().rebuild(curZoomLvl.getAxes().getMapDimensions(), screenBounds);
        Rectangle curMapBox = curZoomLvl.getAxes().getMapReferenceFrame().getBounds();
        Rectangle baseMapImageSize = curZoomLvl.getBaseMap().getImageSize();
        if (curMapBox.width != baseMapImageSize.width
                || curMapBox.height != baseMapImageSize.height
                || !curZoomLvl.getPrecursorMzRange().equals(displayedMzRegion)
                || curZoomLvl.getMsLevel() != displayedMsLevel) {
            // if we already had a basemap, then check if the new available space is larger than our baseMap
            // if it is, then rebuild the basemap for this zoom level
            curZoomLvl.rebuildMapAxesColors(screenBounds, scans, getDisplayedMsLevel(), getDisplayedMzRegion(), isDoDenoise());
            //System.out.println("rebuildMapAxesColors called! @ " + System.nanoTime() / 1e9 + "s");
            
        }
        */

        parent.revalidate();
        repaintAndUpdateInfoDisplay(curZoomLvl);
    }

    protected void initOptions() {
        IScanCollection scns = getScans();
        if (scns == null) {
            throw new IllegalStateException("Scans must be set prior to calling "
                    + " initOptions()");
        }
        
        Map2DPanelOptions opts = new Map2DPanelOptions();
        Integer msLevelLo = scans.getMapMsLevel2index().firstKey();
        opts.setMsLevel(msLevelLo);

        TreeMap<Integer, IntervalST<Double, TreeMap<Integer, IScan>>> rangeGrps = scans.getMapMsLevel2rangeGroups();

        if (rangeGrps != null && rangeGrps.size() > 0) {
            IntervalST<Double, TreeMap<Integer, IScan>> mzRangesAtMsLvlLo = rangeGrps.get(msLevelLo);
            if (mzRangesAtMsLvlLo != null) {
                if (mzRangesAtMsLvlLo.size() > 1) {
                    // add the infinite interval, which represents the option to show any m/z range
                    opts.setMzRange(OPT_DISPLAY_ALL_MZ_REGIONS);
                } else {
                    IntervalST.Node<Double, TreeMap<Integer, IScan>> range = mzRangesAtMsLvlLo.iterator().next();
                    DoubleRange doubleRange = DoubleRange.fromInterval1D(range.getInterval());
                    opts.setMzRange(doubleRange);
                }
            }
        } else {
            // if scan meta-info didn't contain high/low m/z values in MS1 scan (for example), then
            // the mapping from MS Levels to range groups would not have been created, so we just
            // provide the option to view everything
            opts.setMzRange(OPT_DISPLAY_ALL_MZ_REGIONS);
        }
        
        opts.setDoDenoise(false);
        setOptions(opts);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        // this always scales the iamge to the available space
        // e.g. if a TopComponent has been moved to another mode, which is smaller
        Map2DZoomLevel curZoomLvl = getCurrentZoomLevel();
        if (curZoomLvl != null) {

            // if drag-move was activated, draw everything shifted
            if (mouseHandler.dragMoveInfo != null) {
                // draw the axes
                Map2DAxes axes = curZoomLvl.getAxes();
                Point orig = mouseHandler.dragMoveInfo.origin;
                Point dest = mouseHandler.dragMoveInfo.destination;

                MzRtPoint origMzRt = axes.convertScreenCoordsToMzRt(orig.x, orig.y);
                MzRtPoint destMzRt = axes.convertScreenCoordsToMzRt(dest.x, dest.y);
                double mzShift = destMzRt.getMz() - origMzRt.getMz();
                double rtShift = destMzRt.getRt() - origMzRt.getRt();
                MzRtRegion mapDims = new MzRtRegion(curZoomLvl.getBaseMap().getMzRtRegion());
                mapDims.setMzLo(mapDims.getMzLo() - mzShift);
                mapDims.setRtLo(mapDims.getRtLo() - rtShift);
                axes.rebuild(mapDims, axes.getScreenBounds());

                axes.draw(g2);

                BufferedImage image = getMapImage(curZoomLvl);
                Rectangle mapRefOrig = axes.getMapReferenceFrame();
                Rectangle mapRefShifted = new Rectangle(mapRefOrig);
                mapRefShifted.translate(dest.x - orig.x, dest.y - orig.y);
                Rectangle mapRefIntersection = mapRefShifted.createIntersection(mapRefOrig).getBounds();
                g2.setClip(mapRefIntersection);
                g2.drawImage(image, mapRefShifted.x, mapRefShifted.y,
                        mapRefOrig.width, mapRefOrig.height, null);

            // or just draw as normal
            } else {
                // draw the axes
                Map2DAxes axes = curZoomLvl.getAxes();
                axes.draw(g2);

                // Draw the main image
                BufferedImage image = getMapImage(curZoomLvl);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, this.zoomedInUpscalingAlgo);
                Rectangle ref = axes.getMapReferenceFrame();
                g2.drawImage(image, ref.x, ref.y, ref.width, ref.height, null);
            }
        }


        // draw selection box
        if (mouseHandler.selectionBounds != null) {
            //g2.setXORMode(Color.GRAY);
            g2.setColor(gray50);
            g2.fillRect(mouseHandler.selectionBounds.x, mouseHandler.selectionBounds.y,
                    mouseHandler.selectionBounds.width, mouseHandler.selectionBounds.height);
            g2.setPaintMode();
        }


        g2.dispose();
        if (isZoomInProgress) {
            fireZoomEvent(new ZoomEvent(getCurrentZoomLevel(), ZoomEvent.TYPE.ZOOM_END));
        }
    }

    /**
     * Gets saved image from the current zoomLevel. If the dimensions have
     * changed, the baseMap should have been rebuilt, and the image reset to
     * null, if it's null, then we need to recreate the image and cache it.
     *
     * @param curZoomLvl
     * @return
     */
    public BufferedImage getMapImage(Map2DZoomLevel curZoomLvl) {
        if (curZoomLvl.getImg() != null) {
            return curZoomLvl.getImg();
        }

        BaseMap2D baseMap = curZoomLvl.getBaseMap();
        ColorMap colorMap = curZoomLvl.getColorMap();

        // TODO: WARNING: ACHTUNG: this is a hack to evade a condition when the map is empty

        BufferedImage img = new BufferedImage(baseMap.map[0].length, baseMap.map.length, BufferedImage.TYPE_INT_ARGB);
        double[][] ints = baseMap.getMap();
        Map2DZoomLevel.RangeNormalizer intensityNormalizer = curZoomLvl.getIntensityNormalizer();
        for (int x = 0; x < baseMap.getWidth(); x++) {
            for (int y = 0; y < baseMap.getHeight(); y++) {
                img.setRGB(
                        x,
                        baseMap.getHeight() - y - 1,
                        colorMap.getColor(intensityNormalizer.getScaled(ints[y][x])));
            }
        }
        
        // if we have features
        Features<ILCMSFeature2D<?>> feats = getFeatures();
        if (feats != null && getCurrentZoomLevel().getMsLevel() == 1 && feats.getMs1() != null) {
            double minRt = baseMap.getRtStart();
            double maxRt = baseMap.getRtEnd();
            double minMz = baseMap.getMzStart();
            double maxMz = baseMap.getMzEnd();
        
            // search for features overlapping the viewport
            Rectangle2D.Double query = new Rectangle2D.Double(minMz, minRt, maxMz-minMz, maxRt-minRt);
            Iterable<Entry<ILCMSFeature2D<?>, com.github.davidmoten.rtree.geometry.Rectangle>> q = feats.getMs1().query(query);

            // draw the featrues
            Graphics2D g = (Graphics2D) img.getGraphics();
            ILCMSFeature2D<?> feature;
            com.github.davidmoten.rtree.geometry.Rectangle box;
            for (Entry<ILCMSFeature2D<?>, com.github.davidmoten.rtree.geometry.Rectangle> entry : q) {
                feature = entry.value();
                box = entry.geometry();
                
                
                float rtSpan = box.y1() - box.y2();
                
                Rectangle featureRect = baseMap.convertMzRtBoxToPixelCoords(box.x1(), box.x2(), box.y1() + rtSpan, box.y2() + rtSpan, 0d);
                g.setColor(feature.getColor());
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
                g.fill(featureRect);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
                g.draw(featureRect);
            }
            
        }


        // if we have features available, need to draw them over the image
        ProcessingUmpireFeatures pufs = getPuf();
        List<PeakCluster> featuresInView = new ArrayList<>();
        Color colorUnidentified = Color.RED;
        Color colorIdentified = Color.GREEN;

        double minRt = baseMap.getRtStart();
        double maxRt = baseMap.getRtEnd();
        double minMz = baseMap.getMzStart();
        double maxMz = baseMap.getMzEnd();

        double pcMinRt, pcMaxRt, pcMinMz, pcMaxMz;
        int pcLastNonNullIdx;

        // we check for MS level 1, because I don't yet know how to get MS2 features from CC datastructure.
        if (pufs != null && getCurrentZoomLevel().getMsLevel() == 1) {
            for (PeakCluster pc : pufs.getPeakClusters()) {
                // start iterating from 1, because element [0] should never be null
                for (pcLastNonNullIdx = 1; pcLastNonNullIdx < pc.IsoPeakIndex.length; pcLastNonNullIdx++) {
                    if (pc.IsoPeakIndex[pcLastNonNullIdx] == 0) {
                        break;
                    }
                }
                pcLastNonNullIdx--;

                pcMinMz = pc.mz[0];
                pcMaxMz = pc.mz[pcLastNonNullIdx];
                pcMinRt = pc.startRT;
                pcMaxRt = pc.endRT;
                if (pcMaxRt > minRt && pcMinRt < maxRt && pcMaxMz > minMz && pcMinMz < maxMz) {
                    featuresInView.add(pc);
                }
            }

            Graphics2D g = (Graphics2D) img.getGraphics();
            double widthAddon;
            for (PeakCluster pc : featuresInView) {
                widthAddon = 30d * pc.mz[0] / 1e6;
                for (pcLastNonNullIdx = 1; pcLastNonNullIdx < pc.IsoPeakIndex.length; pcLastNonNullIdx++) {
                    if (pc.IsoPeakIndex[pcLastNonNullIdx] == 0) {
                        break;
                    }
                }
                pcLastNonNullIdx--;

                pcMinMz = pc.mz[0];
                pcMaxMz = pc.mz[pcLastNonNullIdx];
                pcMinRt = pc.startRT;
                pcMaxRt = pc.endRT;
                Rectangle featureRect = baseMap.convertMzRtBoxToPixelCoords(pcMinMz, pcMaxMz, pcMinRt, pcMaxRt, widthAddon);
                if (!pc.Identified) {
                    g.setColor(colorUnidentified);
                } else {
                    g.setColor(colorIdentified);
                }
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
                g.fill(featureRect);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
                g.draw(featureRect);
            }
            g.dispose();
        }
        
        
        
        curZoomLvl.setImg(img);
        return img;
    }



    /**
     * This will calculate the new mz-rt region and call
     * {@link #zoom(umich.gui.viewers.scancollection2d.components.Map2DPanel.MzRtRegion) }.<br/>
     * Zooms into a selected m/z-rt point calculating the zoombox parameters
     * automagically.
     *
     * @param p
     * @param doVerticalZoom whether to zoom in in vertical direction
     * @param doHorizontalZoom whether to zoom in in horizontal direction
     */
    public void zoomIn(MzRtPoint p, boolean doVerticalZoom, boolean doHorizontalZoom) {
        if (isZoomInProgress) {
            OutputWndPrinter.printOut("Map2D", String.format("\tZoom event discarded (another zoom in progress)"));
            return;
        }
        
        if (!doVerticalZoom && !doHorizontalZoom) {
            return;
        }
        // check the validity of the provided point
        if (!checkMzRtPoint(p)) {
            return;
        }
        // construct an mzRtInterval
        Map2DZoomLevel currentZoomLevel = getCurrentZoomLevel();
        if (currentZoomLevel == null) {
            return;
        }

        // if H is our new zoomed width in X (m/z) direction (X is measured in absolute m/z values)
        // then to keep mouse pointer at the same m/z we need to shift our H interval
        // so that our mouse happened to look at exactly the same point
        // offset + orig_mouse_X_rel_pos * H = orig_mouse_X
        BaseMap2D baseMap = currentZoomLevel.getBaseMap();

        double mzSpanOrig = baseMap.getMzSpan();
        double mzSpanNew = mzSpanOrig;
        if (doHorizontalZoom) {
            mzSpanNew = mzSpanOrig / zoomCoef;
        }

        double rtSpanOrig = baseMap.getRtSpan();
        double rtSpanNew = rtSpanOrig;
        if (doVerticalZoom) {
            rtSpanNew = rtSpanOrig / zoomCoef;
        }

        // relative to (0 mz, 0rt) which is bottom left corner of the map
        double mzRelPosOrig = (p.getMz() - baseMap.getMzStart()) / mzSpanOrig;
        double rtRelPosOrig = (p.getRt() - baseMap.getRtStart()) / rtSpanOrig;

        double mzOffset = p.getMz() - mzRelPosOrig * mzSpanNew;
        double rtOffset = p.getRt() - rtRelPosOrig * rtSpanNew;

        // coordinates of the new zoom-box
        double mzLo = mzOffset;
        double mzHi = mzOffset + mzSpanNew;
        double rtLo = rtOffset;
        double rtHi = rtOffset + rtSpanNew;

        // zoom in
        MzRtRegion mzRtInterval = new MzRtRegion(mzLo, mzHi, rtLo, rtHi);
        zoom(mzRtInterval);
    }

    /**
     * Checks if a point lies within the current view (zoom level).
     * @param p
     * @return false if the point is not STRICKTLY inside the viewport
     */
    private boolean checkMzRtPoint(MzRtPoint p) {
        Map2DZoomLevel currentZoomLevel = getCurrentZoomLevel();
        BaseMap2D baseMap = currentZoomLevel.getBaseMap();
        if (p.getMz() > baseMap.getMzEnd() || p.getMz() < baseMap.getMzStart()
                || p.getRt() > baseMap.getRtEnd() || p.getRt() < baseMap.getRtStart()) {
            return false;
        }
        return true;
    }

    /**
     * 1) Starts zoom-in animation sequence.<br/>
     * 2) Creates a zoomed BaseMap2D.<br/>
     * 3) stores the new zoomed map in {@code this.mapZoomedLevels}.<br/>
     * 4) sets the current map to the new one.<br/>
     * 5) calls redraw on this whole component.<br/>
     *
     * @param mzRtInterval where we'll be zooming in
     * zoom-stack. Used by "Go To" action.
     */
    public void zoom(MzRtRegion mzRtInterval) {
        if (isZoomInProgress) {
            OutputWndPrinter.printOut("Map2D", String.format("\tZoom event discarded (another zoom in progress)"));
            return;
        }
        
        OutputWndPrinter.printOut("Map2D", String.format("\tZoom started, into: mz[%.4f-%.4f], rt[%.3f - %.3f]", mzRtInterval.getMzLo(), mzRtInterval.getMzHi(), mzRtInterval.getRtLo(), mzRtInterval.getRtHi()));

        Map2DZoomLevel curZoomLvl = getCurrentZoomLevel();
        if (curZoomLvl == null) {
            return;
        }

        fireZoomEvent(new ZoomEvent(curZoomLvl, ZoomEvent.TYPE.ZOOM_START));

        Rectangle screenBounds = ScreenUtils.getScreenBounds(this);
        // this is a hacky way to limit zoom level storage to just 2 levels
        //  - the top level (0) would generally be the whole map
        //  - the second level (1) would be the current viewport
        int zoomLvl = curZoomLvl.getLevel() + 1 > 0 ? 1 : 0;
        curZoomLvl = new Map2DZoomLevel(zoomLvl, scans, mzRtInterval, screenBounds,
                getOptions().getMsLevel(), getOptions().getMzRange(), getOptions().getDoDenoise());
        if (!curZoomLvl.isMapFilledSuccess()) {
            OutputWndPrinter.printOut("Map2D", String.format("\tZoom cancelled, zoomed map could not be filled"));
            return;
        }
        if (zoomLevels.size() <= zoomLvl) {
            zoomLevels.add(curZoomLvl);
        } else {
            zoomLevels.set(zoomLvl, curZoomLvl);
        }
        curZoomLevel = curZoomLvl;

        repaintAndUpdateInfoDisplay(curZoomLvl);
    }

    /**
     * Either brings you to the previous zoom level, or zooms out completely
     * erasing all other zoom level history.
     *
     * @param doFullZoomOut if true, zooms out to the full LC/MS run overview, other
     * params can be left null if this one is set to true
     * @param p the point around we should be zooming out
     * @param doVerticalZoom should trigger zoom in RT direction only
     * @param doHorizontalZoom trigger zoom in m/z direction only
     */
    public void zoomOut(boolean doFullZoomOut, MzRtPoint p, Boolean doVerticalZoom, Boolean doHorizontalZoom) {
        if (isZoomInProgress) {
            OutputWndPrinter.printOut("Map2D", String.format("\tZoom event discarded (another zoom in progress)"));
            return;
        }
        
        Map2DZoomLevel curZoomLvl = getCurrentZoomLevel();
        if (curZoomLvl == null) {
            return;
        }
        // if we're at the topmost level, then no need to zoom out
        if (curZoomLvl.getLevel() == 0) {
            return;
        }

        if (doFullZoomOut) {
            // for full zoom-out we jsut wipe the whole zoom level list and restore the
            // main zoom-level
            ColorMap curColorMap  = curZoomLvl.getColorMap();
            ColorMap origColorMap = zoomLevels.getFirst().getColorMap();
            if (!curColorMap.equals(origColorMap)) {
                curZoomLevel = new Map2DZoomLevel(0, scans, defaultViewport, ScreenUtils.getScreenBounds(this), 
                        getOptions().getMsLevel(), getOptions().getMzRange(), getOptions().getDoDenoise());
            } else {
                curZoomLevel = zoomLevels.getFirst();
            }
            zoomLevels.clear();
            zoomLevels.add(curZoomLevel);
        } else {
            // Previously:
            //    to jump one level higher in zoom levels, we removed the last element
            //    from stack
            //Map2DZoomLevel previousZoomLevel = zoomLevels.pollLast();
            //curZoomLvl = zoomLevels.peekLast();
            //curZoomLevel = curZoomLvl;

            // However now we don't store zoom levels in a full stack, we're
            // only storing the topmost and the current view, so we call regualr
            // zoom() method after calculating the proper mz-rt region
            if (p == null || doVerticalZoom == null || doHorizontalZoom == null)
                return;
            if (!doVerticalZoom && !doHorizontalZoom)
                return;
            // check the validity of the provided point
            if (!checkMzRtPoint(p)) {
                return;
            }
            // construct an mzRtInterval
            Map2DZoomLevel currentZoomLevel = getCurrentZoomLevel();
            if (currentZoomLevel == null) {
                return;
            }

            // if H is our new zoomed width in X (m/z) direction (X is measured in absolute m/z values)
            // then to keep mouse pointer at the same m/z we need to shift our H interval
            // so that our mouse happened to look at exactly the same point
            // offset + orig_mouse_X_rel_pos * H = orig_mouse_X
            BaseMap2D baseMap = currentZoomLevel.getBaseMap();
            double mzSpanOrig = baseMap.getMzSpan();
            double mzSpanNew = mzSpanOrig;
            if (doHorizontalZoom) {
                mzSpanNew = mzSpanOrig * zoomCoef;
            }

            double rtSpanOrig = baseMap.getRtSpan();
            double rtSpanNew = rtSpanOrig;
            if (doVerticalZoom) {
                rtSpanNew = rtSpanOrig * zoomCoef;
            }

            // relative to (0 mz, 0rt) which is bottom left corner of the map
            double mzRelPosOrig = (p.getMz() - baseMap.getMzStart()) / mzSpanOrig;
            double rtRelPosOrig = (p.getRt() - baseMap.getRtStart()) / rtSpanOrig;

            double mzOffset = p.getMz() - mzRelPosOrig * mzSpanNew;
            double rtOffset = p.getRt() - rtRelPosOrig * rtSpanNew;

            // coordinates of the new zoom-box
            double mzLo = mzOffset;
            double mzHi = mzOffset + mzSpanNew;
            double rtLo = rtOffset;
            double rtHi = rtOffset + rtSpanNew;

            // construct the region to zoom out to, and check if we can use the topmost
            // zoom level
            MzRtRegion zoomOutRegion = new MzRtRegion(mzLo, mzHi, rtLo, rtHi);
            MzRtRegion topMzRtRegion = zoomLevels.getFirst().getBaseMap().getMzRtRegion();
            Rectangle2D intersection = topMzRtRegion.createIntersection(zoomOutRegion);
            MzRtRegion intersectRegion = new MzRtRegion(intersection);
            if (zoomLevels.size() > 1 && intersection.contains(topMzRtRegion)) {
                // do full zoom-out
                zoomOut(true, null, null, null);
                return;
            }
            // do normal zoom out
            zoom(intersectRegion);
        }

        fireZoomEvent(new ZoomEvent(curZoomLvl, ZoomEvent.TYPE.ZOOM_START));
        repaintAndUpdateInfoDisplay(curZoomLevel);
    }

    private void repaintAndUpdateInfoDisplay(Map2DZoomLevel currentZoomLevel) {
        this.repaint();
        BaseMap2D baseMap = currentZoomLevel.getBaseMap();
        infoDisplayer.setRtRange(baseMap.getRtStart(), baseMap.getRtEnd());
        infoDisplayer.setMzRange(baseMap.getMzStart(), baseMap.getMzEnd());
        double colorMapMinValue = currentZoomLevel.getColorMap().getMinValue();
        double colorMapMaxValue = currentZoomLevel.getColorMap().getMaxValue();
        infoDisplayer.setIntensityRange(
                currentZoomLevel.getIntensityNormalizer().getOriginal(colorMapMinValue),
                currentZoomLevel.getIntensityNormalizer().getOriginal(colorMapMaxValue));
        infoDisplayer.refresh();
    }

    private static class DragMoveInfo {
        public Point origin;
        public Point destination;
        public boolean isX;
        public boolean isY;

        public DragMoveInfo(Point origin, Point destination) {
            this.origin = origin;
            this.destination = destination;
            isX = true;
            isY = true;
        }

        public DragMoveInfo(Point origin, Point destination, boolean isX, boolean isY) {
            this.origin = origin;
            this.destination = destination;
            this.isX = isX;
            this.isY = isY;
        }
    }

    //////////////////////////////////////////////////////////////////////
    //
    //
    //             HANDLING MOUSE
    //
    //
    //////////////////////////////////////////////////////////////////////
    protected class Map2dMouseListener extends MouseAdapter implements Map2DZoomEventListener {

        public Point clickPoint;
        private Point lastDragPoint;
        public Rectangle selectionBounds;
        DragMoveInfo dragMoveInfo;
        public Point mouseCoords;

        public Rectangle getSelectionBounds() {
            return selectionBounds;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
                OutputWndPrinter.printOut("Map2D", String.format("Double click: [%d, %d]", e.getX(), e.getY()));
            }

            // zooming out on single left mouse click
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                Map2DAxes axes = getCurrentZoomLevel().getAxes();
                MzRtPoint mzRtPoint = axes.convertScreenCoordsToMzRt(e.getX(), e.getY());

                int maskBtn1CtrlShiftAlt = (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
                int maskBtn1CtrlShift =    (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
                int maskBtn1CtrlAlt =      (InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
                int maskBtn1Ctrl =         (InputEvent.CTRL_DOWN_MASK);

                if ((e.getModifiersEx() & maskBtn1CtrlShiftAlt) == maskBtn1CtrlShiftAlt) {
                    // Click + Ctrl + Shift + Alt= full zoom out
                    zoomOut(true, null, null, null);
                } else if ((e.getModifiersEx() & maskBtn1CtrlShift) == maskBtn1CtrlShift) {
                    // Click + Ctrl + Shift = zoom out m/z only
                    zoomOut(false, mzRtPoint, false, true);
                } else if ((e.getModifiersEx() & maskBtn1CtrlAlt) == maskBtn1CtrlAlt) {
                    // Click + Ctrl + Alt = zoom out RT only
                    zoomOut(false, mzRtPoint, true, false);
                } else if ((e.getModifiersEx() & maskBtn1Ctrl) == maskBtn1Ctrl) {
                    // Click + Ctrl = zoom out both m/z and RT
                    zoomOut(false, mzRtPoint, true, true);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // undo selection, if we were dragging mouse. ZoomIn won't kick in in this case.
            if (selectionBounds != null && SwingUtilities.isRightMouseButton(e)) {
                OutputWndPrinter.printOut("Map2D", String.format("Mouse pressed: [%d, %d], "
                        + "Returning early", e.getX(), e.getY()));
                selectionBounds = null;
                repaint();
                return;
            }
            clickPoint = e.getPoint();
            OutputWndPrinter.printOut("Map2D", String.format("Mouse pressed: [%d, %d]", e.getX(), e.getY()));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            OutputWndPrinter.printOut("Map2D", String.format("Mouse released: [%d, %d]", e.getX(), e.getY()));
            // if we have a selection (mouse was dragged) when mouse is released, then zoom into that region
            if (selectionBounds != null) {
                repaint(selectionBounds);
                OutputWndPrinter.printOut("Map2D", String.format("\tBefore zoom started, zoom box: tlc[%d, %d], brc[%d, %d]",
                        selectionBounds.x, selectionBounds.y, selectionBounds.x + selectionBounds.width, selectionBounds.y + selectionBounds.height));
                MzRtRegion mzRtIntervalOfZoom = getCurrentZoomLevel().getAxes().convertZoomBoxToMzRtRegion(selectionBounds);
                if (selectionBounds.width > 0 && selectionBounds.height > 0) {
                    OutputWndPrinter.printOut("Map2D", String.format("\tZooming in: mz[ from: %.6f, to: %.6f, span: %.6f], rt[from: %.3f, to: %.3f, span: %.3f]", mzRtIntervalOfZoom.getMzLo(), mzRtIntervalOfZoom.getMzHi(), mzRtIntervalOfZoom.getMzSpan(), mzRtIntervalOfZoom.getRtLo(), mzRtIntervalOfZoom.getRtHi(), mzRtIntervalOfZoom.getRtSpan()));
                    zoom(mzRtIntervalOfZoom);
                }
            } else {
                OutputWndPrinter.printOut("Map2D", "No selection was made");
            }

            // if we were in drag-move mode, we need to rebuild the map for the
            // new coordinates
            if (dragMoveInfo != null) {
                Map2DZoomLevel curZoomLvl = getCurrentZoomLevel();
                fireZoomEvent(new ZoomEvent(curZoomLevel, ZoomEvent.TYPE.ZOOM_START));
                // check if the new area is out of the bounds of the original experiment
                MzRtRegion mzRtRegionFull = zoomLevels.getFirst().getBaseMap().getMzRtRegion();
                MzRtRegion mapDims = curZoomLvl.getAxes().getMapDimensions();
                MzRtRegion mzRtRegionNew = new MzRtRegion(mzRtRegionFull.createIntersection(mapDims));
                curZoomLvl.rebuildMapAxesColors(curZoomLevel.getScreenBounds(), mzRtRegionNew, scans, 
                        getOptions().getMsLevel(), getOptions().getMzRange(), getOptions().getDoDenoise());
                setCursor(Cursor.getDefaultCursor());
                repaintAndUpdateInfoDisplay(curZoomLevel);
            }
            
            // whenever any mouse button is released, reset everything
            clickPoint = null;
            lastDragPoint = null;
            dragMoveInfo = null;
            selectionBounds = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point dragPoint = e.getPoint();
            mouseCoords = dragPoint;

            if (clickPoint == null) {
                // this happens after a drag-move is finished.
                // the user releases LMB while still holding Ctrl, and the next
                // mousePressed() is not triggered, when he starts dragging again
                clickPoint = mouseCoords;
            }

            int x = Math.min(clickPoint.x, dragPoint.x);
            int y = Math.min(clickPoint.y, dragPoint.y);
            int width = Math.max(clickPoint.x - dragPoint.x, dragPoint.x - clickPoint.x);
            int height = Math.max(clickPoint.y - dragPoint.y, dragPoint.y - clickPoint.y);


            // check what to do: [select and zoom] or [grab and move]
            if (e.isControlDown()) {
                // grab and move
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                int xShift = dragPoint.x - clickPoint.x;
                int yShift = dragPoint.y - clickPoint.y;
                boolean isX = true;
                boolean isY = true;
                Point origin = new Point(clickPoint);
                Point destination = new Point(dragPoint);
                if (e.isAltDown() && !e.isShiftDown()) {
                    // restrict movement to Y axis (RT)
                    isX = false;
                    destination.x = origin.x;
                } else if (e.isShiftDown() && !e.isAltDown()) {
                    // restrict movement to X axis (m/z)
                    isY = false;
                    destination.y = origin.y;
                }
                dragMoveInfo = new DragMoveInfo(origin, destination, isX, isY);
                OutputWndPrinter.printOut("Map2D",
                    String.format("Mouse dragged [grab-move] from: [%d, %d], to: [%d, %d]\n"
                            + "\tshift: [%d, %d]",
                            clickPoint.x, clickPoint.y, e.getX(), e.getY(), xShift, yShift));
                repaint();

            } else {
                // select and zoom
                selectionBounds = new Rectangle(x, y, width, height);
                if (lastDragPoint == null) {
                    repaint(selectionBounds);
                } else {
                    int clearX = Math.min(x, lastDragPoint.x);
                    int clearY = Math.min(y, lastDragPoint.y);
                    int clearWX = Math.max(x + width, lastDragPoint.x);
                    int clearHY = Math.max(y + height, lastDragPoint.y);
                    repaint(new Rectangle(clearX, clearY, clearWX - clearX, clearHY - clearY));
                }
                OutputWndPrinter.printOut("Map2D",
                    String.format("Mouse dragged [selection] from: [%d, %d], to: [%d, %d]",
                            clickPoint.x, clickPoint.y, e.getX(), e.getY()));
            }
            lastDragPoint = dragPoint;

            updateMouseCoords(mouseCoords);
            ToolTipManager.sharedInstance().mouseMoved(e);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            OutputWndPrinter.printOut("Map2D", String.format("Received mousewheel event: rotation=%.4f, ",
                    e.getPreciseWheelRotation()));

            if (!isZoomInProgress) {
                double preciseWheelRotation = e.getPreciseWheelRotation();
                // initiate zooming sequence
                MzRtPoint mzRtPoint = getCurrentZoomLevel().getAxes().convertScreenCoordsToMzRt(e.getX(), e.getY());
                if (preciseWheelRotation < 0) {
                    // wheel rotated in "from the user" direction
                    // WheelUp + Shift = m/z zoom out
                    // WheelUp + Alt   = RT zoom out
                    boolean doVerticalZoom = true;
                    boolean doHorizontalZoom = true;
                    if (e.isShiftDown()) {
                        doVerticalZoom = false;
                    }
                    if (e.isAltDown()) {
                        doHorizontalZoom = false;
                    }
                    zoomIn(mzRtPoint, doVerticalZoom, doHorizontalZoom);
                } else {
                    // wheel rotated in "towards the user" direction
                    // WheelDown + Shift = m/z zoom out
                    // WheelDown + Alt   = RT zoom out
                    boolean doVerticalZoom = true;
                    boolean doHorizontalZoom = true;
                    if (e.isShiftDown()) {
                        doVerticalZoom = false;
                    }
                    if (e.isAltDown()) {
                        doHorizontalZoom = false;
                    }
                    zoomOut(false, mzRtPoint, doVerticalZoom, doHorizontalZoom);
                }
            } else {
                OutputWndPrinter.printOut("Map2D", String.format("\tMousewheel event discarded, another zoom in progress"));
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseCoords = e.getPoint();
            updateMouseCoords(mouseCoords);
            ToolTipManager.sharedInstance().mouseMoved(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mouseCoords = null;
            updateMouseCoords(mouseCoords);
            ToolTipManager.sharedInstance().mouseExited(e);
        }

        @Override
        public void handleZoomEvent(ZoomEvent e) {
            if (e.isStart()) {
                OutputWndPrinter.printOut("Map2D", String.format(" MouseHandler got zoom START event"));
            } else {
                OutputWndPrinter.printOut("Map2D", String.format("MouseHandler got zoom END event"));
            }

        }
    }


    private void updateMouseCoords(Point p) {
        if (p == null || curZoomLevel == null) {
            infoDisplayer.setMouseCoords(null, null);
        } else if (mouseHandler.getSelectionBounds() != null) {
            Rectangle selectionBounds = mouseHandler.getSelectionBounds();
            Map2DZoomLevel curZoomLvl = getCurrentZoomLevel();
            MzRtPoint mzRt = curZoomLvl.getAxes().convertScreenCoordsToMzRt(p.x, p.y);
            
            infoDisplayer.setMouseCoords(mzRt.getMz(), mzRt.getRt());
        } else {
            MzRtPoint mzRt = getCurrentZoomLevel().getAxes().convertScreenCoordsToMzRt(p.x, p.y, true, true);
            infoDisplayer.setMouseCoords(mzRt.getMz(), mzRt.getRt());
        }
        infoDisplayer.refresh();
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        String tooltipText;

        if (getCurrentZoomLevel() == null) {
            return super.getToolTipText();
        }

        Rectangle selectionBounds = mouseHandler.getSelectionBounds();
        if (selectionBounds != null) {
            // if we're in "selection" mode, then show range of mz and rt
            MzRtRegion zoomBoxMzRt = getCurrentZoomLevel().getAxes().convertZoomBoxToMzRtRegion(selectionBounds);

            // calc summed intensity in selection box
            Map2DZoomLevel curZoomLvl = getCurrentZoomLevel();
            BaseMap2D baseMap = curZoomLvl.getBaseMap();
            double[][] map = curZoomLvl.getBaseMap().getMapRaw();

            if (map.length == 0)
                return "";

            int mapYlen = map.length;
            int mapXlen = map.length > 0 ? map[0].length : -1;
            // Top Left Corner
            int tlcX = baseMap.extrapolateMzToX(zoomBoxMzRt.getMzLo());
            if (tlcX < 0) {
                tlcX = 0;
            } else if (tlcX > mapXlen - 1) {
                tlcX = mapXlen - 1;
            }
            int tlcY = baseMap.extrapolateRtToY(zoomBoxMzRt.getRtHi());
            if (tlcY < 0) {
                tlcY = 0;
            } else if (tlcY > mapYlen - 1) {
                tlcY = mapYlen - 1;
            }
            // Bottom Right Corner
            int brcX = baseMap.extrapolateMzToX(zoomBoxMzRt.getMzHi());
            if (brcX < 0) {
                brcX = 0;
            } else if (brcX > mapXlen - 1) {
                brcX = mapXlen - 1;
            }
            int brcY = baseMap.extrapolateRtToY(zoomBoxMzRt.getRtLo());
            if (brcY < 0) {
                brcY = 0;
            } else if (brcY > mapYlen - 1) {
                brcY = mapYlen - 1;
            }

            double sum = 0;
            for (int y = brcY; y <= tlcY; y++) {
                for (int x = tlcX; x <= brcX; x++) {
                    sum = sum + map[y][x];
                }
            }

            double ppm = Double.NaN;
            if (zoomBoxMzRt.getMzSpan() < 1d) {
                if (mouseHandler.clickPoint.x < e.getX()) {
                    // this means the mouse was dragged to the left, so mzHi of the
                    // selection box should be used for PPM calc
                    ppm = (zoomBoxMzRt.getMzSpan() / zoomBoxMzRt.getMzHi()) * 1e6d;
                } else {
                    // otherwise mzLo is used
                    ppm = (zoomBoxMzRt.getMzSpan() / zoomBoxMzRt.getMzLo()) * 1e6d;
                }
            }
            StringBuilder sb = new StringBuilder(128);
            sb.append(String.format("mz: %.2f - %.2f (%.4f", zoomBoxMzRt.getMzLo(), zoomBoxMzRt.getMzHi(), zoomBoxMzRt.getMzSpan()));
            if (ppm < 500d) {
                sb.append(String.format("=%.0fppm", ppm));
            }
            sb.append("), ");
            sb.append(String.format("rt: %.2f - %.2f (%.2f), ab: %,.0f",
                    zoomBoxMzRt.getRtLo(), zoomBoxMzRt.getRtHi(), zoomBoxMzRt.getRtSpan(), sum));
            tooltipText = sb.toString();

//            tooltipText = String.format("mz: %.2f - %.2f (%.4f), "
//                                      + "rt: %.2f - %.2f (%.2f), "
//                                      + "ab: %,.0f",
//                    zoomBoxMzRt.getMzLo(), zoomBoxMzRt.getMzHi(), zoomBoxMzRt.getMzSpan(),
//                    zoomBoxMzRt.getRtLo(), zoomBoxMzRt.getRtHi(), zoomBoxMzRt.getRtSpan(),
//                    sum);
        } else {
            // we're not in selection mode
            Map2DZoomLevel curZoomLvl = getCurrentZoomLevel();
            MzRtPoint mzRt = curZoomLvl.getAxes().convertScreenCoordsToMzRt(e.getX(), e.getY());
            BaseMap2D baseMap = curZoomLvl.getBaseMap();
            int x = baseMap.extrapolateMzToX(mzRt.getMz());
            int y = baseMap.extrapolateRtToY(mzRt.getRt());
            double[][] map = curZoomLvl.getBaseMap().getMap();

            // search in some radius of the map for the max value
            if (map.length > 0 && map[0].length > 0) {
                int r = 3; // search radius
                double maxMapValInRadius = Double.NEGATIVE_INFINITY;
                for (int yPtr = y-r; yPtr <= y+r; yPtr++) {
                    if (yPtr < map.length && yPtr > 0) {
                        for (int xPtr = x-r; xPtr <= x+r; xPtr++) {
                        if (xPtr < map[0].length && xPtr > 0 && map[yPtr][xPtr] > maxMapValInRadius) {
                            maxMapValInRadius = map[yPtr][xPtr];
                        }
                    }
                    }
                }

                //we don't typically see data where intensities are lower than 10
                if (maxMapValInRadius != 0 && maxMapValInRadius < 10d) {
                    tooltipText = String.format("mz: %.4f, rt: %.2f, ab: %,.2f", mzRt.getMz(), mzRt.getRt(), maxMapValInRadius);
                } else {
                    tooltipText = String.format("mz: %.4f, rt: %.2f, ab: %,.0f", mzRt.getMz(), mzRt.getRt(), maxMapValInRadius);
                }
            } else {
                tooltipText = String.format("mz: %.4f, rt: %.2f", mzRt.getMz(), mzRt.getRt());
            }
        }

        return tooltipText;
    }

    /**
     * Change the default tooltip, to my custom one, which can display multiple lines
     * in one tooltip.
     * @return 
     */
    @Override
    public JToolTip createToolTip() {
        if (tooltip == null) {
            ToolTipMap2D tip = new ToolTipMap2D();
            tip.setSeparator(", ");
            tip.setComponent(this);
            tooltip = tip;
        }
        return tooltip;
    }


    /**
     * Implementations of message handlers are stored in this class.
     * As well as all pub/sub support.
     */
    public class BusHandler extends AbstractBusPubSub implements Map2DZoomEventListener {
        private Map2DPanel map = Map2DPanel.this;
        private volatile boolean isRespondingToRecievedZoomEvent = false;

        @Handler
        public void eventbusHandleZoom2DEvent(MsgZoom2D m) {
            // we're also receiving our own events, which this Map published to the bus
            // need to filter those out
            if (m.getOrigin() != this) {
                isRespondingToRecievedZoomEvent = true;
                Map2DZoomLevel zoomLvlTo = m.getZoomLvl();
                Map2DZoomLevel zoomLvlFrom = Map2DPanel.this.getCurrentZoomLevel();
                if (zoomLvlFrom.getMsLevel() == zoomLvlTo.getMsLevel()) {
                    // if it's the same ms level, just do a fully synced zoom
                    map.zoom(zoomLvlTo.getAxes().getMapDimensions());
                } else {
                    // if ms levels are different, then we won't zoom m/z range
                    MzRtRegion region = new MzRtRegion(zoomLvlFrom.getAxes().getMapDimensions());
                    region.setRtLo(zoomLvlTo.getAxes().getMapDimensions().getRtLo());
                    region.setRtSpan(zoomLvlTo.getAxes().getMapDimensions().getRtSpan());
                    map.zoom(region);
                }
            }
        }

        @Override
        public void handleZoomEvent(ZoomEvent e) {
            if (e.isFinish()) {
                if (!isRespondingToRecievedZoomEvent) {
                    // if we got zoom-finished event from our own map, while it was
                    // not in response to a remote message-request for zoom, then
                    // we publish our zoom event to all buses
                    for (MBassador<Object> bus : getBuses()) {
                        bus.publish(new MsgZoom2D(this, e.getZoomLevel()));
                    }
                } else {
                    // this zoom was in response to a zoom-message, so we just unset the flag and do nothing.
                    isRespondingToRecievedZoomEvent = false;
                }
            }
        }

        @Handler
        public void eventbusHandlerZoom1DEvent(MsgZoom1D m) {
            if (m.getOrigin() != this) {
                Map2DZoomLevel zoomLvlFrom = Map2DPanel.this.getCurrentZoomLevel();
                if (m.getScan().getMsLevel() != zoomLvlFrom.getMsLevel()) {
                    return;
                }
                isRespondingToRecievedZoomEvent = true;
                MzRtRegion region = new MzRtRegion(zoomLvlFrom.getAxes().getMapDimensions());
                region.setMzLo(m.getRegion().getMzLo());
                region.setMzSpan(m.getRegion().getMzSpan());
                map.zoom(region);
            }
        }
        
        @Handler
        public void eventbusHandlerFeatureClickEvent(MsgFeatureClick m) {
            if (m.getOrigin() != this) {
                MzRtRegion region = m.getRegion();
                map.zoom(region);
            }
        }
    }
}
