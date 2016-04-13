package stanford.androidlib;

/**
 * This exception class is a thin wrapper around Java's reflection exception classes
 * like ClassNotFoundException and NoSuchMethodException.
 * We use it to throw runtime exceptions when reflection errors occur.
 * This allows the client to optionally catch them or let the program crash.
 */
public class ReflectionRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public ReflectionRuntimeException() {
        super();
    }

    public ReflectionRuntimeException(Throwable cause) {
        super(cause);
    }

    public ReflectionRuntimeException(String message) {
        super(message);
    }

    public ReflectionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
