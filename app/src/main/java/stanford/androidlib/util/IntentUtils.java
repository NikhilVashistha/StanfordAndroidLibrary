package stanford.androidlib.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.io.Serializable;

import stanford.androidlib.SimpleActivity;

/**
 * Utility methods related to intents.
 */
public final class IntentUtils {
    private static Context context = null;
    private static IntentUtils INSTANCE = new IntentUtils();

    public static IntentUtils with(Context context) {
        IntentUtils.context = context;
        return INSTANCE;
    }

    private IntentUtils() {
        // empty
    }

    public PendingIntent makePendingIntent(@NonNull Class<? extends Activity> activityClass, Object... params) {
        return makePendingIntent(activityClass, /* requestCode */ 0, params);
    }

    public PendingIntent makePendingIntent(@NonNull Class<? extends Activity> activityClass, int requestCode, Object... params) {
        Intent intent = new Intent(context, activityClass);
        putParameters(intent, params);
        return PendingIntent.getActivity(context, requestCode, intent, /* flags */ 0);
    }

    public Intent makeIntent(@NonNull Class<? extends Activity> activityClass, Object... params) {
        Intent intent = new Intent(context, activityClass);
        return putParameters(intent, params);
    }

    public Intent makeIntent(@NonNull String action, Object... params) {
        Intent intent = new Intent();
        intent.setAction(action);
        return putParameters(intent, params);
    }

    public Intent makeIntentAction(@NonNull Class<? extends Activity> activityClass, String action, Object... params) {
        Intent intent = new Intent(context, activityClass);
        intent.setAction(action);
        return putParameters(intent, params);
    }

    /**
     * Puts the given 'extra' parameters into the given Intent.
     * The parameters are passed as key/value pairs, as in:
     *
     * <pre>
     * putParameters(myIntent, "name1", value1, "name2", value2, ...);
     * </pre>
     *
     * The intent itself is returned, but the intent passed is also mutated,
     * so you don't need to use the returned value.
     */
    public static Intent putParameters(@NonNull Intent intent, Object... parameters) {
        for (int i = 0; i < parameters.length - 1; i += 2) {
            String name = String.valueOf(parameters[i]);
            Object value = parameters[i + 1];
            if (value instanceof Boolean) {
                intent.putExtra(name, (Boolean) value);
            } else if (value instanceof Bundle) {
                intent.putExtra(name, (Bundle) value);
            } else if (value instanceof Character) {
                intent.putExtra(name, (Character) value);
            } else if (value instanceof Double) {
                intent.putExtra(name, (Double) value);
            } else if (value instanceof Integer) {
                intent.putExtra(name, (Integer) value);
            } else if (value instanceof Long) {
                intent.putExtra(name, (Long) value);
            } else if (value instanceof String) {
                intent.putExtra(name, (String) value);
            } else if (value instanceof Serializable) {
                intent.putExtra(name, (Serializable) value);
            } else {
                // don't store?
                intent.putExtra(name, (String) null);
            }
        }
        return intent;
    }

}
