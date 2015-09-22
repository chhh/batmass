/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.files.types.agilent.cef.model;

import java.util.ArrayList;
import java.util.List;

/**
 * All compounds from an Agilent .cef file.
 * @author Dmitry Avtonomov
 */
public class AgilentCompounds {
    List<AgilentCompound> compounds;

    public AgilentCompounds() {
        compounds = new ArrayList<>();
    }

    public AgilentCompounds(int startSize) {
        compounds = new ArrayList<>(startSize);
    }

    public List<AgilentCompound> getCompounds() {
        return compounds;
    }

    public int size() {
        return compounds.size();
    }

    public boolean isEmpty() {
        return compounds.isEmpty();
    }

    public boolean add(AgilentCompound e) {
        return compounds.add(e);
    }
}
