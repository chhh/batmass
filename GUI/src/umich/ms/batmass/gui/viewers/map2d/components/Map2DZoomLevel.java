/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.viewers.map2d.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.api.util.color.ColorMap;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.util.Interval1D;

/**
 * After creating a new instance, check the value of {@link #wasMapFilledSuccess()}.
 * If false - it means that the basemap was still created, but it is filled with zeroes,
 * so you could actually display it.
 * @author dmitriya
 */
public class Map2DZoomLevel {
    private final int level;

    private BaseMap2D baseMap;
    private ColorMap colorMap;
    private BufferedImage img;
    private Map2DAxes axes;
    private int msLevel;
    private Interval1D<Double> precursorMzRange;

    private int width;
    private int height;
    private boolean isMapFilledSuccess = false;
    private RangeNormalizer intensityNormalizer;
    private static final double targetRange = 100d;

    /**
     * Struct which stores BaseMap, ColorMap and a rendered Image for a specific
     * region of LC-MS, given the available rendering on-screen space.
     * @param level pseudo zoom-level, actually not used for anything.
     * @param scans ScanCollection to init the BaseMap from.
     * @param mapDimaensions The portion of scans to display [mzLo-mzHi, rtLo-rtHi]
     * @param screenBounds available screen space in the JFrame where this map
     *          will be rendered.
     */
    public Map2DZoomLevel(int level, IScanCollection scans, MzRtRegion mapDimaensions, Rectangle screenBounds, 
            int msLevel, Interval1D<Double> precursorMzRange, boolean doDenoise) {
        this.level = level;
        this.msLevel = msLevel;
        this.precursorMzRange = precursorMzRange;
        initMapAxesColors(screenBounds, mapDimaensions, scans, msLevel, precursorMzRange, doDenoise);
    }

    private void initMapAxesColors(Rectangle screenBounds, MzRtRegion mapDimensions, IScanCollection scans, 
            int msLevel, Interval1D<Double> precursorMzRange, boolean doDenoise) {
        this.width = screenBounds.width;
        this.height = screenBounds.height;
        axes = new Map2DAxes(mapDimensions, screenBounds);
        Rectangle ref = axes.getMapReferenceFrame();
        baseMap = new BaseMap2D(ref.width, ref.height, mapDimensions, msLevel, precursorMzRange);
        baseMap.setDoDenoise(doDenoise);
        isMapFilledSuccess = baseMap.fillMapFromScans(scans);
        double minNonZero = baseMap.getTotalIntensityMinNonZero();
        double max = baseMap.getTotalIntensityMax();
        // TODO: we offset the intensity by minNonZero here,
        // there should be an option to switch to real Min value
        intensityNormalizer = createNormalizer(minNonZero, max);
        colorMap = new ColorMap(
                getColorPalette(),
                intensityNormalizer.getScaled(baseMap.getTotalIntensityMinNonZero()),
                intensityNormalizer.getScaled(max));
    }
    
    /**
     * You might call this method if the available screen space has changed to
     * update the BaseMap properly. Cached Image will be reset, so upon next
     * repaint it will be re-rendered from the fresh BaseMap.
     * @param width
     * @param height
     * @param scans
     */
    public void rebuildMapAxesColors(Rectangle screenBounds, IScanCollection scans, 
            int msLevel, Interval1D<Double> precursorMzRange, boolean doDenoise) {
        this.msLevel = msLevel;
        this.precursorMzRange = precursorMzRange;
        MzRtRegion mapDims = new MzRtRegion(baseMap.getMzStart(), baseMap.getMzEnd(), baseMap.getRtStart(), baseMap.getRtEnd());
        initMapAxesColors(screenBounds, mapDims, scans, msLevel, precursorMzRange, doDenoise);
        img = null;
    }

