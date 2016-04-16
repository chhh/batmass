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

import umich.ms.batmass.gui.core.api.data.MzRtRegion;
import umich.ms.batmass.gui.core.api.data.MzRtPoint;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Holds all the information for drawing a Map2DPanel. LC/MS region,
 * available screen space, X and Y axes rendered with respect to that info,
 * as well as reference frames for the Map itself and the axes.<br/>
 * The Map should be rendered precisely to <code>mapReferenceFrame</code>,
 * use {@link #getMapReferenceFrame() } for this.
 * To draw the axes just call {@link #draw(java.awt.Graphics2D) } providing
 * the Graphics of the corresponding {@link Map2DPanel}.
 * @author dmitriya
 */
public final class Map2DAxes {
    /** The m/z - rt  region of current BaseMap2D */
    private MzRtRegion mapDimensions;
    /** Available screen real estate at the time of creation */
    private Rectangle screenBounds;
    private BufferedImage yAxis;
    private BufferedImage xAxis;
    private int xAxisHeight;
    private int yAxisWidth;
    private Rectangle mapReferenceFrame;
    private Rectangle xAxisReferenceFrame;
    private Rectangle yAxisReferenceFrame;


    private enum Axis {X, Y};

    // TODO: these might be configurable by some API. Hardcoded for now.
    private final String xAxisLabel = "m/z";
    private final String yAxisLabel = "RT(min)";

    // TODO: these should go to options
    private final boolean doDrawAxes = true;
    private final Font fontAxis = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
    // these formats are only used once during initial precalculation of axis size;
    private final DecimalFormat yAxisFormatShort = new DecimalFormat("0.0", formatSymbols);
    private final DecimalFormat yAxisFormatLong  = new DecimalFormat("0.00", formatSymbols);
    private final DecimalFormat xAxisFormatShort = new DecimalFormat("0",    formatSymbols);
    private final DecimalFormat xAxisFormatLong  = new DecimalFormat("0.00", formatSymbols);
    private final DecimalFormat xAxisFormatXLong  = new DecimalFormat("0.000", formatSymbols);
    private final double xAxisFormatLongThreshold = 5.0d; // when X axis range is under that value, long format will be used for labels
    private final double xAxisFormatXLongThreshold = 0.5d; // when X axis range is under that value, extra long format will be used for labels
    private final int pxBorderToNumber = 2; // from border of JFrame to the number
    private final int pxNumberToTick =  3;  // from number to it's large tick
    private final int pxNumberToNumber = 10; // min px distance between numbers
    private final int pxLabelToNumber = 10; // min px distance between the last number and axis label
    private final int pxTickSmall = 2; // in pixels, length of minor tick on axis
    private final int pxTickLarge = 4; // in pixels, length of major tick on axis
    private final int pxAxisLineWidth = 3; // in pixels, line width for axes themselves
    private final double[] xAxisSteps = new double[]{1, 2, 2.5, 5};
    private final double[] yAxisSteps = new double[]{1, 2, 2.5, 5};
//    private final double[] yAxisSteps = new double[]{1, 2, 4, 6, 8};
//    private final double[] yAxisSteps = new double[]{1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5};


    /**
     *
     * @param mapDimensions m/z and RT span of the LC/MS area being shown
     * @param screenBounds the total available space for the map and axes,
     * use {@link ScreenUtils#getScreenBounds(java.awt.Container) } on the
     * component in which you place the {@link Map2DPanel}.
     */
    public Map2DAxes(MzRtRegion mapDimensions, Rectangle screenBounds) {
        this.mapDimensions = mapDimensions;
        this.screenBounds = screenBounds;
        init();
    }

    /**
     * Call this method to reuse the existing Map2DAxes object in new context.
     * The result is exactly the same as calling the constructor and replacing
     * the original object with a new one.
     */
    public void rebuild(MzRtRegion mapDimensions, Rectangle screenBounds) {
        this.mapDimensions = mapDimensions;
        this.screenBounds = screenBounds;
        init();
    }

    private void init() {
        // When building axes, we need to create Y axis first, because it's
        // width is variable, depending on e.g. the number of decimal places
        // we need to show.
        // To build the Y axis we need the height of the X axis, but we don't
        // have X axis yet! BUT we can precisely estimate the future height of
        // the X axis - it's not variable
        estimateXAxisHeight();
        yAxis = createImageYAxis();
        // now we have built the Y axis, and we can use it's actual width
        // when building the X axis
        yAxisWidth = yAxis.getWidth();
        xAxis = createImageXAxis();
        mapReferenceFrame = createMapReferenceFrame();

        // TODO: create axis reference frames
    }

    /** Should only be called once in the constructor/init(). */
    private void estimateXAxisHeight() {
        Double xHi = mapDimensions.getMzHi();
        DecimalFormat df = getXAxisNumberFormatting();
        String xHiStrFormatted = df.format(xHi);

        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)dummyImg.getGraphics();

        // largest number label height
        TextLayout numberLayout = new TextLayout(xHiStrFormatted, fontAxis, g.getFontRenderContext());
        int heightNumber = numberLayout.getPixelBounds(null, 0, 0).height;

        // axis label height
        TextLayout labelLayout = new TextLayout(xAxisLabel, fontAxis, g.getFontRenderContext());
        int heightLabel = labelLayout.getPixelBounds(null, 0, 0).height;

        int numberHeight = calcAxisSize(heightNumber);
        int labelHeight  = calcAxisSize(heightLabel);
        g.dispose();
        xAxisHeight = Math.max(numberHeight, labelHeight);
    }

    /**
     * Should only be called once in the constructor/init().
     * @return
     */
    private BufferedImage createImageYAxis() {

        // set up dummy graphics
        Graphics2D dummyG = getDummyGraphics();

        // rt span of the axis
        double range = mapDimensions.getRtSpan();
        // pixel length of the axis, by this point in constructor we have
        // precisely estimated the height of the X axis
        int axisLength = screenBounds.height - xAxisHeight;

        // calc label placement, so that we knew when to stop drawing numbers
        Rectangle labelBoundingBox = calcPixelTextBoundingBox(dummyG, yAxisLabel);
        int widthLabel = labelBoundingBox.width; // because it will be rotated 90 degrees
        // This is in global pixel coords, not relative to axis start.
        // We count from the Top Left Corner which is [0, 0]
        int labelBottomBorderPx = pxBorderToNumber + widthLabel + pxLabelToNumber;

        // optimize m/z steps for large ticks
        TickStepInfo tickStepInfo = calcRtTickLargeStep(dummyG, range, axisLength);
        dummyG.dispose();

        // calculate Y axis width based on the tick info we've created
        yAxisWidth = calcYAxisWidth(tickStepInfo);

        // set up the real image for the axis
        BufferedImage img = new BufferedImage(yAxisWidth, screenBounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor(Color.BLACK);
        g.setFont(fontAxis);

        // draw numbers and ticks
        double pxPerRt = axisLength / range;
        double yStartValOffset = mapDimensions.getRtLo() % tickStepInfo.step;
        double yStartVal = mapDimensions.getRtLo() - yStartValOffset + tickStepInfo.step;

        for (int i = 0; i < tickStepInfo.numSteps; i++) {
            double yCurVal = yStartVal + i * tickStepInfo.step;
//            int yCurStartPx = axisLength - extrapolateRtToY(yCurVal, axisLength); // Y pixel position relative to end of axis
            int yCurStartPx = (axisLength - 1) - extrapolateRtToY(yCurVal, axisLength); // Y pixel position relative to end of axis
            Point tickEndCoord = drawTickLarge(g, Axis.Y, yCurStartPx); // this is the startig large tick
            String yCurValStrFmt = tickStepInfo.decimalFormat.format(yCurVal);
            Rectangle strBox = calcPixelTextBoundingBox(g, yCurValStrFmt);
            if ((tickEndCoord.y + (int)strBox.getHeight()/ 2) < labelBottomBorderPx) {
                // ensure we leave space to draw the axis label
                break;
            }
            Point numberCoord  = calcAxisNumberDrawingCoords(Axis.Y, strBox, tickEndCoord.x, tickEndCoord.y);
            g.drawString(yCurValStrFmt, numberCoord.x, numberCoord.y);
        }


        // draw the label
        int xOffset = pxBorderToNumber + labelBoundingBox.height;
        int yOffset = pxBorderToNumber + labelBoundingBox.width;
        AffineTransform transformOrig = g.getTransform();
        g.rotate(-Math.PI/2, xOffset, yOffset);
        g.drawString(yAxisLabel, xOffset, yOffset);
        g.setTransform(transformOrig);



        // DEBUG: draw the axis bounding box
        //g.setColor(Color.GREEN);
        //g.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);


        g.dispose();
        return img;
    }

    /**
     * Should only be called once in the constructor.
     * @return
     */
    private BufferedImage createImageXAxis() {
        BufferedImage img = new BufferedImage(screenBounds.width, xAxisHeight, BufferedImage.TYPE_INT_ARGB);

        // set up graphics
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor(Color.BLACK);
        g.setFont(fontAxis);

        double range = mapDimensions.getMzSpan();     // m/z span of the axis
        int axisLength = calcXAxisPxLength(img.getWidth()); // pixel length of the axis

        // calc label placement, so that we knew when to stop drawing numbers
        Rectangle2D labelPreciseBox = calcPreciseTextBoundingBox(g, xAxisLabel);
        Rectangle labelBoundingBox = labelPreciseBox.getBounds();
        int widthLabel = labelBoundingBox.width;
        // this is in global pixel coords, not relative to axis start
        int labelLeftBorderPx = yAxisWidth + axisLength - widthLabel - pxBorderToNumber - pxLabelToNumber;


        // optimize m/z steps for large ticks
        TickStepInfo tickStepInfo = calcMzTickLargeStep(g, range, axisLength);

        // draw numbers and ticks
        double pxPerMz = axisLength / range;
        double xStartValOffset = mapDimensions.getMzLo() % tickStepInfo.step;
        double xStartVal = mapDimensions.getMzLo() - xStartValOffset + tickStepInfo.step;

        for (int i = 0; i < tickStepInfo.numSteps; i++) {
            double xCurVal = xStartVal + i * tickStepInfo.step;
            int xCurStartPx = extrapolateMzToX(xCurVal, axisLength); // X pixel position relative to start of axis
            Point tickEndCoord = drawTickLarge(g, Axis.X, xCurStartPx); // this is the startig large tick
            String xCurValStrFmt = tickStepInfo.decimalFormat.format(xCurVal);
            Rectangle strBox = calcPixelTextBoundingBox(g, xCurValStrFmt);
            if ((tickEndCoord.x + (int)strBox.getWidth() / 2) > labelLeftBorderPx) {
                // ensure we leave space to draw the axis label
                break;
            }
            Point numberCoord  = calcAxisNumberDrawingCoords(Axis.X, strBox, tickEndCoord.x, tickEndCoord.y);
            g.drawString(xCurValStrFmt, numberCoord.x, numberCoord.y);
        }


        // draw the label
        int xOffset = yAxisWidth + axisLength - labelBoundingBox.width - pxBorderToNumber;
        int yOffset = pxAxisLineWidth + pxTickLarge + pxNumberToTick + labelBoundingBox.height;
        g.drawString(xAxisLabel, xOffset, yOffset);

        // DEBUG: draw the axis bounding box
        //g.setColor(Color.RED);
        //g.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);

        g.dispose();
        return img;
    }

    /** Don't forget to dispose of the graphics object */
    private Graphics2D getDummyGraphics() {
        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dummyG = (Graphics2D)dummyImg.getGraphics();
        dummyG.setColor(Color.BLACK);
        dummyG.setFont(fontAxis);
        return dummyG;
    }

    private int calcXAxisPxLength(int imgSize) {
        return imgSize - yAxisWidth;
    }

    /** relative to the start of the axis, not the axis image */
    private int extrapolateRtToY(double rt, int axisLength) {
        return (int)(((rt - mapDimensions.getRtLo()) / mapDimensions.getRtSpan()) * axisLength);
    }

    /** relative to the start of the axis, not the axis image */
    private int extrapolateMzToX(double mz, int axisLength) {
        if (mz == mapDimensions.getMzHi()) {
            // this is the only case when mz falls exactly on the border of the last
            // pixel column and thus gets mapped to the next column, which doen't exist
            return axisLength - 1;
        }
        return (int)(((mz - mapDimensions.getMzLo()) / mapDimensions.getMzSpan()) * axisLength);
    }

    /**
     * Draw a large tick on the axis. Calculates the position of the pixel in the
     * image automatically, given it's realtive pixel position to the start of the axis.
     * @param g
     * @param axis
     * @param offset in pixels from the: <br/>
     * <li>X-axis: real start of the axis ("min value" on the axis)</li>
     * <li>Y-axis: from the end of the real axis</li>
     * @return the coordinates of the outward last pixel of the tick (to be used in
     * {@link #calcAxisNumberDrawingCoords(umich.gui.viewers.scancollection2d.components.Map2DAxes.Axis, java.awt.Graphics2D, java.lang.String, int, int) })
     */
    private Point drawTickLarge(Graphics2D g, Axis axis, int offset) {
        Point outwardTickLinePx;
        int x1, y1, x2, y2;
        if (axis == Axis.X) {
            x1 = offset + yAxisWidth;
            y1 = pxAxisLineWidth;
            x2 = x1;
            y2 = pxTickLarge + pxAxisLineWidth;
            outwardTickLinePx = new Point(x2, y2);
        } else {
            x1 = yAxisWidth - pxAxisLineWidth;
            y1 = offset;
            x2 = x1 - pxTickLarge;
            y2 = y1;
            outwardTickLinePx = new Point(x2, y2);
        }
        g.drawLine(x1, y1, x2, y2);
        return outwardTickLinePx;
    }

    private TickStepInfo calcMzTickLargeStep(Graphics2D g, double axisRange, int axisLength) {
        double[] steps = xAxisSteps;

        // this is the largest number we might have on the axis
        Double xHi = mapDimensions.getMzHi();
        DecimalFormat df = new DecimalFormat("0");
        String xHiStrFmt = df.format(xHi);

        // find out how many digits after dot we need to show.
        // we start with showing no digits at all
        Rectangle strBox = calcPixelTextBoundingBox(g, xHiStrFmt);
        int widthNumberPlusSpacing = strBox.width + pxNumberToNumber;
        int maxPossibleTickLargeCount = (axisLength / widthNumberPlusSpacing);
        double mzPerTick = axisRange / maxPossibleTickLargeCount;
        if (mzPerTick < steps[0]) {
            // if the spacing between ticks is less than the minimum step that we can show, 
            // it means we need to add digits after dot to avoid duplicate numbers 
            // being shown on the axis
            int exp = calcExponent(mzPerTick);
            int firstSignificantDigitAfterDot = -1 * exp;
            StringBuilder sb = new StringBuilder("0");
            if (firstSignificantDigitAfterDot > 0) {
                sb.append(".");
                for (int i = 0; i < firstSignificantDigitAfterDot; i++) {
                    sb.append("0");
                }
            }
            df = new DecimalFormat(sb.toString());

            // update our approximation of the number of ticks we have space for
            xHiStrFmt = df.format(xHi);
            strBox = calcPixelTextBoundingBox(g, xHiStrFmt);
            widthNumberPlusSpacing = strBox.width + pxNumberToNumber;
            maxPossibleTickLargeCount = (axisLength / widthNumberPlusSpacing);
            mzPerTick = axisRange / maxPossibleTickLargeCount;
        }



        // scale the predefined steps to the minimum reasonable size
        // before scaling them up in powers of 10
        int startExponent = calcExponent(mzPerTick) - 1; // -1 for safety, we go one level below what's required for scaling
        Double multiplier, step = null;
        boolean isStepFound = false;
        // 22 is the max power of ten exactly representable by a double
        // anyway we won't need to handle values larger than 10^22 :)))
        for (int i = startExponent; i < 22; i++) {
            multiplier = Math.pow(10, (double)i);
            for (int j = 0; j < steps.length; j++) {
                step = steps[j] * multiplier;
                if (axisRange / step < maxPossibleTickLargeCount) {
                    isStepFound = true;
                    break;
                }
            }
            if (isStepFound == true) {
                break;
            }
        }
        if (!isStepFound || step == null)
            throw new IllegalStateException("Could not calculate optimal step size for m/z (X) axis");
        return new TickStepInfo(df, step, maxPossibleTickLargeCount);
    }

    private TickStepInfo calcRtTickLargeStep(Graphics2D g, double axisRange, int axisLength) {
        double[] steps = yAxisSteps;

        // this is the largest number we might have on the axis
        Double yHi = mapDimensions.getRtHi();
        DecimalFormat df = new DecimalFormat("0");
        String yHiStrFmt = df.format(yHi);

        // find out how many digits after dot we need to show.
        // we start with showing no digits at all
        Rectangle strBox = calcPixelTextBoundingBox(g, yHiStrFmt);
        int heightNumberPlusSpacing = strBox.width + pxNumberToNumber;
        int maxPossibleTickLargeCount = (axisLength / heightNumberPlusSpacing);
        double rtPerTick = axisRange / maxPossibleTickLargeCount;
        if (rtPerTick < steps[0]) {
            // if the spacing between ticks is less than 1, it means we need to add
            // digits after dot to avoid duplicate numbers being shown on the axis
            int exp = calcExponent(rtPerTick);
            int firstSignificantDigitAfterDot = -1 * exp;
            StringBuilder sb = new StringBuilder("0");
            if (firstSignificantDigitAfterDot > 0){
                sb.append(".");
                for (int i = 0; i < firstSignificantDigitAfterDot; i++) {
                    sb.append("0");
                }
            }
            df = new DecimalFormat(sb.toString());

            // update our approximation of the number of ticks we have space for
            yHiStrFmt = df.format(yHi);
            strBox = calcPixelTextBoundingBox(g, yHiStrFmt);
            heightNumberPlusSpacing = strBox.width + pxNumberToNumber;
            maxPossibleTickLargeCount = (axisLength / heightNumberPlusSpacing);
            rtPerTick = axisRange / maxPossibleTickLargeCount;
        }



        // scale the predefined steps to the minimum reasonable size
        // before scaling them up in powers of 10
        int startExponent = calcExponent(rtPerTick) - 1; // -1 for safety, we go one level below what's required for scaling
        Double multiplier, step = null;
        boolean isStepFound = false;
        // 22 is the max power of ten exactly representable by a double
        // anyway we won't need to handle values larger than 10^22 :)))
        for (int i = startExponent; i < 22; i++) {
            multiplier = Math.pow(10, (double)i);
            for (int j = 0; j < steps.length; j++) {
                step = steps[j] * multiplier;
                if (axisRange / step < maxPossibleTickLargeCount) {
                    isStepFound = true;
                    break;
                }
            }
            if (isStepFound == true) {
                break;
            }
        }
        if (!isStepFound || step == null) {
            int a = 1;
            throw new IllegalStateException("Could not calculate optimal step size for RT (y) axis");
        }
        return new TickStepInfo(df, step, maxPossibleTickLargeCount);
    }

    /**
     * Formats the number in scientific notation so that only one digit before E
     * is shown and returns the exponent value (the number following E).<br/>
     * <b>Example 1:<b/> your number: 123.456, in scientific: 1E2, return value: 2
     * <b>Example 2:<b/> your number: 0.038, in scientific: 4E-2, return value: -2
     * @param value
     * @return the value of exponent when value is represented in scientific notation
     *  with only one leading significant digit (see example in description).
     * If the value is infinite or NaN, zero is returned.
     */
    private int calcExponent(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return 0;
        }
        DecimalFormat dfE = new DecimalFormat("0E0");
        String mzPerTickStrFmt = dfE.format(value);
        String expStr = mzPerTickStrFmt.substring( // this is the exponent of mzPerTick
                mzPerTickStrFmt.indexOf('E') + 1); // when mzPerTick represented in scientific notation
                                                   // as single digit plus exponent
        int startExponent = Integer.parseInt(expStr);
        return startExponent;
    }

    private final class TickStepInfo {
        DecimalFormat decimalFormat;
        double step;
        int numSteps;

        public TickStepInfo(DecimalFormat decimalFormat, double step, int numSteps) {
            this.decimalFormat = decimalFormat;
            this.step = step;
            this.numSteps = numSteps;
        }
    }

    /**
     * In swing Graphics.drawString(str, x, y) draws the string so that the
     * baseline of the first character is at [x, y]. This calculates the [x, y]
     * to draw the string so that it's center was at desired position.
     * @param axis axis type as defined by {@link Axis} enum in this class
     * @param strBox the precise bounding box of the text to be rendered.
     *        Use {@link #calcPreciseTextBoundingBox(java.awt.Graphics2D, java.lang.String) }
     *        to get it.
     * @param x the location of the end of a large tick
     * @param y the location of the end of a large tick
     * @return
     */
    private Point calcAxisNumberDrawingCoords(Axis axis, Rectangle strBox, int x, int y) {
        Point drawOrigin = new Point(x, y);

        if (axis == Axis.X) {
            int xOffset = -1 * (int)(strBox.getWidth()/2);
            int yOffset = pxNumberToTick + (int)strBox.getHeight();
            drawOrigin.translate(xOffset, yOffset);
        } else {
            int xOffset = -1 * (pxNumberToTick + (int)strBox.getWidth());
            int yOffset =      (int)(strBox.getHeight()/2);
            drawOrigin.translate(xOffset, yOffset);
        }
        return drawOrigin;
    }

    /**
     * This method allows to calculate the precise bounding box for the string at hand.<br/>
     * The FontMetrics method is inaccurate - it gives the general LINE bounding box
     * so if you have a string like ".,.,." it won't return a small value for the string's height, 
     * it will still count "," and "." as full height characters. This method uses
     * {@link TextLayout} to get the exact sizes of your strings.
     * @param g
     * @param lbl
     * @return 
     */
    private Rectangle2D calcPreciseTextBoundingBox(Graphics2D g, String lbl) {
        // The FontMetrics method is inaccurate - it gives the general LINE bounding box
        // so if you have a string like ".,.,." it won't return a small value for
        // height, it will still count them as full fledged characters
        //FontMetrics fm = g.getFontMetrics(fontAxis);
        //Rectangle2D strBox = fm.getStringBounds(lbl, g);

        TextLayout layout = new TextLayout(lbl, fontAxis, g.getFontRenderContext());
        return layout.getBounds();
    }

    /** this method allows to calculate the precise bounding box for the string at hand */
    private Rectangle calcPixelTextBoundingBox(Graphics2D g, String lbl) {
        TextLayout layout = new TextLayout(lbl, fontAxis, g.getFontRenderContext());
        return layout.getPixelBounds(null, 0, 0);
    }

    /**
     * Calculates width/height of the whole axis area. Works for both X and Y axes.
     * @param axisNumberLinearSize the linear dimension of the largest number on
     * the axis (height for X axis, width for Y axis).
     * @return
     */
    private int calcAxisSize(int axisNumberLinearSize) {
        return pxBorderToNumber + axisNumberLinearSize + pxNumberToTick + pxTickLarge + pxAxisLineWidth;
    }

    /**
     * Should only be called once in the constructor.
     * @return
     */
    private int calcYAxisWidth(TickStepInfo tickStepInfo) {
        Double yHi = mapDimensions.getRtHi();
        String yHiStrFmt = tickStepInfo.decimalFormat.format(yHi);

        Graphics2D g = getDummyGraphics();

        // largest number label width
        Rectangle yHiStrFmtBox = calcPixelTextBoundingBox(g, yHiStrFmt);
        int widthNumber = yHiStrFmtBox.width;

        // axis label width (rotated 90 degrees, so we're taking it's height)
        Rectangle lblBox = calcPixelTextBoundingBox(g, yAxisLabel);
        int widthLabel = lblBox.height;

        int widthNumberFullAxis = calcAxisSize(widthNumber);
        int widthLabelFullAxis  = calcAxisSize(widthLabel);
        g.dispose();
        return Math.max(widthNumberFullAxis, widthLabelFullAxis);
    }

    /**
     * Gets decimal formatter for Y axis. As our axis is in minutes, we always
     * return long format.<br/>
     * NOTICE:<br/>
     * TODO: if we later allow to change units (min/sec) this needs to be updated.
     * @return
     */
    private DecimalFormat getYAxisNumberFormatting() {
        return yAxisFormatLong;
    }

    /**
     * Gets decimal formatter for X axis. If the axis range is under 5Da,
     * it will print 2 decimal places.
     * @return
     */
    private DecimalFormat getXAxisNumberFormatting() {
        // if the range is under 5Da, we print 2 decimal places
        if (mapDimensions.getMzSpan() < xAxisFormatXLongThreshold)
            return xAxisFormatXLong;
        if (mapDimensions.getMzSpan() < xAxisFormatLongThreshold)
            return xAxisFormatLong;
        return xAxisFormatShort;
    }

    /**
     * Draw the created axis images on provided Graphics.
     * @param g
     */
    public void draw(Graphics2D g) {
        g.drawImage(yAxis,
                    0, 0,
                    yAxis.getWidth(), yAxis.getHeight(), null);
        g.drawImage(xAxis,
                    0, screenBounds.height - xAxis.getHeight(),
                    xAxis.getWidth(), xAxis.getHeight(), null);
//        throw new UnsupportedOperationException("need to implement drawing axes (only if it's turned on)");
    }

    /**
     * Calculates the rectangle inside Map2DPanel JFrame, where the BaseMap
     * can be rendered. It excludes axes' area.
     * @return
     */
    private Rectangle createMapReferenceFrame() {
        if (!doDrawAxes)
            return new Rectangle(screenBounds);

        Rectangle ref = new Rectangle();
        ref.setSize(screenBounds.width  - yAxis.getWidth(),
                    screenBounds.height - xAxis.getHeight());
        ref.setLocation(yAxis.getWidth(), 0);
        return ref;
    }

    /**
     * Does the [x,y] => [mz, rt] conversion for the current BaseMap2D at it's
     * visible state. E.g. the current BaseMap2D might be large, but the window
     * showing it might have been downsized. So both the BaseMap2D and it's
     * current view are important.
     *
     * @param x pixel X coordinate of a point in this JComponent.
     * @param y pixel Y coordinate of a point in this JComponent.
     * @param useLowX if these are X (row) pixels |-0-|-1-|-2-|, which boundary
     * for a given pixel to return left, or right
     * @param useLowY same as {@code useLowX}, but for columns of pixels (Y).
     * Don't forget, that in Y direction pixels are counted from top to bottom,
     * so LOW Y means "towards the top of the screen".
     * @return for returned point x=mz, y=rt
     */
    public MzRtPoint convertScreenCoordsToMzRt(int x, int y, boolean useLowX, boolean useLowY) {
        // translate mouse coords to map reference frame
        x = x - mapReferenceFrame.x;
        y = y - mapReferenceFrame.y;

        int curImgViewWidth = mapReferenceFrame.width;
        int curImgViewHeight = mapReferenceFrame.height;
        double mz, rt;
        if (useLowX) {
            mz = mapDimensions.getMzLo() + (mapDimensions.getMzSpan() / curImgViewWidth) * x;
        } else {
            mz = mapDimensions.getMzLo() + (mapDimensions.getMzSpan() / curImgViewWidth) * (x + 1);
        }
        if (useLowY) {
            rt = mapDimensions.getRtHi() - (mapDimensions.getRtSpan() / curImgViewHeight) * y;
        } else {
            rt = mapDimensions.getRtHi() - (mapDimensions.getRtSpan() / curImgViewHeight) * (y + 1);
        }
        return new MzRtPoint(mz, rt);
    }

    /**
     * Tries to convert screen pixel coordinates to exact m/z,rt by averaging
     * coordinates of pixel edges.
     *
     * @param x
     * @param y
     * @return
     */
    public MzRtPoint convertScreenCoordsToMzRt(int x, int y) {
        Point2D.Double leftTop = convertScreenCoordsToMzRt(x, y, true, true);
        Point2D.Double bottomRight = convertScreenCoordsToMzRt(x, y, false, false);
        return new MzRtPoint((leftTop.x + bottomRight.x) / 2.0d, (leftTop.y + bottomRight.y) / 2.0d);
    }

    /**
     * Convenience method to convert the Rectangle of the zoom box to mz and rt
     * intervals. Used by MouseAdapter when mouse is released after dragging.
     *
     * @param zoomBox
     * @return just a simple struct, holding mz and rt intervals
     */
    public MzRtRegion convertZoomBoxToMzRtRegion(Rectangle zoomBoxIn) {
        // translate zoombox to map reference frame coorinate system
//        Rectangle zoomBox = new Rectangle(zoomBoxIn);
//        zoomBox.translate(-1 * mapReferenceFrame.x, -1 * mapReferenceFrame.y);
        Rectangle2D intersection = zoomBoxIn.createIntersection(mapReferenceFrame);

        // Top Left Corner
        Point2D.Double tlc = convertScreenCoordsToMzRt(
                (int)intersection.getMinX(), (int)intersection.getMinY(), true, true);
        // Bottom Right Corner
        Point2D.Double brc = convertScreenCoordsToMzRt(
                (int)intersection.getMaxX(), (int)intersection.getMaxY(), false, false);

        MzRtRegion mzRtRegion = new MzRtRegion(tlc.x, brc.x, brc.y, tlc.y);
        return mzRtRegion;
    }

    public MzRtRegion getMapDimensions() {
        return mapDimensions;
    }

    public Rectangle getScreenBounds() {
        return screenBounds;
    }

    public Rectangle getMapReferenceFrame() {
        return mapReferenceFrame;
    }
}
