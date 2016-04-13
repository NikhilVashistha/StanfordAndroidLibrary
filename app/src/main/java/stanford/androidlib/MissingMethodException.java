package stanford.androidlib;

/**
 * An exception thrown when a SimpleActivity subclass does not implement
 * a method that it ought to implement.
 */
public class MissingMethodException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public MissingMethodException() {
        super();
    }

    public MissingMethodException(Throwable cause) {
        super(cause);
    }

    public MissingMethodException(String message) {
        super(message);
    }

    public MissingMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
