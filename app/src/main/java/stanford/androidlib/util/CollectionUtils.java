package stanford.androidlib.util;

import android.content.Intent;
import android.os.Bundle;
import java.util.*;

/**
 * This class contains some static methods related to Java collections
 * that pertain to Android development.
 */
public final class CollectionUtils {
    private CollectionUtils() {
        // empty
    }

    /**
     * Returns the key/value pairs from the given bundle as a Map.
     * If the bundle is null or contains no parameters, returns an empty Map.
     */
    public static Map<String, Object> asMap(Bundle bundle) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                map.put(key, bundle.get(key));
            }
        }
        return map;
    }

    /**
     * Returns the key/value pairs from the given intent's parameters as a Map.
     * If the intent is null or contains no parameters, returns an empty Map.
     */
    public static Map<String, Object> asMap(Intent intent) {
        return asMap(intent == null ? null : intent.getExtras());
    }
}