    /**
     * You might call this method if the available screen space has changed to
     * update the BaseMap properly. Cached Image will be reset, so upon next
     * repaint it will be re-rendered from the fresh BaseMap.<br/>
     * This method is used, for example, to redraw the map after drag-move events
     * by the user (map panning).
     * @param width
     * @param height
     * @param scans
     */
    public void rebuildMapAxesColors(Rectangle screenBounds, MzRtRegion mapDimensions, IScanCollection scans, 
            int msLevel, Interval1D<Double> precursorMzRange, boolean doDenoise) {
        this.msLevel = msLevel;
        this.precursorMzRange = precursorMzRange;
        initMapAxesColors(screenBounds, mapDimensions, scans, msLevel, precursorMzRange, doDenoise);
        img = null;
    }

    private Map2DZoomLevel.RangeNormalizer createNormalizer(double minNonZero, double max) {
        double curRange = max - minNonZero;

        double base = org.apache.commons.math3.util.FastMath.exp((FastMath.log(max) - FastMath.log(minNonZero)) / targetRange);
        return new RangeNormalizerImplLog(base);

        // TODO: replaced this version, which was not doing any normalization in the intensity
        // range was smaller than the defined threshold, with the above two lines (i.e. the
        // normalization is always executed)
        // It worked better this way for metabolomics Agilent data, where we had signals
        // of very small and very large intensity. Don't know why, though...
//        if (curRange <= targetRange) {
//            // TODO: exp int this case
//            // if the range is smaller than target, then find exp base
//            return new RangeNormalizerImplDoNothing();
//        } else {
//            // if the range is larger than target, then find log base
//            double base = FastMath.exp((FastMath.log(max) - FastMath.log(minNonZero)) / targetRange);
//            return new RangeNormalizerImplLog(base);
//        }

    }

    public static class RangeNormalizerImplDoNothing implements RangeNormalizer {
        @Override
        public double getScaled(double x) {
            return x;
        }

        @Override
        public double getOriginal(double x) {
            return x;
        }

        @Override
        public boolean equals(RangeNormalizer other) {
            return other instanceof RangeNormalizerImplDoNothing;
        }
    }

    public static class RangeNormalizerImplLog implements RangeNormalizer {
        private final double base;

        public RangeNormalizerImplLog(double base) {
            this.base = base;
        }

        public double getBase() {
            return base;
        }

        @Override
        public double getScaled(double x) {
            return FastMath.log(base, x);
            //return FastMath.log(x)/FastMath.log(base);
            //return Math.log(x)/Math.log(base);
        }

        @Override
        public double getOriginal(double x) {
            return FastMath.pow(base, x);
        }

        @Override
        public boolean equals(RangeNormalizer other) {
            if (other instanceof RangeNormalizerImplLog) {
                RangeNormalizerImplLog norm = (RangeNormalizerImplLog)other;
                return norm.getBase() == this.getBase();
            }
            return false;
        }
    }

    public static interface RangeNormalizer {
        public double getScaled(double x);
        public double getOriginal(double x);
        public boolean equals(RangeNormalizer other);
    }

    public Rectangle getScreenBounds() {
        return new Rectangle(width, height);
    }

    private int[] getColorPalette() {
//        return ColorLib.getHotPalette(Map2DPanel.colorLevels);
        return getJetPalette(baseMap.getColorLevels());
    }

