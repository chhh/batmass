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
package umich.ms.batmass.gui.core.components.util.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2009/06/22 09:10:18 $
 */

/**
 * This interface describes the behaviour for a spectrum file
 * (ie., PKL file, Mascot Generic file, dat file, ...).
 *
 * @author Lennart Martens
 * @version $Id: SpectrumFile.java,v 1.2 2009/06/22 09:10:18 lennart Exp $
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface SpectrumFile extends Comparable {

    /**
     * This method reports on the charge of the precursor ion.
     * Note that when the charge could not be determined, this
     * method will return '0'.
     *
     * @return  int with the charge of the precursor, or '0'
     *              if no charge state is known.
     */
    int getCharge();

    /**
     * This method sets the charge of the precursor ion. When the charge is not known,
     * it should be set to '0'.
     *
     * @param aCharge   int with the charge of the precursor ion.
     */
    void setCharge(int aCharge);

    /**
     * This method reports on the filename for the file.
     *
     * @return  String with the filename for the file.
     */
    String getFilename();

    /**
     * This method sets the filename for the file.
     *
     * @param aFilename String with the filename for the file.
     */
    void setFilename(String aFilename);

    /**
     * This method reports on the peaks in the spectrum, with the
     * Doubles for the masses as keys in the HashMap, and the intensities
     * for each peak as Double value for that mass key.
     *
     * @return  HashMap with Doubles as keys (the masses) and Doubles as values (the intensities).
     */
    HashMap getPeaks();

    /**
     * This method sets the peaks on the spectrum.
     * Doubles for the masses as keys in the HashMap, and the intensities
     * for each peak as Double value for that mass key.
     *
     * @param aPeaks HashMap with Doubles as keys (the masses) and Doubles as values (the intensities).
     */
    void setPeaks(HashMap aPeaks);

    /**
     * This method reports on the precursor M/Z
     *
     * @return  double with the precursor M/Z
     */
    double getPrecursorMZ();

    /**
     * This method sets the precursor M/Z on the file.
     *
     * @param aPrecursorMZ  double with the precursor M/Z
     */
    void setPrecursorMZ(double aPrecursorMZ);

    /**
     * This method reports on the intensity of the precursor ion.
     *
     * @return  double with the intensity of the precursor ion.
     */
    double getIntensity();

    /**
     * This method sets the intensity of the precursor ion.
     *
     * @param aIntensity double with the intensity of the precursor ion.
     */
    void setIntensity(double aIntensity);

    /**
     * This method returns the total intensity contributed by all the ions
     * in this spectrum.
     *
     * @return  double with the total intensity.
     */
    double getTotalIntensity();

    /**
     * This method returns the intensity of the highest intensity peak in
     * this spectrum.
     *
     * @return  double with the intensity of the highest intensity peak
     *          in this spectrum. 
     */
    double getHighestIntensity();

    /**
     * This method allows to write the spectrum file to the specified OutputStream.
     *
     * @param   aOut    OutputStream to write the file to. This Stream
     *                  will <b>NOT</b> be closed by this method.
     * @exception   IOException when the write operation fails.
     */
    void writeToStream(OutputStream aOut) throws IOException;

    /**
     * This method allows the caller to write the spectrum file to the specified folder
     * using its current filename.
     *
     * @param   aParentDir  File with the parent directory to put the file in.
     * @exception   java.io.IOException whenever the write process failed.
     */
    void writeToFile(File aParentDir) throws IOException;
}
