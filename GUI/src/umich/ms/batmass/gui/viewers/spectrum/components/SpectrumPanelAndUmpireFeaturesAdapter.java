/*
 * License placeholder
 */

package umich.ms.batmass.gui.viewers.spectrum.components;

import MSUmpire.BaseDataStructure.XYData;
import MSUmpire.BaseDataStructure.XYPointCollection;
import MSUmpire.PeakDataStructure.PeakCluster;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.util.Exceptions;
import umich.ms.batmass.gui.core.components.spectrum.SpectrumPanel;
import umich.ms.batmass.gui.viewers.spectrum.todelete.PeakClusterContainer;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scan.props.PrecursorInfo;
import umich.ms.datatypes.scancollection.IScanCollection;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.exceptions.FileParsingException;
import umich.ms.util.IntervalST;

/**
 *
 * @author dmitriya
 */
public class SpectrumPanelAndUmpireFeaturesAdapter extends SpectrumPanelAdapter {

    IntervalST st;
    ArrayList<PeakClusterContainer> clusters;
    private static final boolean doSubtractionOfFeatures = false;

    public SpectrumPanelAndUmpireFeaturesAdapter(IScanCollection scans) {
        super(scans);
    }

    public void setSt(IntervalST st) {
        this.st = st;
    }

    public void setClusters(ArrayList<PeakClusterContainer> clusters) {
        this.clusters = clusters;
    }



