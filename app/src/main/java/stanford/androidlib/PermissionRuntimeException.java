package stanford.androidlib;

/**
 * An exception thrown when a SimpleActivity subclass does not have
 * permission to do something it wants to do.
 */
public class PermissionRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public PermissionRuntimeException() {
        super();
    }

    public PermissionRuntimeException(Throwable cause) {
        super(cause);
    }

    public PermissionRuntimeException(String message) {
        super(message);
    }

    public PermissionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

