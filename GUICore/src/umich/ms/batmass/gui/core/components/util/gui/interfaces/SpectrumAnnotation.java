/* 
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
package umich.ms.batmass.gui.core.components.util.gui.interfaces;

import java.awt.Color;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2007/10/22 10:09:02 $
 */

/**
 * This interface describes the behaviour for a spectrum annotation.
 * 
 * @author Lennart Martens
 * @version $Id: SpectrumAnnotation.java,v 1.1 2007/10/22 10:09:02 lennart Exp $
 */
public interface SpectrumAnnotation {

    /**
     * This method returns the M/Z of the feature to annotate.
     *
     * @return  double with the M/Z.
     */
    public abstract double getMZ();

    /**
     * This method returns the allowed error margin (both sides)
     * for the M/Z of the annotation (eg., 0.1 means an allowed
     * interval of [M/Z-0.1, M/Z+0.1].
     *
     * @return  double  with the error margin.
     */
    public abstract double getErrorMargin();

    /**
     * This method returns the color for the annotation.
     *
     * @return  Color with the color for the annotation.
     */
    public abstract Color getColor();

    /**
     * This method returns the label for the annotation.
     *
     * @return  String with the label to display (above the M/Z)
     *                  for this annotation.
     */
    public abstract String getLabel();
}
