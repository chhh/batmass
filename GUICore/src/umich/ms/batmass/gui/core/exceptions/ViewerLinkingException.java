/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.gui.core.exceptions;

/**
 *
 * @author Dmitry Avtonomov
 */
public class ViewerLinkingException extends Exception {

    public ViewerLinkingException() {
    }

    public ViewerLinkingException(String message) {
        super(message);
    }

    public ViewerLinkingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViewerLinkingException(Throwable cause) {
        super(cause);
    }

    public ViewerLinkingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    

}
