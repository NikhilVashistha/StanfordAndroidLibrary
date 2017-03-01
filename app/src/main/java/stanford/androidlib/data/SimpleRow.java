/*
 * @version 2017/03/01
 * - added asJSON
 * @version 2016/02/25
 * - added asList, asMap
 * @version 2016/02/23
 * - initial version
 */

package stanford.androidlib.data;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A SimpleRow object represents one row of a SQLite database query result.
 * See the {@code SimpleCursor} class for detailed documentation.
 *
 * <pre>
 *     for (SimpleRow row : SimpleDatabase.rows(myCursor)) { ... }
 * </pre>
 */
public class SimpleRow extends SimpleCursor {
    /**
     * Constructs a simple row from the current position of the given cursor.
     * @param cursor
     */
    public SimpleRow(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns this row's contents as a List with the column values as its values.
     * The list preserves the column order from the SQL query.
     */
    public List<Object> asList() {
        List<Object> list = new ArrayList<>();
        int cols = getColumnCount();
        for (int i = 0; i < cols; i++) {
            Object value = get(i);
            list.add(value);
        }
        return list;
    }

    /**
     * Returns this row's contents as a JSON object with the column names as the keys
     * and the column values as the associated values for each key.
     * @throws IllegalStateException if JSON data cannot be converted
     */
    public JSONObject asJSON() {
        JSONObject json = new JSONObject();
        Map<String, Object> map = asMap();
        for (String key : map.keySet()) {
            try {
                json.put(key, map.get(key));
            } catch (JSONException jsone) {
                throw new IllegalStateException("error creating JSON object from row data", jsone);
            }
        }
        return json;
    }

    /**
     * Returns this row's contents as a Map with the column names as the keys
     * and the column values as the associated values for each key.
     * The map preserves the column order from the SQL query.
     */
    public Map<String, Object> asMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        int cols = getColumnCount();
        for (int i = 0; i < cols; i++) {
            String colName = getColumnName(i);
            Object value = get(i);
            map.put(colName, value);
        }
        return map;
    }

    /**
     * Returns a string representation of this row for debugging.
     */
    public String toString() {
        int cols = getColumnCount();
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleRow{");
        for (int i = 0; i < cols; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            String colName = getColumnName(i);
            Object value = get(i);
            sb.append(colName);
            sb.append('=');
            sb.append(value);
        }
        sb.append('}');
        return sb.toString();
    }
}
