/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.filesupport.core.types.descriptor;

/**
 * Use if a Descriptor deserialization can't be done.
 * @author dmitriya
 */
public class CorruptDescriptorException extends Exception {

    public CorruptDescriptorException(String message) {
        super(message);
    }

    public CorruptDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorruptDescriptorException(Throwable cause) {
        super(cause);
    }

}
