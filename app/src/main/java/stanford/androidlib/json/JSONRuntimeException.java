package stanford.androidlib.json;

/**
 * RuntimeException thrown when JSON data is invalid.
 */
public class JSONRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public JSONRuntimeException() {
        super();
    }

    public JSONRuntimeException(Throwable cause) {
        super(cause);
    }

    public JSONRuntimeException(String message) {
        super(message);
    }

    public JSONRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

