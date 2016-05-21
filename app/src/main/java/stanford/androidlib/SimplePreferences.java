/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * A utility class for getting and setting preferences.
 *
 * <pre>
 * SimplePreferences.with(this).setSharedPreference("username", "Billy Bob");
 * </pre>
 */
public final class SimplePreferences {
    private static Activity context = null;
    private static final SimplePreferences INSTANCE = new SimplePreferences();

    /**
     * Returns a singleton SimpleMedia instance bound to the given context.
     */
    public static SimplePreferences with(Activity context) {
        SimplePreferences.context = context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimplePreferences instance bound to the given context.
     * The context must be an Activity, or an IllegalArgumentException will be thrown.
     */
    public static SimplePreferences with(Context context) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("context must be an Activity");
        }
        SimplePreferences.context = (Activity) context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimplePreferences instance bound to the given view's context.
     */
    public static SimplePreferences with(View context) {
        return with(context.getContext());
    }

    private SimplePreferences() {
        // empty
    }

    /**
     * Returns true if this activity contains a preference with the given name.
     * Equivalent to has(name).
     */
    public boolean contains(@NonNull String name) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        return prefs != null && prefs.contains(name);
    }

    /**
     * Returns true if this activity contains a shared preference with the given name
     * in the given shared preference filename.
     * Equivalent to hasShared(filename, name).
     */
    public boolean containsShared(@NonNull String filename, @NonNull String name) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs != null && prefs.contains(name);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns false.
     */
    public boolean getBoolean(@NonNull String name) {
        return getBoolean(name, /* defaultValue */ false);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public boolean getBoolean(@NonNull String name, boolean defaultValue) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getBoolean(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.0.
     */
    public double getDouble(@NonNull String name) {
        return getDouble(name, /* defaultValue */ 0.0);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public double getDouble(@NonNull String name, double defaultValue) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getFloat(name, (float) defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.
     */
    public int getInt(@NonNull String name) {
        return getInt(name, /* defaultValue */ 0);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public int getInt(@NonNull String name, int defaultValue) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getInt(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.
     */
    public long getLong(@NonNull String name) {
        return getLong(name, /* defaultValue */ 0L);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public long getLong(@NonNull String name, long defaultValue) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getLong(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns an empty string.
     */
    public String getString(@NonNull String name) {
        return getString(name, /* defaultValue */ "");
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public String getString(@NonNull String name, String defaultValue) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getString(name, defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns false.
     */
    public boolean getSharedBoolean(@NonNull String filename, @NonNull String name) {
        return getSharedBoolean(filename, name, /* defaultValue */ false);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public boolean getSharedBoolean(@NonNull String filename, @NonNull String name, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getBoolean(name, defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns 0.0.
     */
    public double getSharedDouble(@NonNull String filename, @NonNull String name) {
        return getSharedDouble(filename, name, /* defaultValue */ 0.0);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public double getSharedDouble(@NonNull String filename, @NonNull String name, double defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getFloat(name, (float) defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns 0.
     */
    public int getSharedInt(@NonNull String filename, @NonNull String name) {
        return getSharedInt(filename, name, /* defaultValue */ 0);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public int getSharedInt(@NonNull String filename, @NonNull String name, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getInt(name, defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns 0.
     */
    public long getSharedLong(@NonNull String filename, @NonNull String name) {
        return getSharedLong(filename, name, /* defaultValue */ 0L);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public long getSharedLong(@NonNull String filename, @NonNull String name, long defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getLong(name, defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns an empty string.
     */
    public String getSharedString(@NonNull String filename, @NonNull String name) {
        return getSharedString(filename, name, /* defaultValue */ "");
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public String getSharedString(@NonNull String filename, @NonNull String name, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getString(name, defaultValue);
    }

    /**
     * Returns true if this activity contains a preference with the given name.
     * Equivalent to contains(name).
     */
    public boolean has(@NonNull String name) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        return prefs != null && prefs.contains(name);
    }

    /**
     * Returns true if this activity contains a shared preference with the given name
     * in the given shared preference filename.
     * Equivalent to containsShared(filename, name).
     */
    public boolean hasShared(@NonNull String filename, @NonNull String name) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs != null && prefs.contains(name);
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public SimplePreferences set(@NonNull String name, boolean value) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(name, value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public SimplePreferences set(@NonNull String name, double value) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putFloat(name, (float) value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public SimplePreferences set(@NonNull String name, int value) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(name, value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public SimplePreferences set(@NonNull String name, long value) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putLong(name, value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public SimplePreferences set(@NonNull String name, String value) {
        SharedPreferences prefs = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(name, value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public SimplePreferences setShared(@NonNull String filename, @NonNull String name, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(name, value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public SimplePreferences setShared(@NonNull String filename, @NonNull String name, double value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putFloat(name, (float) value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public SimplePreferences setShared(@NonNull String filename, @NonNull String name, int value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(name, value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public SimplePreferences setShared(@NonNull String filename, @NonNull String name, long value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putLong(name, value);
        prefsEditor.apply();   // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public SimplePreferences setShared(@NonNull String filename, @NonNull String name, String value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(name, value);
        prefsEditor.apply();   // or commit();
        return this;
    }
}
