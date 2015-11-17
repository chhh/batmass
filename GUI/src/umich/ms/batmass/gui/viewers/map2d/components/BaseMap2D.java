/*
 * License placeholder
 */

package umich.ms.batmass.gui.viewers.map2d.components;


import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.openide.util.Exceptions;
import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.api.util.ArrayUtils;
import umich.ms.batmass.gui.viewers.map2d.options.Map2DOptions;
import umich.ms.batmass.nbputils.OutputWndPrinter;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.exceptions.FileParsingException;
import umich.ms.util.Interval1D;
import umich.ms.util.IntervalST;

/**
 * This class stores extrapolated information from ScanCollection mapped to
 * a pixel intensity array.
 * @author dmitriya
 */
public final class BaseMap2D {

    /**
     * Rows are retention time slots
     * Columns are mass bins, mapping m/z to pixels on screen
     */
    public double[][] map;
    /** Values in this map should not be affected by different user selectable options. */
    public double[][] mapRaw;
    private int[] filledRowIds;

    private int width;
    private int height;

    private final int availableWidth;
    private final int availableHeight;

    /** NOT USED AND NEVER INITIALIZED */
    private int scanNumLo;
    /** NOT USED AND NEVER INITIALIZED */
    private int scanNumHi;
    private final double rtLo;
    private final double rtHi;
    private final double rtSpan;

    private final double mzLo;
    private final double mzHi;
    private final double mzSpan;
    
    private Interval1D<Double> precursorMzRange;
    private int msLevel;
    private boolean doDenoise = false;

    private final MzRtRegion mapDimensions;

    // these are set in options
    // TODO: These values specified here are useless, because anyway
    // they are relaoded from combined default+user configs
    private boolean doInterpRt;
    private boolean doBasePeakMode;
    private boolean doProfileModeGapFilling;
    private boolean doMzCloseZoomGapFilling;
    public int colorLevels;
    public List<Color> colorPalette;

    /**
     * Use this value in conjunction with {@link #totalIntensityMin} to determine dynamic range
     * of this map.
     */
    private double totalIntensityMax = Double.NEGATIVE_INFINITY;
    /**
     * The lowest intensity pixel in this map.
     */
    private double totalIntensityMin = Double.POSITIVE_INFINITY;
    /**
     * The lowers intensity non-zero pixel in this map.
     */
    private double totalIntensityMinNonZero = Double.POSITIVE_INFINITY;

    private static final double epsilon = 1e-8d;

