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
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import umich.ms.batmass.gui.viewers.map2d.BasePassiveMap2DOverlay;
import umich.ms.batmass.gui.viewers.map2d.PassiveMap2DOverlayProvider;
import umich.ms.batmass.gui.viewers.map2d.PassiveOverlayKey;
import umich.ms.batmass.gui.viewers.map2d.noise.DenoiseMexHat.Data;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.exceptions.FileParsingException;


public class DenoiseMexHat implements IAbMzRtTransform, PassiveMap2DOverlayProvider<Data> {
    public static final String NAME = "MexHat";
    public static final String CATEGORY = "Denoise";
    private final RTree<Data, Rectangle> rtree;

    public DenoiseMexHat(RTree<Data, Rectangle> rtree) {
        this.rtree = rtree;
    }
    
    public static DenoiseMexHat from(NavigableMap<Integer, IScan> scansByRtSpanAtMsLevel) {
        RTree<Data, Rectangle> tree = RTree.star().create();
        
        for (Map.Entry<Integer, IScan> e : scansByRtSpanAtMsLevel.entrySet()) {
            Integer scanNum = e.getKey();
            IScan scan = e.getValue();
            
            ISpectrum spec;
            try {
                spec = scan.fetchSpectrum();
            } catch (FileParsingException ex) {
                continue; // bad, but this is just exploratory code
            }
        }
        
        tree = tree.add(new Data(), Geometries.rectangle(600, 40, 650, 70));
        
        return new DenoiseMexHat(tree);
    }
    
    @Override
    public double apply(double mz, double ab) {
        return ab;
    }

    @Override
    public void configure(IScan scan) {
        
    }

    @Override
    public RTree<Data, Rectangle> getIndex() {
        return rtree;
    }

    @Override
    public Iterator<Data> iterator() {
        return rtree.entries().asObservable().map(t -> t.value()).toBlocking().getIterator();
    }

    @Override
    public PassiveOverlayKey getKey() {
        return new PassiveOverlayKey(NAME, CATEGORY);
    }
    
    
    public static class Data extends BasePassiveMap2DOverlay {
        public long id;

        @Override
        public Color getFillColor() {
            return Color.RED;
        }

        @Override
        public float getFillAlpha() {
            return 0.7f;
        }

    }
    
}
