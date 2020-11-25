/*
 * Copyright 2020 chhh.
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
package umich.ms.batmass.gui.viewers.map2d.noise;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import umich.ms.batmass.gui.viewers.map2d.BasePassiveMap2DOverlay;
import umich.ms.batmass.gui.viewers.map2d.PassiveMap2DOverlayProvider;
import umich.ms.batmass.gui.viewers.map2d.PassiveOverlayKey;
import umich.ms.batmass.nbputils.OutputWndPrinter;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.datatypes.spectrum.impl.SpectrumDefault;
import umich.ms.fileio.exceptions.FileParsingException;
import umich.ms.util.Interval1D;
import umich.ms.util.IntervalST;
import umich.ms.util.SpectrumUtils;

/**
 *
 * @author chhh
 */
public class DenoiseLongEluting implements IAbMzRtTransform, PassiveMap2DOverlayProvider<DenoiseLongEluting.Data> {
    public static final String NAME = "LongEluting";
    public static final String CATEGORY = "Denoise";

    private final RTree<Data, Rectangle> rtree;
    private IntervalST<Double, Data> itree = new IntervalST<>();
    IScan configuredScan = null;
    
    public DenoiseLongEluting(RTree<Data, Rectangle> rtree) {
        this.rtree = rtree;
    }
    
    public static class TracingOpts {
        double mzTolPpm = 30;
        int maxGapLen = 15;
        int minPtsToAdd = 10;
    }

    public static DenoiseLongEluting from(NavigableMap<Integer, IScan> scansByRtSpanAtMsLevel) {
        OutputWndPrinter.printOut(CATEGORY, DenoiseLongEluting.class.getSimpleName() + 
                ": .from() called with scan map size="+ scansByRtSpanAtMsLevel.size());
        
        final TracingOpts opts = new TracingOpts();
        final Pool<Trace> pool = new Pool<>(() -> new Trace(10), Trace::reset);  
        final Function<Trace, Double> traceKeyExtractor = trace -> trace.mzAvgWeighted;
        
        final ConcurrentSkipListMap<Double, Trace> tracesAll = new ConcurrentSkipListMap<>();
        final ConcurrentLinkedDeque<Trace> tracesNew = new ConcurrentLinkedDeque<>();
        final ConcurrentLinkedDeque<Trace> tracesUpdated = new ConcurrentLinkedDeque<>();
        final ArrayList<Trace> tracesComplete = new ArrayList<>();
        
        
        
        final Iterator<IScan> itScans = scansByRtSpanAtMsLevel.values().iterator();
        final AtomicInteger scanCounter = new AtomicInteger(0);
        long timeLo = System.nanoTime();
        while (itScans.hasNext()) {
            
            // Get next scan
            final IScan scan = itScans.next();
            final int scanIndex = scanCounter.getAndIncrement();
            
            if (scanIndex % 1000 == 0) {
                OutputWndPrinter.printOut(CATEGORY, DenoiseLongEluting.class.getSimpleName()
                        + String.format("scan index=%d, currently tracking: tracesAll=%d, tracesComplete=%d",
                                scanIndex, tracesAll.size(), tracesComplete.size()));
            }

            // Get the spectrum, possibly centroiding
            ISpectrum spec;
            try {
                spec = scan.fetchSpectrum();
                if (!scan.isCentroided()) {
                    double[] mzsRaw = spec.getMZs();
                    double[] absRaw = spec.getIntensities();
                    List<Centroider.PeakMz> centroids = Centroider.detectPeaks(0, mzsRaw.length, mzsRaw, absRaw, 3, 0);
                    double[] mzs = new double[centroids.size()];
                    double[] abs = new double[centroids.size()];
                    for (int i = 0; i < centroids.size(); i++) {
                        mzs[i] = centroids.get(i).mz;
                        abs[i] = centroids.get(i).intensity;
                    }
                    spec = new SpectrumDefault(mzs, abs, null);
                }
            } catch (FileParsingException ex) {
                OutputWndPrinter.printErr(CATEGORY, DenoiseLongEluting.class.getSimpleName() 
                        + " error fetching spectrum for scan " + scan.toString() );
                continue;
            }
            
            createOrUpdateTraces(spec, scan, opts, pool, tracesAll, tracesNew);
            maintenance(scan, opts, traceKeyExtractor, tracesAll, tracesNew, tracesUpdated, tracesComplete);
        }
        long timeHi = System.nanoTime();
        if (true) {
            OutputWndPrinter.printOut(CATEGORY, DenoiseLongEluting.class.getSimpleName()
                    + String.format(": feature tracing took [%.4fs]", (timeHi - timeLo)/1e9d));
        }

        // in the end add all currently active traces to Completed list as well
        tracesComplete.addAll(tracesAll.values());
        tracesComplete.sort((o1, o2) -> Integer.compare(o2.ptr, o1.ptr));

        OutputWndPrinter.printErr(CATEGORY, DenoiseLongEluting.class.getSimpleName() 
                        + " RTree creation started for [" + tracesComplete.size() + "] traces" );
        
        RTree<Data, Rectangle> tree = createRtree(tracesComplete);
        return new DenoiseLongEluting(tree);
    }

