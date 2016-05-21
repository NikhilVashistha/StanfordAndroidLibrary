/*
 * @version 2016/05/17
 * - made methods non-static to decrease confusion
 * - added delete, exists, list, open, query() methods
 * @version 2016/02/23
 * - initial version
 */

package stanford.androidlib.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.RawRes;
import android.util.Log;
import java.util.*;
import stanford.androidlib.SimpleActivity;

/**
 * This class provides methods to help make it easier to query
 * local SQLite databases.
 * Some methods require the context of a SimpleActivity, so you
 * must call {@code SimpleDatabase.with(yourActivity)} first.
 */
public class SimpleDatabase {
    private static SimpleActivity context;
    private static SimpleDatabase INSTANCE = new SimpleDatabase();
    private static final Set<String> PRIVATE_TABLE_NAMES = new HashSet<>(Arrays.asList(
            "android_metadata"
    ));

    /**
     * Returns a singleton SimpleDatabase instance bound to the given context.
     */
    public static SimpleDatabase with(SimpleActivity context) {
        SimpleDatabase.context = context;
        return INSTANCE;
    }

    private static boolean logging = false;

    private SimpleDatabase() {
        // empty
    }

    /**
     * Sets whether Log.d statements should print as queries are run.
     */
    public SimpleDatabase setLogging(boolean logging) {
        SimpleDatabase.logging = logging;
        return INSTANCE;
    }

    /**
     * Deletes the database with the given name, if it exists.
     * Returns whether the database was deleted successfully.
     */
    public boolean delete(String databaseName) {
        return context.deleteDatabase(databaseName);
    }

    /**
     * Reads the .sql file with the given name and executes all SQL statements inside it,
     * placing them into a database with the same name as the file.
     * For example, if you pass "foo", reads/executes file resource R.raw.foo and
     * uses it to create/populate a database named foo.
     */
    public SimpleDatabase executeSqlFile(String name) {
        // possibly trim .sql extension
        if (name.toLowerCase().endsWith(".sql")) {
            name = name.substring(0, name.length() - 4);
        }
        SQLiteDatabase db = context.openOrCreateDatabase(name);
        int id = context.getResourceId(name, "raw");
        return executeSqlFile(db, id);
    }

    /**
     * Reads the .sql file with the given resource ID and executes all SQL statements inside it
     * using the given database.
     */
    public SimpleDatabase executeSqlFile(SQLiteDatabase db, @RawRes int id) {
        Scanner scan = context.openInternalFileScanner(id);
        String query = "";
        if (logging) Log.d("SimpleDB", "start reading file");
        int queryCount = 0;
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (line.startsWith("--") || line.isEmpty()) {
                continue;
            } else {
                query += line + "\n";
            }

            if (query.endsWith(";\n")) {
                if (logging) Log.d("SimpleDB", "query: \"" + query + "\"");
                db.execSQL(query);
                query = "";
                queryCount++;
            }
        }
        if (logging) Log.d("SimpleDB", "done reading file");
        if (logging) Log.d("SimpleDB", "performed " + queryCount + " queries.");
        return this;
    }

    /**
     * Returns true if a database with the given name exists.
     */
    public boolean exists(String databaseName) {
        return context.databaseExists(databaseName);
    }

    /**
     * Returns an array of the names of all databases in this app.
     */
    public String[] getDatabaseNames() {
        return context.databaseList();
    }

    /**
     * Returns the names of all tables in the given database as an array.
     * (Omits the private SQLite/Android table names like android_metadata.)
     * Reference: http://stackoverflow.com/questions/15383847/how-to-get-all-table-names-in-android-sqlite-database
     */
    public String[] getTableNames(String databaseName) {
        return getTableNames(context.openOrCreateDatabase(databaseName));
    }

    /**
     * Returns the names of all tables in the given database as an array.
     * (Omits the private SQLite/Android table names like android_metadata.)
     * Reference: http://stackoverflow.com/questions/15383847/how-to-get-all-table-names-in-android-sqlite-database
     */
    public String[] getTableNames(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        List<String> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String table = c.getString(0);
                if (!PRIVATE_TABLE_NAMES.contains(table)) {
                    list.add(table);
                }
            } while (c.moveToNext());
        }
        c.close();
        return list.toArray(new String[list.size()]);
    }

    /**
     * Opens the database with the given name, or creates it if it doesn't exist.
     * Equivalent to openOrCreateDatabase on an activity.
     */
    public SQLiteDatabase open(String databaseName) {
        return context.openOrCreateDatabase(databaseName);
    }

    /**
     * Performs the given database query on the given database and returns a view of the results.
     * Intended usage:
     *
     * <pre>
     * for (SimpleRow row : SimpleDatabase.with(this).query(db, myQuery)) { ... }
     * </pre>
     */
    public SimpleCursor query(SQLiteDatabase db, String query) {
        Cursor cursor = db.rawQuery(query, null);
        return rows(cursor);
    }

    /**
     * Performs the given database query on the database with the given name
     * and returns a view of the results.
     * Intended usage:
     *
     * <pre>
     * for (SimpleRow row : SimpleDatabase.with(this).query(db, myQuery)) { ... }
     * </pre>
     */
    public SimpleCursor query(String databaseName, String query) {
        SQLiteDatabase db = context.openOrCreateDatabase(databaseName);
        return query(db, query);
    }

    /**
     * Returns an object that can be used to iterate over the rows of a given
     * query cursor.
     *
     * <pre>
     *     for (SimpleRow row : SimpleDatabase.rows(myCursor)) { ... }
     * </pre>
     */
    public static SimpleCursor rows(Cursor cursor) {
        return new SimpleCursor(cursor);
    }
}
