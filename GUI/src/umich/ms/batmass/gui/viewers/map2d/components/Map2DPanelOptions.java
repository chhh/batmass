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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import org.apache.commons.beanutils.BeanUtils;
import org.openide.util.Exceptions;
import umich.ms.util.DoubleRange;

/**
 * We do not implement {@link Cloneable} here, because of the property change support.
 * @author Dmitry Avtonomov
 */
public class Map2DPanelOptions {
//    protected Integer curMsLevel = null;
//    protected Interval1D<Double> curMzRange = null;
//    protected Boolean isDoDenoise = null;

    protected Integer msLevel;
    public static final String PROP_MSLEVEL = "msLevel";
    protected DoubleRange mzRange;
    public static final String PROP_MZRANGE = "mzRange";
    protected Boolean doDenoise;
    public static final String PROP_DODENOISE = "doDenoise";

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public Map2DPanelOptions() {
        this.msLevel = null;
        this.mzRange = null;
        this.doDenoise = false;
    }

    public Map2DPanelOptions(Integer msLevel, DoubleRange mzRange, Boolean doDenoise) {
        this.msLevel = msLevel;
        this.mzRange = mzRange;
        this.doDenoise = doDenoise;
    }

//    /**
//     * Copy constructor. Will not cope the change support.
//     * @param other
//     */
//    public Map2DPanelOptions(Map2DPanelOptions other) {
//        //this(other.msLevel, other.mzRange, other.doDenoise);
//    }
    /**
     * Like a copy-constructor. Copies the properties, property change support 
     * is initialized to an empty one.
     * @return 
     */
    public Map2DPanelOptions copy() {
        Map2DPanelOptions opts = new Map2DPanelOptions();
        try {
            BeanUtils.copyProperties(opts, this);
            
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        } 
       return opts;
    }

    /**
     * Get the value of msLevel
     *
     * @return the value of msLevel
     */
    public Integer getMsLevel() {
        return msLevel;
    }

    /**
     * Set the value of msLevel
     *
     * @param msLevel new value of msLevel
     */
    public void setMsLevel(Integer msLevel) {
        Integer oldMsLevel = this.msLevel;
        this.msLevel = msLevel;
    }

    /**
     * Get the value of mzRange
     *
     * @return the value of mzRange
     */
    public DoubleRange getMzRange() {
        return mzRange;
    }

    /**
     * Set the value of mzRange
     *
     * @param mzRange new value of mzRange
     */
    public void setMzRange(DoubleRange mzRange) {
        DoubleRange oldMzRange = this.mzRange;
        this.mzRange = mzRange;
    }

    /**
     * Get the value of isDoDenoise
     *
     * @return the value of isDoDenoise
     */
    public Boolean getDoDenoise() {
        return doDenoise;
    }

    /**
     * Set the value of isDoDenoise
     *
     * @param doDenoise new value of doDenoise
     */
    public void setDoDenoise(Boolean doDenoise) {
        Boolean oldIsDoDenoise = this.doDenoise;
        this.doDenoise = doDenoise;
        propertyChangeSupport.firePropertyChange(PROP_DODENOISE, oldIsDoDenoise, doDenoise);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.msLevel);
        hash = 11 * hash + Objects.hashCode(this.mzRange);
        hash = 11 * hash + Objects.hashCode(this.doDenoise);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Map2DPanelOptions other = (Map2DPanelOptions) obj;
        if (!Objects.equals(this.msLevel, other.msLevel)) {
            return false;
        }
        if (!Objects.equals(this.mzRange, other.mzRange)) {
            return false;
        }
        if (!Objects.equals(this.doDenoise, other.doDenoise)) {
            return false;
        }
        return true;
    }


}
