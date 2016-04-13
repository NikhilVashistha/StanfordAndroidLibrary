package stanford.androidlib;

/**
 * This exception class is a thin wrapper around Java's I/O exception classes
 * like FileNotFoundException and IOException.
 * We use it to throw runtime exceptions when I/O errors occur.
 * This allows the client to optionally catch them or let the program crash.
 */
public class IORuntimeException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public IORuntimeException() {
        super();
    }

    public IORuntimeException(Throwable cause) {
        super(cause);
    }

    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
