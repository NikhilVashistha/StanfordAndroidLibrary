package stanford.androidlib;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import stanford.androidlib.util.IntentUtils;

/**
 * A SimpleService is meant as a drop-in replacement for Android's Service class.
 * It provides convenience methods to make basic Android programming easier for students
 * and new developers.  In your service class, you should write:
 *
 * <pre>
 * public class MyService extends SimpleService { ... }
 * </pre>
 *
 * By default a SimpleService is a 'started' and not 'bound' service.
 * It keeps running until shut down and does not rely on bound applications.
 * Rather than overriding the more typical {@code onStartCommand} method, you should
 * override the {@code onStartInThread} method which will be run by our {@code onStartCommand}
 * in its own thread for each request.
 */
public abstract class SimpleService extends Service {
    private HandlerThread hThread;

    /**
     * Lifecycle method that is called when the service is created.
     * Sets up a handler thread to process incoming requests.
     * As each request comes in, its onStartInThread method will be called.
     */
    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();
        hThread = new HandlerThread("SimpleService-" + Integer.toHexString(this.hashCode()));
        hThread.start();
    }

    /**
     * This method handles a single incoming request; rather than overriding this method,
     * override {@code onStartInThread(Intent)} to handle each incoming request.
     */
    @Override
    public final int onStartCommand(final Intent intent, int flags, int id) {
        if (intent != null) {
            intent.putExtra("service_flags", flags);
            intent.putExtra("service_id", id);
        }
        Looper looper = hThread.getLooper();
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            public void run() {
                // the code to process the job
                onStartInThread(intent);
            }
        });
        return START_STICKY;
    }

    /**
     * Override this method to handle each incoming request.
     * @param intent
     */
    public void onStartInThread(Intent intent) {
        // empty; override me
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
