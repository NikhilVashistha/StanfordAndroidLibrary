package stanford.androidlib;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import stanford.androidlib.util.IntentUtils;

/**
 * A SimpleService is meant as a drop-in replacement for Android's IntentService class.
 * It provides convenience methods to make basic Android programming easier for students
 * and new developers.  In your service class, you should write:
 *
 * <pre>
 * public class MyService extends SimpleIntentService { ... }
 * </pre>
 *
 * By default a SimpleIntentService is a 'started' and not 'bound' service.
 * It keeps running until shut down and does not rely on bound applications.
 * Override the {@code onHandleIntent} method to process each request.
 */
public abstract class SimpleIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SimpleIntentService() {
        super("SimpleIntentService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SimpleIntentService(String name) {
        super(name);
    }

    /**
     * This method returns null to indicate that a SimpleService does not support binding.
     */
    @Nullable
    @Override
    public final IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Sends a broadcast using an intent with the given action
     * and passes it the given 'extra' parameters.
     */
    public void sendBroadcast(String action, Object... parameters) {
        // unpack and store parameters
        sendBroadcast(IntentUtils.with(this).makeIntent(action, parameters));
    }
}