    @Override
    @SuppressWarnings("unchecked")
    public SpectrumPanel buildPanelForScanNum(int scanNum) {
        IScan scan = scans.getScanByNum(scanNum);
        if (scan == null) {
            return null;
        }


        ISpectrum spectrum = null;
        try {
            spectrum = scan.fetchSpectrum();
        } catch (FileParsingException ex) {
            Exceptions.printStackTrace(ex);
        }

        double[] xValues = spectrum.getMZs();
        double[] yValues = spectrum.getIntensities();

        boolean profileMode = !scan.isCentroided();
        int msLevel = scan.getMsLevel();
        int maxPadding = 50;
        PrecursorInfo precursor = scan.getPrecursor();
        String charge = "-";
        double precursorMz = 0.0d;
        if (precursor != null) {
            if (precursor.getMzRangeStart() != null) {
                precursorMz = precursor.getMzRangeStart();
            } else if (precursor.getMzRangeEnd() != null) {
                precursorMz = precursor.getMzRangeEnd();
            }
        }



        List<PeakCluster> pcs = new ArrayList<>();
        for (PeakClusterContainer pcc : clusters) {
            if (scanNum >= pcc.startScanNum && scanNum <= pcc.endScanNum) {
                pcs.add(pcc.peakCluster);
            }
        }

        // This "if block" deals with subtracting features from original spectra
        // it's not so easy, because we don't have a direct correspondence of feature points
        // to points in spectra, so we have to guess a lot. Because of that
        // and because of interpolation from smoothed peak-curves we're not always
        // able to subtract the full original peak from the spectrum, even if
        // we guessed m/z correctly.
        if (doSubtractionOfFeatures && pcs != null && !pcs.isEmpty()) {
            for (int i = 0; i < pcs.size(); i++) {
                PeakCluster pc = pcs.get(i);
                for (int j = 0; j < pc.mz.length; j++) {
                    double pcMz = pc.mz[j];
                    if (pc.mz[j] <= 0.0f)
                        break;

                    float a, b;
                    XYData loXYPoint, hiXYPoint;
                    XYPointCollection smoothedList;

                    smoothedList = pc.IsoPeaksCurves[j].GetSmoothedList();
                    loXYPoint = smoothedList.GetPoinByXLower(scan.getRt().floatValue());
                    hiXYPoint = smoothedList.GetPoinByXHigher(scan.getRt().floatValue());


                    a = (loXYPoint.getY() - hiXYPoint.getY()) / (loXYPoint.getX() - hiXYPoint.getX());
                    b = loXYPoint.getY() - a * loXYPoint.getX();
                    double avgIntensityInterpolatedFromSmoothedData = a * scan.getRt().floatValue() + b;


                    int binarySearchResult = Arrays.binarySearch(xValues, pcMz);
                    if (binarySearchResult < 0) {
                        int insertionPoint = - 1 - binarySearchResult;
                        if (insertionPoint == 0) {
                            yValues[0] -= avgIntensityInterpolatedFromSmoothedData;
                            if (yValues[0] < 0d) yValues[0] = 0d;
                        } else if (insertionPoint >= yValues.length - 1) {
                            yValues[yValues.length - 1] -= avgIntensityInterpolatedFromSmoothedData;
                            if (yValues[yValues.length - 1] < 0d) yValues[yValues.length - 1] = 0d;
                        } else {
                            double dMzLeft = Math.abs(xValues[insertionPoint-1] - pcMz);
                            double dMzCenter = Math.abs(xValues[insertionPoint] - pcMz);
                            double dMzRight = Math.abs(xValues[insertionPoint+1] - pcMz);


                            double dIntLeft = Math.abs(yValues[insertionPoint-1] - avgIntensityInterpolatedFromSmoothedData);
                            double dIntCenter = Math.abs(yValues[insertionPoint] - avgIntensityInterpolatedFromSmoothedData);
                            double dIntRight = Math.abs(yValues[insertionPoint+1] - avgIntensityInterpolatedFromSmoothedData);
                            double min = Math.min(Math.min(dIntCenter, dIntLeft), dIntRight);

                            if (dIntLeft < dIntRight && dIntLeft < dIntCenter) {
                                yValues[insertionPoint-1] -= avgIntensityInterpolatedFromSmoothedData;
                                if (yValues[insertionPoint-1] < 0d) yValues[insertionPoint-1] = 0d;

                            } else if (dIntRight < dIntLeft && dIntRight < dIntCenter) {
                                yValues[insertionPoint+1] -= avgIntensityInterpolatedFromSmoothedData;
                                if (yValues[insertionPoint+1] < 0d) yValues[insertionPoint+1] = 0d;
                            } else {
                                yValues[insertionPoint] -= avgIntensityInterpolatedFromSmoothedData;
                                if (yValues[insertionPoint] < 0d) yValues[insertionPoint] = 0d;
                            }
                        }
                    } else {
                        yValues[binarySearchResult] -= avgIntensityInterpolatedFromSmoothedData;
                    }
                }
            }
        }



        SpectrumPanel newSpectrumPanel = new SpectrumPanel(
                xValues,
                yValues,
                precursorMz,
                charge,
                String.format("Scan #%d(%.2fm): MS%d m/z[%d-%d]", scan.getNum(), scan.getRt(), scan.getMsLevel(),
                    (int)Math.round(scan.getScanMzWindowLower()), (int)Math.round(scan.getScanMzWindowUpper())),
                maxPadding,
                false, // show filename
                false, false,

                msLevel,
                profileMode);
        newSpectrumPanel.setXAxisStartAtZero(false);

//        // looking for peak clusters for this scan (IntervalST version)
//        List<PeakCluster> pcs = null;
//        for (Object x : st.searchAll(new Interval1D(scanNum, scanNum))) {
//            pcs = (List<PeakCluster>)((IntervalST.Node)x).getValue();
//        }
//
//        if (pcs != null && !pcs.isEmpty()) {
//            ArrayList<Double> mirroredSpectrumMz = new ArrayList<>();
//            ArrayList<Double> mirroredSpectrumInt = new ArrayList<>();
//            for (PeakCluster pc : pcs) {
//                for (int i=0; i < pc.mz.length; i++) {
//                    if (pc.mz[i] <= 0.0f)
//                        break;
//                    mirroredSpectrumMz.add((double)pc.mz[i]);
//                    mirroredSpectrumInt.add((double)pc.PeakHeight[i]);
//                }
//            }
//            double[] mirroredSpectrumMzArr = new double[mirroredSpectrumMz.size()];
//            double[] mirroredSpectrumIntArr = new double[mirroredSpectrumInt.size()];
//            for (int i = 0; i < mirroredSpectrumIntArr.length; i++) {
//                mirroredSpectrumMzArr[i] = mirroredSpectrumMz.get(i);
//                mirroredSpectrumIntArr[i] = mirroredSpectrumInt.get(i);
//            }
//
//             newSpectrumPanel.addMirroredSpectrum(
//                mirroredSpectrumMzArr, mirroredSpectrumIntArr,
//                0.0,
//                "neutral",
//                "umpire features",
//                false,
//                Color.BLUE, Color.BLUE);
//        }


        // looking for peak clusters for this scan (just an array of clusters)
        // multiple colors for clusters

        Color color;
        double[] mirroredSpectrumMzArrPrev = new double[0];
        double avgIntensityInterpolatedFromSmoothedData;
        float a, b;
        XYData loXYPoint, hiXYPoint;
        XYPointCollection smoothedList;

        if (pcs != null && !pcs.isEmpty()) {
            for (int i = 0; i < pcs.size(); i++) {
                PeakCluster pc = pcs.get(i);
                ArrayList<Double> mirroredSpectrumMz = new ArrayList<>();
                ArrayList<Double> mirroredSpectrumInt = new ArrayList<>();


                for (int j=0; j < pc.mz.length; j++) {
                    if (pc.mz[j] <= 0.0f)
                        break;
                    smoothedList = pc.IsoPeaksCurves[j].GetSmoothedList();
                    loXYPoint = smoothedList.GetPoinByXLower(scan.getRt().floatValue());
                    hiXYPoint = smoothedList.GetPoinByXHigher(scan.getRt().floatValue());

                    a = (loXYPoint.getY() - hiXYPoint.getY()) / (loXYPoint.getX() - hiXYPoint.getX());
                    b = loXYPoint.getY() - a * loXYPoint.getX();
                    avgIntensityInterpolatedFromSmoothedData = a * scan.getRt().floatValue() + b;

                    mirroredSpectrumMz.add((double)pc.mz[j]);
//                    mirroredSpectrumInt.add((double)pc.PeakHeight[j]);
                    mirroredSpectrumInt.add(avgIntensityInterpolatedFromSmoothedData);
                }

                double[] mirroredSpectrumMzArr = new double[mirroredSpectrumMz.size()];
                double[] mirroredSpectrumIntArr = new double[mirroredSpectrumInt.size()];
                for (int j = 0; j < mirroredSpectrumIntArr.length; j++) {
                    mirroredSpectrumMzArr[j] = mirroredSpectrumMz.get(j);
                    for (int k = 0; k < mirroredSpectrumMzArrPrev.length; k++) {
                        if (Math.abs(mirroredSpectrumMzArr[j] - mirroredSpectrumMzArrPrev[k]) < 0.01) {
                            mirroredSpectrumMzArr[j] += 0.01;
                            break;
                        }
                    }
                    mirroredSpectrumIntArr[j] = mirroredSpectrumInt.get(j);
                }
                mirroredSpectrumMzArrPrev = mirroredSpectrumMzArr;

                color = ColorWheel.getColorForInt(pc.Index);
                if (i == 0) {
                    newSpectrumPanel.addMirroredSpectrum(
                    mirroredSpectrumMzArr, mirroredSpectrumIntArr,
                    0.0,
                    "neutral",
                    "umpire features",
                    false,
                    color, color);
                } else {
                    newSpectrumPanel.addAdditionalMirroredDataset(mirroredSpectrumMzArr, mirroredSpectrumIntArr, color, color);
                }
            }
        }



        return newSpectrumPanel;
    }


    public static class ColorWheel {
        public static Color color = Color.BLUE;

        public static Color[] colors5 = new Color[9];
        static {
            colors5[0] = Color.BLUE;
            colors5[1] = Color.ORANGE;
            colors5[2] = Color.RED;
            colors5[3] = Color.GREEN;
            colors5[4] = Color.CYAN;
            colors5[5] = Color.darkGray;
            colors5[6] = Color.pink;
            colors5[7] = Color.MAGENTA;
            colors5[8] = Color.YELLOW;
        }

        public static Color getColorForInt(int colorIndex) {
            return colors5[colorIndex % 9];
        }
    }


}