    /**
     * This holds the "binned" map of peaks from spectra. Screen pixels are used
     * as bins.
     * @param availableWidth available width in pixels, where the map will be drawn
     * @param availableHeight screen hight in pixels, where the map will be drawn
     * @param mapDimensions m/z-rt region which this map will be displaying
     * @param msLevel
     * @param precursorMzRange if null, then all possible precursor ranges will be used
     */
    public BaseMap2D(int availableWidth, int availableHeight, MzRtRegion mapDimensions, 
            int msLevel, Interval1D<Double> precursorMzRange) {
        this.rtHi = mapDimensions.getRtHi();
        this.rtLo = mapDimensions.getRtLo();
        this.mzHi = mapDimensions.getMzHi();
        this.mzLo = mapDimensions.getMzLo();
        this.precursorMzRange = precursorMzRange != null ? precursorMzRange: Map2DPanel.OPT_DISPLAY_ALL_MZ_REGIONS;
        this.msLevel = msLevel;

        if (rtHi < rtHi) {
            throw new IllegalArgumentException("RT-start must be <= RT-end");
        }
        if (mzHi < mzLo) {
            throw new IllegalArgumentException("MZ-start must be <= MZ-end");
        }
        this.mapDimensions = mapDimensions;
        this.availableWidth = availableWidth;
        this.availableHeight = availableHeight;

        // add a little something to spans, so that max RT values didn't map
        // to non-existent matrix rows/columns
        this.rtSpan = rtHi - rtLo + epsilon;
        this.mzSpan = mzHi - mzLo + epsilon;
        //this.rtSpan = nextAfter(rtEnd - rtStart, 1.0d);
        //this.mzSpan = nextAfter(mzEnd - mzStart, 1.0d);
        try {
            setStaticVarsFromConfig();
        } catch (ConfigurationException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * TODO: this is a rather ugly solution overall, needs refactoring.
     * @throws ConfigurationException
     * @throws IOException
     */
    private void setStaticVarsFromConfig() throws ConfigurationException, IOException {
        CompositeConfiguration config = Map2DOptions.getInstance().getConfig();

        doBasePeakMode = config.getBoolean("doBasePeakMode");
        doInterpRt = config.getBoolean("doUpscaling");
        doMzCloseZoomGapFilling = config.getBoolean("doMzCloseZoomGapFilling");
        doProfileModeGapFilling = config.getBoolean("doProfileModeGapFilling");
        colorLevels = config.getInt("colorLevels");
        colorPalette = Arrays.asList(Map2DOptions.getColorsFromConfig(config));
    }
    
    /**  */
    private void initErrorFillingState() {
        this.totalIntensityMax = Double.MIN_NORMAL; //TODO: why did I do that MIN_NORMAL instead of zero??? don't remember
        this.totalIntensityMin = 0;
        this.totalIntensityMinNonZero = 0;
    }


    /**
     * Fills the map given a scan collection.
     * @param scans
     * @return True, if filling was done successfully.
     *         False if something bad happened, e.g. scanCollection didn't contain any scans between rtStart & rtEnd
     */
    public boolean fillMapFromScans(IScanCollection scans) {
        int pixelsVertical = availableHeight;
        height = pixelsVertical;
        width = availableWidth;

        NavigableMap<Integer, IScan> scansByRtSpanAtMsLevel = scans.getScansByRtSpanAtMsLevel(rtLo, rtHi, msLevel);;
        if (!precursorMzRange.equals(Map2DPanel.OPT_DISPLAY_ALL_MZ_REGIONS)) {
            // if only scans from specific precursor m/z window were requested
            IntervalST<Double, TreeMap<Integer, IScan>> precursorRanges = scans.getMapMsLevel2rangeGroups().get(msLevel);
            if (precursorRanges != null) {
                IntervalST.Node<Double, TreeMap<Integer, IScan>> node = precursorRanges.get(precursorMzRange);
                if (node != null) {
                    // these are all the scans at proper MS level and in proper precursor m/z range
                    TreeMap<Integer, IScan> scansInMzRange = node.getValue();
                    // now filter this TreeMap to only leave scans that are in our RT range
                    Integer numLo = scansByRtSpanAtMsLevel.firstKey();
                    Integer numHi = scansByRtSpanAtMsLevel.lastKey();
                    numLo = scansInMzRange.ceilingKey(numLo);
                    numHi = scansInMzRange.floorKey(numHi);
                    scansByRtSpanAtMsLevel = scansInMzRange.subMap(numLo, true, numHi, true);
                }
            }
        }
        if (scansByRtSpanAtMsLevel == null || scansByRtSpanAtMsLevel.size() == 0) {
            initErrorFillingState();
            return false;
        }
        scanNumLo = scansByRtSpanAtMsLevel.firstKey();
        scanNumHi = scansByRtSpanAtMsLevel.lastKey();



        // compare the number of scans to available vertical pixels
        int scanCount = scansByRtSpanAtMsLevel.size();
        this.map = new double[height][width];
        this.mapRaw = new double[height][width];

        IScan scan;
        TreeMap<Integer, IScan> mapNum2scan = scans.getMapNum2scan();
        IScan[] scansToAverage = new IScan[4];
        ISpectrum spectrum;
        Integer mzIdxLo, mzIdxHi;
        int x, y;
        boolean hasProfile = false;
        double[] masses, intensities;
        filledRowIds = new int[scansByRtSpanAtMsLevel.size()];
        int idx = 0;
        double denoisingTimeCounter = 0;
        for (Map.Entry<Integer, IScan> num2scan : scansByRtSpanAtMsLevel.entrySet()) {
            scan = num2scan.getValue();
            if (doProfileModeGapFilling && !scan.isCentroided()) {
                hasProfile = true;
            }
            spectrum = null;
            try {
                spectrum = scan.fetchSpectrum();
            } catch (FileParsingException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (spectrum == null) {
                continue;
            }
            
            y = extrapolateRtToY(scan.getRt());
            filledRowIds[idx] = y;
            idx++;
            if (y > this.map.length - 1) {
                OutputWndPrinter.printErr("DEBUG",
                    String.format("BaseMap2D: (y > this.map.length-1) for scan #%d.\n"
                            + "\ty=%d, len-1=%d, height=%d\n"
                            + "\trt=%.20f, rtStart=%.20f, rtEnd=%.20f, rtSpan=%.20f",
                            scan.getNum(), y, this.map.length - 1, height, scan.getRt(), rtLo, rtHi, rtSpan));
            }
            masses = spectrum.getMZs();
            intensities = spectrum.getIntensities();

            mzIdxLo = spectrum.findMzIdxCeiling(mzLo);
            mzIdxHi = spectrum.findMzIdxFloor(mzHi);
            if (mzIdxLo == null || mzIdxHi == null) {
                OutputWndPrinter.printErr("DEBUG",
                        String.format("BaseMap2D: mzIdxLo or mzIdxHi were null for scan #%d. "
                                + "Not filling the map from them.", scan.getNum()));
                continue;
            }
            if (mzIdxLo < 0 || mzIdxLo > masses.length-1) {
                OutputWndPrinter.printErr("DEBUG",
                        String.format("BaseMap2D: (mzIdxLo < 0 || mzIdxLo > masses.length-1) for scan #%d", scan.getNum()));
            }
            if (mzIdxHi < 0 || mzIdxHi > masses.length-1) {
                OutputWndPrinter.printErr("DEBUG",
                        String.format("BaseMap2D: (mzIdxHi < 0 || mzIdxHi > masses.length-1) for scan #%d", scan.getNum()));
            }
            
            double denoiseThreshold = Double.NaN;
            boolean applyDenoise = isDoDenoise();
            if (applyDenoise) {
                long start = System.nanoTime();
                denoiseThreshold = findDenoiseThreshold(masses, intensities);
                double denoisingTime = (System.nanoTime() - start) / 1e6;
                denoisingTimeCounter = denoisingTimeCounter + denoisingTime;
                if (Double.isNaN(denoiseThreshold)) {
                    applyDenoise = false;
                }
            }
            
            for (int i = mzIdxLo; i <= mzIdxHi; i++) {

                x = extrapolateMzToX(masses[i]);
                addPeakRaw(x, y, intensities[i]);

                if (applyDenoise && intensities[i] < denoiseThreshold) {
                    continue;
                }
                if (x > this.map[0].length-1) {
                    OutputWndPrinter.printErr("DEBUG",
                        String.format("BaseMap2D: (x > this.map[0].length-1) for scan #%d.\n"
                                + "\tx=%d, len-1=%d, width=%d,\n"
                                + "\ti=%d, masses[i]=%.20f, mzStart=%.20f, mzEnd=%.20f, mzSpan=%.20f",
                                scan.getNum(), x, this.map[0].length-1, width, i, masses[i], mzLo, mzHi, mzSpan));
                }



                // boost if present in previous/next scan
                // boost if present in previous/next scan// boost if present in previous/next scan// boost if present in previous/next scan// boost if present in previous/next scan// boost if present in previous/next scan
//                double curIntensity = intensities[i];
//                final int maxScanSpan = 2000;
//                int numScansDisplayed = scansByRtSpanAtMsLevel.size();
//                if (false && numScansDisplayed <= maxScanSpan) {
//                    double maxIntInVicinity;
//                    double intensityUpdateFactor = 1;
//                    double dm, dmPpm, dmUpdateFactor;
//                    int maxIntIdx;
//                    double[] curInts, curMzs;
//
//                    final int scanNumShift = 1;
//                    final double ppmTolerance = 15d;
//
//                    if (scan.getNum() % 1000 == 0) {
//                        System.out.printf("Averaging for scan %d\n", scan.getNum());
//                    }
//                    scansToAverage[0] = mapNum2scan.get(scan.getNum() - scanNumShift*2);
//                    scansToAverage[1] = mapNum2scan.get(scan.getNum() - scanNumShift);
//                    scansToAverage[2] = mapNum2scan.get(scan.getNum() + scanNumShift);
//                    scansToAverage[3] = mapNum2scan.get(scan.getNum() + scanNumShift*2);
//                    double curMass = masses[i];
//
//                    for (IScan avgScan : scansToAverage) {                        
//                        if (avgScan == null) {
//                            continue;
//                        }
//                        ISpectrum s = avgScan.getSpectrum();
//                        if (s == null) {
//                            continue;
//                        }
//                        int[] mzIdxs = s.findMzIdxsWithinPpm(curMass, ppmTolerance);
//                        dm = Double.NEGATIVE_INFINITY;
//                        dmUpdateFactor = 1;
//                        intensityUpdateFactor = 1;
//                        if (mzIdxs != null) {
//                            curInts = s.getIntensities();
//                            curMzs = s.getMZs();
//                            maxIntIdx = -1;
//                            maxIntInVicinity = Double.NEGATIVE_INFINITY;
//                            for (int j = mzIdxs[0]; j <= mzIdxs[1]; j++) {
//                                if (curInts[j] > maxIntInVicinity) {
//                                    maxIntIdx = j;
//                                }
//                            }
//                            if (maxIntIdx != -1) {
//                                intensityUpdateFactor = curInts[maxIntIdx];
//                                dm = Math.abs(curMass - curMzs[maxIntIdx]);
//
//                                dmPpm = dm / (curMass / 1e6d);
//                                if (dmPpm > ppmTolerance) {
//                                    dmUpdateFactor = 0d;
//                                    throw new IllegalStateException("dmUpdateFactor set to zero, should not happen");
//                                } else {
//                                    dmUpdateFactor = (1 - Math.pow(dmPpm / ppmTolerance, 2d));
//                                }
//                            } else {
//                                throw new IllegalStateException("Strange condition, should never be triggered");
//                            }
//                        } else {
//                            // if masses in the vicinity not found, then penalize
//                            // TODO: this should be dependent on the chosen distribution for mass deviations
//                            //       see dmFactor
//                            intensityUpdateFactor = 1;
//                            dmUpdateFactor = (1 - Math.pow(0.5d, 2d));
//                        }
//                        
//                        curIntensity = curIntensity * (intensityUpdateFactor * dmUpdateFactor);
//                    }
//                }

//                addPeak(x, y, curIntensity);
                addPeak(x, y, intensities[i]);
//                if (curIntensity > 1e6) {
//                    addPeak(x, y, curIntensity);
//                }
            }

            if (hasProfile && doProfileModeGapFilling) {
                double pixelSizeMz = getMzSpan() / availableWidth;
                if (pixelSizeMz < 0.01) {
                    fillProfileGaps(0, y, pixelSizeMz);
                }
            }
        }
        if (isDoDenoise()) {
            OutputWndPrinter.printErr("DEBUG",
                    String.format("Denoising took on average: %.2fms (%d scans)\n",
                            (denoisingTimeCounter) / scansByRtSpanAtMsLevel.size(), scansByRtSpanAtMsLevel.size()));
        }

        if (hasProfile) { // profile mode spectrum
            if (!doProfileModeGapFilling && doMzCloseZoomGapFilling) {
                applySavitzkyGolay(map);
            }
        } else { // !hasProfile => centroided spectrum
            if (doMzCloseZoomGapFilling) {
                applySavitzkyGolay(map);
            }
        }

        findMinMaxIntensities();

        // if we created the full-sized version of the map, then a lot of rows might
        // be zero, because no scan actually mapped to this row of pixels
        // so we just fill it with the same pixels as in the previous filled row.
        if (doInterpRt) {
            for (int filledRowIdx = 0; filledRowIdx < filledRowIds.length - 1; filledRowIdx++) {
                int rowLo = filledRowIds[filledRowIdx];
                int rowHi = filledRowIds[filledRowIdx+1];
                for (int rowToFillIdx = rowLo + 1; rowToFillIdx < rowHi; rowToFillIdx++) {
                    System.arraycopy(map[rowLo], 0, map[rowToFillIdx], 0, width);
                }
            }
        }

        // add a tiny bit to the total intensity, allows not to care about
        // edge values when mapping intensities to colors.
        // Adding MIN_NORMAL, as totalIntensity shoule be a value > 1.0
        totalIntensityMax += 1e-8;
        return true;
    }

    /**
     * Applies the smoothing in-place.
     * 5 point 1D version.
     * @param map
     */
//    private static final double[] savGolCoefs = {-3d/35d, 12d/35d, 17d/35d, 12d/35d, -3d/35d};
    private static final double[] savGolCoefs3 = {0.25d, 0.5d, 0.25d};
    private static final double[] savGolCoefs5 = {0.1, 0.15d, 0.6d, 0.15d, 0.1d};
    private void applySavitzkyGolay(double[][] map)  {
        double[] usedCoefs;
        double mzPerPixel = mzSpan / (double)width;

        // this was the old conditional application of Savitzky-Golay smoothing
//        if (mzPerPixel < 0.05) {
//            usedCoefs = savGolCoefs5;
//        } else if (mzPerPixel < 0.2) {
//            usedCoefs = savGolCoefs3;
//        } else {
//            return;
//        }
        usedCoefs = savGolCoefs3;
        
        int tailLen = usedCoefs.length / 2;
        int wndLen = usedCoefs.length;
        if (map[0].length < wndLen)
            return;
        double[] wnd = new double[wndLen];
        double val;
        int y, x, i;
        double[] Xtmp = new double[map[0].length];
        try {
            for (y = 0; y < map.length; y++) {
                double[] X = map[y];
                for (x = tailLen; x < X.length - tailLen; x++) {
                    val = 0d;
                    for (i = 0; i < wndLen; i++) {
                        val = val + usedCoefs[i] * X[x - tailLen + i];
                    }
                    Xtmp[x] = val;
                }
                System.arraycopy(Xtmp, 0, X, 0, X.length);
            }
        } catch (IndexOutOfBoundsException e) {
            Exceptions.printStackTrace(e);
        }
    }

    private void findMinMaxIntensities() {
        double d;
        totalIntensityMax = Double.MIN_NORMAL;
        totalIntensityMin = Double.POSITIVE_INFINITY;
        totalIntensityMinNonZero = Double.POSITIVE_INFINITY;
        for (int i = 0; i < map.length; i++) {
            double[] ds = map[i];
            for (int j = 0; j < ds.length; j++) {
                d = ds[j];
                if (d > totalIntensityMax)
                    totalIntensityMax = d;
                if (d < totalIntensityMin)
                    totalIntensityMin = d;
                if (d > 0d && d < totalIntensityMinNonZero)
                    totalIntensityMinNonZero = d;
            }
        }
    }

//    private void interpRepeatedZeroes() {
//        int minConsecutiveRepeatsRequired = 3;
//        int curRepeatSize = 0;
//        int curRepeatStart, curRepeatEnd;
//        for (int y = 0; y < map.length; y++) {
//            double[] X = map[y];
//            for (int x = 0; x < X.length; x++) {
//                if (X[x] == 0) {
//                    curRepeatSize++;
//                } else {
//                    if (prevRepeatSize != 0 && prevRepeatSize == curRepeatSize) {
//                    consecutiveCnt++;
//                    if (consecutiveCnt >= minConsecutiveRepeatsRequired) {
//                        sizeCnt[curRepeatSize]++;
//                    }
//                    } else {
//                        // starting new sequence of repeat counting
//                        consecutiveCnt = 1;
//                    }
//                    prevRepeatSize = curRepeatSize;
//                    curRepeatSize = 0;
//                }
//            }
//        }
//    }

    private Integer findRepeatedZeroes(int y) {
        int minSameSizeRepeats = 1;
        int minConsecutiveRepeatsRequired = 3;
        int prevRepeatSize = 0;
        int curRepeatSize = 0;
        int consecutiveCnt = 0;
        int[] sizeCnt = new int[width];

        for (int j = 0; j < width; j++) {
            if (map[y][j] == 0) {
                curRepeatSize++;
            } else {
                if (prevRepeatSize != 0 && prevRepeatSize == curRepeatSize) {
                    consecutiveCnt++;
                    if (consecutiveCnt >= minConsecutiveRepeatsRequired) {
                        sizeCnt[curRepeatSize]++;
                    }
                } else {
                    // starting new sequence of repeat counting
                    consecutiveCnt = 1;
                }
                prevRepeatSize = curRepeatSize;
                curRepeatSize = 0;
            }
        }

        int maxVal = 0;
        int maxIdx = 0;
        for (int i = 0; i < sizeCnt.length; i++) {
            if (sizeCnt[i] > maxVal) {
                maxVal = sizeCnt[i];
                maxIdx = i;
            }
        }
        if (maxVal > minSameSizeRepeats && maxIdx > 0) {
            OutputWndPrinter.printErr("Map2D",
                String.format("BaseMap2D: Profile mode statistics gathered, "
                        + "most frequent common distance was %d", maxIdx));
            return maxIdx;
        }
        return null;
    }

    private void fillProfileGaps(int targetRepeatLength, int y, double pixelSize) {
        int curRepeatSize = 0;
        double prevNonZeroVal = 0, curNonZeroVal = 0;
        int prevNonZeroIdx = 0;
        for (int j = 0; j < width; j++) {
            if (map[y][j] == 0) {
                curRepeatSize++;
            } else {
                if (pixelSize * curRepeatSize < 0.01) {
                    curNonZeroVal = map[y][j];
                    if (curRepeatSize != 0 && curRepeatSize > targetRepeatLength && prevNonZeroVal != 0) {
                        // we have found an appropriate repeat
                        // do linear interpolation
                        int cnt = 0;
                        double stepSize = (curNonZeroVal - prevNonZeroVal) / (curRepeatSize+1);
                        for (int idx = j-1; idx >= prevNonZeroIdx ; idx--) {
                            cnt++;
                            map[y][idx] = map[y][j] - stepSize * cnt;
                        }
                    }
                }
                curRepeatSize = 0;
                prevNonZeroVal = curNonZeroVal;
                prevNonZeroIdx = j;
            }
        }
    }

    public int extrapolateRtToY(double rt) {
//        if (rt == rtEnd) {
//            // this is the only case when RT falls exactly on the border of the last
//            // pixel row and thus gets mapped to the next row, which doen't exist
//            return height - 1;
//        }
        return (int)(((rt - rtLo) / rtSpan) * height);
    }

    /**
     *
     * @param mz
     * @return
     */
    public int extrapolateMzToX(double mz) {
//        if (mz == mzEnd) {
//            // this is the only case when mz falls exactly on the border of the last
//            // pixel column and thus gets mapped to the next column, which doen't exist
//            return width-1;
//        }
        return (int)(((mz - mzLo) / mzSpan) * width);
    }

    /**
     * Convert mz-rt coordinates to pixel coordinates in BaseMap.
     * @param mzLo
     * @param mzHi
     * @param rtLo
     * @param rtHi
     * @param widthAddon will be subtracted from mzLo and added to mzHi
     * @return 
     */
    public Rectangle convertMzRtBoxToPixelCoords(double mzLo, double mzHi, double rtLo, double rtHi, double widthAddon) {
        int pixelMzLo = this.extrapolateMzToX(mzLo - widthAddon);
        if (pixelMzLo < 0) pixelMzLo = 0;
        int pixelRtHi = this.extrapolateRtToY(rtHi);
        if (pixelRtHi < 0) pixelRtHi = 0;
        int pixelMzHi = this.extrapolateMzToX(mzHi + widthAddon);
        if (pixelMzHi > this.getWidth() - 1) pixelMzHi = this.getWidth() - 1;
        int pixelRtLo = this.extrapolateRtToY(rtLo);
        if (pixelRtLo > this.getHeight() - 1) pixelRtLo = this.getHeight() - 1;
        int pixelWidth = pixelMzHi - pixelMzLo;
        if (pixelWidth < 2) pixelWidth = 2;
        int pixelHeight = pixelRtHi - pixelRtLo;
        if (pixelHeight < 2) pixelHeight = 2;
        Rectangle featureRect = new Rectangle(
                pixelMzLo, this.getHeight() - pixelRtHi - 1,
                pixelWidth, pixelHeight);
        return featureRect;
    }



    private void addPeakRaw(int x, int y, double intensity) {
        mapRaw[y][x] += intensity;
    }
    private void addPeak(int x, int y, double intensity) {
        if (doBasePeakMode) {
            if (map[y][x] < intensity)
                map[y][x] = intensity;
        } else {
            map[y][x] += intensity;
        }
    }
    
    private double findDenoiseThreshold(double[] mz, double[] ints) {
        double background = Double.NaN;
        if (mz.length < 10) {
            return background;
        }
        double quantile = 0.5;      
//        double[] copyOfInts = Arrays.copyOf(ints, ints.length);
//        Arrays.sort(copyOfInts);
//        double lower = copyOfInts[0];
//        double upper = copyOfInts[(int) (copyOfInts.length * quantile)];
        double[] calcedQuantile = ArrayUtils.calcQuantileValue(ints, quantile);
        double lower = calcedQuantile[0];
        double upper = calcedQuantile[1];
        double interval = (upper - lower) / 20d;

        int count1, count2, count3, count4, noise;
        double ratio = 1.2d;

        for (double bk = lower; bk < upper; bk += interval) {
            count1 = 0;
            count2 = 0;
            count3 = 0;
            count4 = 0;
            noise = 0;
            background = bk;
            
            int len = mz.length;
            double dist;
            for (int idxLo = 0; idxLo < len-1; idxLo++) {
                if (ints[idxLo] < background) {
                    continue;
                }
                for (int idxHi = idxLo + 1; idxLo < len; idxLo++) {
                    if (idxHi < background) {
                        continue;
                    }
                    dist = mz[idxHi] - mz[idxLo];
                    if (dist < 0.23 || dist > 1.05 ) {
                        noise++;
                        break;
                    }
                    
                    if (ints[idxLo] > ints[idxHi]) {
                        if (       dist > 0.24 && dist < 0.26) {
                            count1++;
                            break;
                        } else if (dist > 0.30 && dist < 0.36) {
                            count2++;
                            break;
                        } else if (dist > 0.45 && dist < 0.55) {
                            count3++;
                            break;
                        } else if (dist > 0.95 && dist < 1.05) {
                            count4++;
                            break;
                        }
                    } 
                }
            }
            if (noise < (count1 + count2 + count3 + count4) * ratio) {
                break;
            }
        }
        return background;
    }

    public double[][] getMap() {
        return map;
    }

    public double[][] getMapRaw() {
        return mapRaw;
    }

    /**
     * The IDs of the rows in the base map, that were actually filled from the scans.
     * Other rows of the map might have been filled because of the RT
     * interpolation option.
     * @return
     */
    public int[] getFilledRowIds() {
        return filledRowIds;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle getImageSize() {
        return new Rectangle(availableWidth, availableHeight);
    }

    public int getScanNumStart() {
        return scanNumLo;
    }

    public int getScanNumEnd() {
        return scanNumHi;
    }

    public double getRtStart() {
        return rtLo;
    }

    public double getRtEnd() {
        return rtHi;
    }

    public double getRtSpan() {
        return rtSpan;
    }

    public double getMzStart() {
        return mzLo;
    }

    public double getMzEnd() {
        return mzHi;
    }

    public double getMzSpan() {
        return mzSpan;
    }

    /**
     * Convenience method to get the real (m/z, rt) dimensions of this map.
     * @return a new instance of MzRtInterval class
     */
    public MzRtRegion getMzRtRegion() {
        return new MzRtRegion(mzLo, mzHi, rtLo, rtHi);
    }

    public double getTotalIntensityMax() {
        return totalIntensityMax;
    }

    public double getTotalIntensityMin() {
        return totalIntensityMin;
    }

    public double getTotalIntensityMinNonZero() {
        return totalIntensityMinNonZero;
    }

    public boolean isBasepeakMode() {
        return doBasePeakMode;
    }

    public List<Color> getColorPalette() {
        return colorPalette;
    }

    public int getColorLevels() {
        return colorLevels;
    }

    public boolean isDoDenoise() {
        return doDenoise;
    }

    public void setDoDenoise(boolean doDenoise) {
        this.doDenoise = doDenoise;
    }

}
