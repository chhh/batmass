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
package umich.ms.batmass.gui.viewers.map2d.components;

import umich.ms.batmass.gui.viewers.map2d.norm.RangeNormalizer;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.api.util.color.ColorMap;
import umich.ms.batmass.gui.management.EBus;
import umich.ms.batmass.gui.viewers.map2d.norm.RangeNormalizers;
import umich.ms.batmass.gui.viewers.map2d.options.Map2DOptions;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.util.Interval1D;

/**
 * After creating a new instance, check the value of {@link #wasMapFilledSuccess()}.
 * If false - it means that the basemap was still created, but it is filled with zeroes,
 * so you could actually display it.
 * @author Dmitry Avtonomov
 */
public class Map2DZoomLevel {
    private final int level;

    private BaseMap2D baseMap;
    private ColorMap colorMap;
    private BufferedImage img;
    private Map2DAxes axes;
    private int msLevel;
    private Interval1D<Double> precursorMzRange;
    private EBus bus;

    private int width;
    private int height;
    private boolean isMapFilledSuccess = false;
    private RangeNormalizer intensityNormalizer;
    private static final double targetRange = 100d;

    /**
     * Struct which stores BaseMap, ColorMap and a rendered Image for a specific
     * region of LC-MS, given the available rendering on-screen space.
     * @param zoomLevel pseudo zoom-level, actually not used for anything.
     * @param scans ScanCollection to init the BaseMap from.
     * @param mapDimaensions The portion of scans to display [mzLo-mzHi, rtLo-rtHi]
     * @param screenBounds available screen space in the JFrame where this map
     *          will be rendered.
     */
    public Map2DZoomLevel(int zoomLevel, IScanCollection scans, MzRtRegion mapDimaensions, Rectangle screenBounds, 
            int msLevel, Interval1D<Double> precursorMzRange, String doDenoise, EBus bus) {
        this.level = zoomLevel;
        this.msLevel = msLevel;
        this.precursorMzRange = precursorMzRange;
        this.bus = bus;
        initMapAxesColors(screenBounds, mapDimaensions, scans, msLevel, precursorMzRange, doDenoise, bus);
    }

    private void initMapAxesColors(Rectangle screenBounds, MzRtRegion mapDimensions, 
            IScanCollection scans, int msLevel, Interval1D<Double> precursorMzRange, 
            String doDenoise, EBus bus) {
        this.width = screenBounds.width;
        this.height = screenBounds.height;
        axes = new Map2DAxes(mapDimensions, screenBounds);
        Rectangle ref = axes.getMapReferenceFrame();
        baseMap = new BaseMap2D(ref.width, ref.height, mapDimensions, msLevel, precursorMzRange, bus);
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
            int msLevel, Interval1D<Double> precursorMzRange, String doDenoise) {
        this.msLevel = msLevel;
        this.precursorMzRange = precursorMzRange;
        MzRtRegion mapDims = new MzRtRegion(baseMap.getMzStart(), baseMap.getMzEnd(), baseMap.getRtStart(), baseMap.getRtEnd());
        initMapAxesColors(screenBounds, mapDims, scans, msLevel, precursorMzRange, doDenoise, bus);
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
            int msLevel, Interval1D<Double> precursorMzRange, String doDenoise) {
        this.msLevel = msLevel;
        this.precursorMzRange = precursorMzRange;
        initMapAxesColors(screenBounds, mapDimensions, scans, msLevel, precursorMzRange, doDenoise, bus);
        img = null;
    }
    
    /**
     * TODO: this is a rather ugly solution overall, needs refactoring.
     * @throws ConfigurationException
     * @throws IOException
     */
    private void setStaticVarsFromConfig() throws ConfigurationException, IOException {
        
        
    }

    private RangeNormalizer createNormalizer(double minNonZero, double max) {
        double curRange = max - minNonZero;
        
        CompositeConfiguration config = Map2DOptions.getInstance().getConfig();
        // TODO: the default value is set here, in case the user config is messed up
        String normalizerName = config.getString("intensityNormalizer", "LOG");

        RangeNormalizers norm = RangeNormalizers.valueOf(normalizerName);
        RangeNormalizer rangeNormalizer = norm.getRangeNormalizer();
        rangeNormalizer.configure(max, minNonZero, targetRange);
        
        return rangeNormalizer;
//        return new RangeNormalizerImplDoNothing();

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
