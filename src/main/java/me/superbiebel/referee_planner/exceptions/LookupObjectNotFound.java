package me.superbiebel.referee_planner.exceptions;

public class LookupObjectNotFound extends RuntimeException {
    public LookupObjectNotFound() {
    }
    
    public LookupObjectNotFound(String message) {
        super(message);
    }
    
    public LookupObjectNotFound(String message, Throwable cause) {
        super(message, cause);
    }
    
    public LookupObjectNotFound(Throwable cause) {
        super(cause);
    }
    
    public LookupObjectNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