    private static RTree<Data, Rectangle> createRtree(List<Trace> traces) {
//        if (true)
//            return RTree.star().create();
        
        OutputWndPrinter.printErr(CATEGORY, DenoiseLongEluting.class.getSimpleName() 
                        + " RTree creation started for [" + traces.size() + "] traces" );
        
        RTree<Data, Rectangle> tree = RTree.star().create();
        for (Trace t : traces) {
            double mzLo = Double.POSITIVE_INFINITY;
            double mzHi = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < t.size(); i++) {
                if (t.mzs[i] < mzLo) mzLo = t.mzs[i];
                if (t.mzs[i] > mzHi) mzHi = t.mzs[i];
            }
            double rtLo = t.rtLo;
            double rtHi = t.rtHi;
            
            double mzSpan = mzHi - mzLo;
            double rtSpan = rtHi - rtLo;
            if (mzSpan <= 0 || rtSpan <= 0) {
                continue;
            }
//            if (rtSpan < 5.0) {
//                continue;
//            }
            
            tree = tree.add(new Data(), Geometries.rectangle(mzLo, rtLo, mzHi, rtHi));
        }
        return tree;
    }
    
    private static void maintenance(final IScan scan, final TracingOpts opts, 
            final Function<Trace, Double> traceKeyExtractor,
            final ConcurrentSkipListMap<Double, Trace> tracesAll, 
            final ConcurrentLinkedDeque<Trace> tracesNew,
            final ConcurrentLinkedDeque<Trace> tracesUpdated,
            final ArrayList<Trace> tracesComplete) {
        // maintenance, done sequentially
        final int curScanNum = scan.getNum();
        
        // remove/update traces that did not get a match from this spectrum
        Iterator<Map.Entry<Double, Trace>> it = tracesAll.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Double, Trace> next = it.next();
            Trace t = next.getValue();
            
            if (curScanNum == t.scanNums[t.ptr]) { // was updated on this iteration
                t.zeroAbStretch = 0;
                it.remove();
                tracesUpdated.add(t);
                
            } else { // was not updated on this iteration
                t.zeroAbStretch += 1;
                if (t.zeroAbStretch > opts.maxGapLen) { // if long gap - complete trace
                    it.remove();
                    if (t.ptr + 1 >= opts.minPtsToAdd) {
                        tracesComplete.add(t);
                    } else { // trace didn't fit criteria, discard
                        //pool.surrender(t);
                    }
                }
            }
        }
        
        // reinsert new and updated traces back into the tree
        for (Trace t : tracesUpdated) {
            tracesAll.put(traceKeyExtractor.apply(t), t);
        }
        tracesUpdated.clear();
        for (Trace t : tracesNew) {
            tracesAll.put(traceKeyExtractor.apply(t), t);
        }
        tracesNew.clear();
    }

    /**
     * For each data point in spectrum try find an existing matching Trace.
     * If trace found - extend it with the new data point.
     * Otherwise create a new trace.
     * @param tracesAll Map of current traces
     * @param tracesNew 
     */
    private static void createOrUpdateTraces(final ISpectrum spec, final IScan scan, 
            final TracingOpts opts, final Pool<Trace> pool, ConcurrentSkipListMap<Double, Trace> tracesAll, 
            final ConcurrentLinkedDeque<Trace> tracesNew) {
        // Take each data point of the spectrum and update Traces
        for (int index = 0; index < spec.getMZs().length; index++) {
            double mz = spec.getMZs()[index];
            double ab = spec.getIntensities()[index];
            float rt = scan.getRt().floatValue();
            int scanNum = scan.getNum();
            if (ab <= 0) {
                continue;
            }
            double mzTol = SpectrumUtils.ppm2amu(mz, opts.mzTolPpm);
            final ConcurrentNavigableMap<Double, Trace> range = tracesAll.subMap(mz - mzTol, true, mz + mzTol, true);
            
            if (range.isEmpty()) { // no hits, create new trace
                Trace t = pool.borrow();
                t.add(mz, (float) ab, scanNum, rt);
                tracesNew.add(t);
                
            } else if (range.size() == 1) { // one hit, update an exising trace
                Trace t = range.firstEntry().getValue();
                t.add(mz, (float) ab, scanNum, rt);
                
            } else { // many possible traces match, select one with closest intensity for update
                Trace bestMatch = range.firstEntry().getValue();
                double bestDiff = Math.abs(ab - bestMatch.abs[bestMatch.ptr]);
                for (Map.Entry<Double, Trace> mz2trace : range.entrySet()) {
                    Trace t = mz2trace.getValue();
                    double diff = Math.abs(ab - t.abs[t.ptr]);
                    if (diff < bestDiff) {
                        bestDiff = diff;
                        bestMatch = t;
                    }
                }
                bestMatch.add(mz, (float) ab, scanNum, rt);
            }
        }
    }
    
    
    @Override
    public double apply(double mz, double ab) {
        if (configuredScan == null)
            return ab;
        boolean isHit = rtree.search(Geometries.point(mz, configuredScan.getRt()))
        .toBlocking().getIterator().hasNext();
        return isHit ? 0 : ab;
//        Interval1D<Double> search = itree.search(new DoubleInterval(mz, mz));
//        if (search == null)
//            return ab; // no-op for now
//        return 0;
    }

    @Override
    public void configure(IScan scan) {
        configuredScan = scan;
//        Double rt = scan.getRt();
//        Double mzHi = scan.getScanMzWindowUpper();
//        mzHi = mzHi == null ? 10000.0 : mzHi;
//        double eps = 1e-6;
//        Observable<Entry<Data, Rectangle>> search = rtree
//                .search(Geometries.rectangle(0, rt-eps, mzHi, rt+eps));
//        search.toList().subscribeOn(Schedulers.immediate()).subscribe((list) -> {
//            DenoiseLongEluting.this.itree.clear();
//            for (Entry<Data, Rectangle> e : list) {
//                double mz1 = e.geometry().x1();
//                double mz2 = e.geometry().x2();
//                itree.put(new DoubleInterval(mz1, mz2), e.value());
//            }
//        });
        
    }

    @Override
    public RTree<DenoiseLongEluting.Data, Rectangle> getIndex() {
        return rtree;
    }

    @Override
    public PassiveOverlayKey getKey() {
        return new PassiveOverlayKey(NAME, CATEGORY);
    }

    @Override
    public Iterator<DenoiseLongEluting.Data> iterator() {
        return rtree.entries().asObservable().map(t -> t.value()).toBlocking().getIterator();
    }

    public static class Data extends BasePassiveMap2DOverlay {

        public Data() {
        }

        @Override
        public Color getFillColor() {
            return Color.MAGENTA;
        }

        @Override
        public float getFillAlpha() {
            return 0.6f;
        }

    }
    
    public static class DoubleInterval extends Interval1D<Double> {

        public DoubleInterval(Double left, Double right) {
            super(left, right);
        }
    }
}
