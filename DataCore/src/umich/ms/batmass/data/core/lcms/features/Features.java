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
package umich.ms.batmass.data.core.lcms.features;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.api.annotations.common.NonNull;
import rx.Observable;
import umich.ms.batmass.data.core.lcms.features.api.FeatureUtils;
import umich.ms.util.DoubleRange;
import umich.ms.util.Interval1D;
import umich.ms.util.IntervalST;

/**
 * Storage data structure for 2D features, with support for quick intersection
 * queries.
 * @author Dmitry Avtonomov
 * @param <T>
 */
public class Features<T extends ILCMSFeature2D<?>> {

    protected Storage ms1;
    protected IntervalST<Double, Storage> ms2;
    public static final double MIN_SEARCH_RANGE_OVERLAP = 0.95;

    public Features() {
        
    }

    
    /**
     * This method is not thread safe, only add items to the structure from a single
     * thread.
     * @param f feature to add
     * @param msLevel MS Level at which the feature is located
     * @param precursorRange ignored for MS1 features, for MS2 must be non-null
     */
    public void add(T f, int msLevel, DoubleRange precursorRange) {
        if (msLevel > 1 && precursorRange == null) {
            throw new IllegalArgumentException("If MS Level is >1, you must provide a non-null"
                    + " precursor range.");
        }

        switch (msLevel) {
            case 1:
                if (ms1 == null) {
                    ms1 = new Storage(true);
                }
                ms1.add(f);
                break;

            case 2:
                if (ms2 == null) {
                    ms2 = new IntervalST<>();
                }
                IntervalST.Node<Double, Storage> node = ms2.get(precursorRange);
                Storage storage;
                if (node == null) {
                    // we don't have a storage for that MS2 interval yet
                    storage = new Storage(true);
                    ms2.put(precursorRange, storage);
                } else {
                    storage = node.getValue();
                }
                storage.add(f);
                break;

            default:
                throw new IllegalArgumentException(String.format("Only MS1 and MS2 "
                        + "levels are supported, [%d] given.", msLevel));
        }
    }

    /**
     * This method is not thread safe, only add items to the structure from a single
     * thread.
     * @param features to be added to this storage. If nothing has been added to this instance before,
     * then the size of this collection will determine the underlying R-Tree heuristics to be used.
     * @param msLevel MS Level at which the feature is located
     * @param precursorRange ignored for MS1 features, for MS2 must be non-null
     */
    public void addAll(Collection<? extends T> features, int msLevel, DoubleRange precursorRange) {
        if (msLevel > 1 && precursorRange == null) {
            throw new IllegalArgumentException("If MS Level is >1, you must provide a non-null"
                    + " precursor range.");
        }

        switch (msLevel) {
            case 1:
                if (ms1 == null) {
                    ms1 = new Storage(features.size());
                }
                ms1.addAll(features);
                break;

            case 2:
                if (ms2 == null) {
                    ms2 = new IntervalST<>();
                }
                IntervalST.Node<Double, Storage> node = ms2.get(precursorRange);
                Storage storage;
                if (node == null) {
                    // we don't have a storage for that MS2 interval yet
                    storage = new Storage(features.size());
                    ms2.put(precursorRange, storage);
                } else {
                    storage = node.getValue();
                }
                storage.addAll(features);
                break;

            default:
                throw new IllegalArgumentException(String.format("Only MS1 and MS2 "
                        + "levels are supported, [%d] given.", msLevel));
        }
    }

    /**
     * Do not modify the storage. Use {@link #add(umich.ms.batmass.data.core.lcms.features.ILCMSFeature2D, int, umich.ms.util.DoubleRange) } and
     * {@link #addAll(java.util.Collection, int, umich.ms.util.DoubleRange) } methods of this class instead.
     * @return
     */
    public Storage getMs1() {
        return ms1;
    }

    /**
     * Do not modify the tree yourself
     * @return
     */
    public IntervalST<Double, Storage> getMs2() {
        return ms2;
    }

