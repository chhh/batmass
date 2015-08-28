/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.data.core.api;

/**
 * Thrown when a {@link DataSource} can't load its data.
 * @author Dmitry Avtonomov
 */
public class DataLoadingException extends Exception {

    public DataLoadingException() {
    }

    public DataLoadingException(String message) {
        super(message);
    }

    public DataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLoadingException(Throwable cause) {
        super(cause);
    }

    public DataLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    
}