    /**
     * Returns a Jet colormap almost like the Jet map from MatLab.
     * to white.
     * @param size the size of the color palette
     * @return the color palette
     */
    public int[] getJetPalette(int size) {
        int[] cm = new int[size];

        List<Color> colorPivots = baseMap.getColorPalette();


          // High contrast
//        colorPivots.add(Color.decode("#000000")); // black
//        colorPivots.add(Color.decode("#00007F")); // dark blue
//        colorPivots.add(Color.decode("#007FFF")); // azure
//        colorPivots.add(Color.decode("#FFFF00")); // yellow
//        colorPivots.add(Color.decode("#00FFFF")); // cyan
//        colorPivots.add(Color.decode("#FF7F00")); // orange
//        colorPivots.add(Color.decode("#0000FF")); // blue
//        colorPivots.add(Color.decode("#FF0000")); // red
//        colorPivots.add(Color.decode("#7FFF7F")); // light green
//        colorPivots.add(Color.decode("#7F0000")); // dark red


        int transCount = colorPivots.size()-1;
        int transSize = (int)Math.ceil((double)size/(double)transCount);
        int curTrans = 0;
        int curMapPoint = 0;
        Color c1, c2;
        int r1,r2,g1,g2,b1,b2;
        float[] comp1 = new float[3];
        float[] comp2 = new float[3];
        float[] comp3 = new float[3];
        float weight;

        for (int transNum = 0; transNum < transCount; transNum++) {
            int curTransSize = Math.min(transSize, size - curMapPoint);
            c1 = colorPivots.get(transNum);
            c2 = colorPivots.get(transNum + 1);
            c1.getColorComponents(comp1);
            c2.getColorComponents(comp2);
            for (int ptTransNum = 0; ptTransNum < curTransSize; ptTransNum++) {
                weight = ((float)ptTransNum) / curTransSize;
                for (int i = 0; i < comp1.length; i++) {
                    comp3[i] = (1.0f - weight) * comp1[i] + weight * comp2[i];
                }
                Color c3 = new Color(comp3[0], comp3[1], comp3[2]);
                cm[curMapPoint] = c3.getRGB();
                curMapPoint++;
            }
        }

        return cm;
    }
    
    /**
     * Pretty useless, {@link Map2DPanel} tracks the zoom level itself, as well
     * as the stack of cached zoom BaseMaps and BufferedImages, this one is
     * only here for convenience. Or rather inconvenience, because you need to
     * provide the level in the constructor.
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * A check for the last filling of the BaseMap. Filling a BaseMap might fail
     * if provided rt/mz range did not contain any scans or mz values. The BaseMap
     * is still created though at the desired size, but should be all zeroes,
     * so the Image produced from it should be all of the "zero color" from the colormap
     * which generally should be black or dark blue.
     * It is only useful to check this flag if you want to avoid unnecessary zooming-in
     * if this flag is FALSE it basically means there will never be any info to
     * display, no matter how much more you zoom-in.
     * Default: false.
     * @return
     */
    public boolean isMapFilledSuccess() {
        return isMapFilledSuccess;
    }

    public BaseMap2D getBaseMap() {
        return baseMap;
    }

    /**
     * The last cached rendered Image at a particular width/height size.
     * @return
     */
    public BufferedImage getImg() {
        return img;
    }

    /**
     * The colormap for the last filling of the BaseMap2D.
     * It is non-null even if filling the BaseMap had failed.
     * @return
     */
    public ColorMap getColorMap() {
        return colorMap;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    /**
     * This should be exactly the same as cached image size.
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * This should be exactly the same as cached image size.
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the lowest mapped color of the associated colormap.
     * I.e. the color that'll be used to paint zero-intensity pixels.
     * @return
     */
    public Color getBaseColor() {
        return new Color(colorMap.getColor(colorMap.getMinValue()));
    }

    /**
     * Axes for the last creation/rebuilding of the baseMap
     * @return
     */
    public Map2DAxes getAxes() {
        return axes;
    }

    /**
     * When zoom level is built for a BaseMap, this normalizer calculates
     * the proper intensity scaling, so that the range between minimum non-zero
     * intensity and the maximum intensity was around a 1000.
     * @return
     */
    public RangeNormalizer getIntensityNormalizer() {
        return intensityNormalizer;
    }

    /**
     * Displayed MS Level at the time of creation of this zoom level.
     * @return 
     */
    public int getMsLevel() {
        return msLevel;
    }

    /**
     * Displayed precursor m/z range at the time of creation of this zoom level.<br/>
     * Might be <code>null</code> or {@link Map2DPanel#DISPLAY_ALL_MZ_REGIONS} (or
     * any Interval1D<Double> of course).
     * @return 
     */
    public Interval1D<Double> getPrecursorMzRange() {
        return precursorMzRange;
    }
}