    /**
     * Get the MS2 storage of features for a particular precursor range.
     * @param rng non-null. the range to look for. Does not have to be exact, 95% overlap
     * between the range provided and the assigned range of storage is required
     * for the storage to be retrieved.
     * @return the storage with the best overlapping precursor range, relative
     * overlap will be above {@link #MIN_SEARCH_RANGE_OVERLAP}.
     */
    public Storage getMs2(@NonNull DoubleRange rng) {
        List<IntervalST.Node<Double, Storage>> nodes = ms2.searchAll(rng);

        switch (nodes.size()) {
            case 0:
                break;

            case 1:
                IntervalST.Node<Double, Storage> node = nodes.get(0);
                Interval1D<Double> interval = node.getInterval();
                DoubleRange rangeFound = DoubleRange.fromInterval1D(interval);
                if (rng.overlapRelative(rangeFound) >= MIN_SEARCH_RANGE_OVERLAP) {
                    return node.getValue();
                }
                break;

            default:
                double maxOverlap = Double.NEGATIVE_INFINITY;
                Storage bestStorage = null;
                for (IntervalST.Node<Double, Storage> n : nodes) {
                    rangeFound = DoubleRange.fromInterval1D(n.getInterval());
                    double overlap = rng.overlapRelative(rangeFound);

                    if (overlap > maxOverlap) {
                        maxOverlap = overlap;
                        bestStorage = n.getValue();
                    }
                }
                if (bestStorage != null) {
                    return bestStorage;
                }
                break;
        }

        return null;

        
    }



    public class Storage {
        /** Features as a list for sequential traversal. */
        protected List<T> list;
        /** R-Tree for fast search of intersections. */
        protected RTree<T, Rectangle> tree;
        public static final int RTREE_STAR_CAPACITY_THRESHOLD = 10000;

        /**
         * @param initCapacity the initial capacity for the internal storages
         */
        public Storage(int initCapacity) {
            list = new ArrayList<>(initCapacity);
            if (initCapacity > RTREE_STAR_CAPACITY_THRESHOLD) {
                tree = RTree.star().create();
            } else {
                tree = RTree.create();
            }
        }

        /**
         * 
         * @param useRStarTree if true, initial capacity will be set to {@link #RTREE_STAR_CAPACITY_THRESHOLD}
         * and R*-Tree will be used. Otherwise a quadratic splitter in the tree is used and the capacity is set
         * to 1000.
         */
        public Storage(boolean useRStarTree) {
            if (!useRStarTree) {
                list = new ArrayList<>(1000);
                tree = RTree.create();
            } else {
                list = new ArrayList<>(RTREE_STAR_CAPACITY_THRESHOLD);
                tree = RTree.star().create();
            }
        }

        public List<T> getList() {
            return list;
        }

        public RTree<T, Rectangle> getTree() {
            return tree;
        }

        /**
         *
         * @param f the feature to be added
         */
        public void add(T f) {
            list.add(f);
            Rectangle2D bbox = f.getBounds();
            Rectangle rectangle = FeatureUtils.geometryAwtToRtree(bbox);
            tree = tree.add(f, rectangle);
        }

        public void addAll(Collection<? extends T> features) {
            list.addAll(features);
            for (T f : features) {
                Rectangle2D bbox = f.getBounds();
                Rectangle rectangle = FeatureUtils.geometryAwtToRtree(bbox);
                tree = tree.add(f, rectangle);
            }
        }

        /**
         * Queries the storage for all features intersecting the given rectangle.
         * @param r can be a point, in case you're looking for features under mouse cursor.
         * @return
         */
        public Iterable<Entry<T, Rectangle>> query(Rectangle2D r) {
            Rectangle query = FeatureUtils.geometryAwtToRtree(r);
            Observable<Entry<T, Rectangle>> result = tree.search(query);
            return result.toBlocking().toIterable();
        }

        /**
         * Queries the storage for all features intersecting the given point.
         * @param p
         * @return
         */
        public Iterable<Entry<T, Rectangle>> query(Point2D p) {
            Point query = FeatureUtils.geometryAwtToRtree(p);
            Observable<Entry<T, Rectangle>> result = tree.search(query);
            return result.toBlocking().toIterable();
        }
    }


}
