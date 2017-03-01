/*
 * @version 2017/03/01
 * - added toJson
 * - added getColumnNames
 * - added escape
 * - added checking/throwing exceptions in some methods when database not found
 * - TODO: change String[] return type to List<String>
 * @version 2017/02/19
 * - put executeSqlFile into a transaction for speed
 * @version 2017/02/15
 * - added QueryProgressListener, overloads to executeSqlFile
 * - added executeSqlFile that also takes String dbName
 * @version 2016/05/17
 * - made methods non-static to decrease confusion
 * - added delete, exists, list, open, query() methods
 * @version 2016/02/23
 * - initial version
 */

package stanford.androidlib.data;

import android.database.*;
import android.database.sqlite.*;
import android.support.annotation.RawRes;
import android.util.Log;
import org.json.*;
import java.util.*;
import stanford.androidlib.SimpleActivity;

/**
 * This class provides methods to help make it easier to query
 * local SQLite databases.
 * Some methods require the context of a SimpleActivity, so you
 * must call {@code SimpleDatabase.with(yourActivity)} first.
 */
public class SimpleDatabase {
    /**
     * An interface for listening to progress on a long query.
     */
    public interface QueryProgressListener {
        /**
         * Called when part of a long-running query finishes.
         * @param amountComplete between 0.0-1.0
         */
        public void queryUpdated(String query, double amountComplete);
    }

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

    // private constructor forbids instance construction
    private SimpleDatabase() {
        // empty
    }

    /**
     * Creates a database with the given name, if it doesn't exist.
     * If it does already exist, simply opens it and returns it.
     * Equivalent to openOrCreateDatabase on an activity.
     */
    public SQLiteDatabase create(String databaseName) {
        return open(databaseName);
    }

    /**
     * Deletes the database with the given name, if it exists.
     * Returns whether the database was deleted successfully.
     */
    public boolean delete(String databaseName) {
        return context.deleteDatabase(databaseName);
    }

    /**
     * Escapes/quotes a string for use as a parameter in an SQL query.
     */
    public String escape(String s) {
        return DatabaseUtils.sqlEscapeString(s);
    }

    /**
     * Reads the .sql file with the given name and executes all SQL statements inside it,
     * placing them into a database with the same name as the file.
     * For example, if you pass "foo", reads/executes file resource R.raw.foo and
     * uses it to create/populate a database named foo.
     */
    public SimpleDatabase executeSqlFile(String filename) {
        return executeSqlFile(/* dbName */ filename, filename, /* listener */ null);
    }

    /**
     * Reads the .sql file with the given name and executes all SQL statements inside it,
     * placing them into a database with the same name as the file.
     * For example, if you pass "foo", reads/executes file resource R.raw.foo and
     * uses it to create/populate a database named foo.
     */
    public SimpleDatabase executeSqlFile(String filename, QueryProgressListener listener) {
        return executeSqlFile(/* dbName */ filename, filename, listener);
    }

    /**
     * Reads the .sql file with the given name and executes all SQL statements inside it,
     * placing them into the database with the given name.
     * For example, if you pass "foo" and "bar", reads/executes file resource R.raw.bar and
     * uses it to create/populate a database named foo.
     */
    public SimpleDatabase executeSqlFile(String dbName, String filename, QueryProgressListener listener) {
        // possibly trim .sql extension
        if (filename.toLowerCase().endsWith(".sql")) {
            filename = filename.substring(0, filename.length() - 4);
        }
        SQLiteDatabase db = open(dbName);
        int id = context.getResourceId(filename, "raw");
        return executeSqlFile(db, id, listener);
    }

    /**
     * Reads the .sql file with the given resource ID and executes all SQL statements inside it
     * using the given database.
     */
    public SimpleDatabase executeSqlFile(SQLiteDatabase db, @RawRes int id) {
        return executeSqlFile(db, id, /* listener */ null);
    }

    /**
     * Reads the .sql file with the given resource ID and executes all SQL statements inside it
     * using the given database.
     */
    public SimpleDatabase executeSqlFile(SQLiteDatabase db, @RawRes int id, QueryProgressListener listener) {
        Scanner scan = context.openInternalFileScanner(id);
        if (logging) Log.d("SimpleDB", "start reading file");

        // read file into list of queries to run
        List<String> queries = new ArrayList<>();
        StringBuilder currentQuery = new StringBuilder();
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (line.startsWith("--") || line.isEmpty()) {
                continue;
            } else {
                currentQuery.append(line);
                currentQuery.append('\n');
            }

            if (currentQuery.toString().endsWith(";\n")) {
                queries.add(currentQuery.toString());
                currentQuery.delete(0, currentQuery.length());
            }
        }

        // execute the queries (in a transaction, to speed up)
        db.beginTransaction();
        int queriesExecuted = 0;
        for (String query : queries) {
            if (logging) Log.d("SimpleDB", "query: \"" + query + "\"");
            db.execSQL(query);
            queriesExecuted++;
            if (listener != null) {
                listener.queryUpdated(query, /* percentComplete */ 1.0 * queriesExecuted / queries.size());
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        if (logging) Log.d("SimpleDB", "done reading file");
        if (logging) Log.d("SimpleDB", "performed " + queries.size() + " queries.");
        return this;
    }

    /**
     * Returns true if a database with the given name exists.
     */
    public boolean exists(String databaseName) {
        return context.databaseExists(databaseName);
    }

    /**
     * Returns the names of all columns in the given query's cursor as an array.
     * The column names will appear in the array in their natural order in which
     * they occur in the internal ordering of the query.
     */
    public String[] getColumnNames(Cursor cr) {
        int cols = cr.getColumnCount();
        String[] names = new String[cols];
        for (int i = 0; i < cols; i++) {
            names[i] = cr.getColumnName(i);
        }
        return names;
    }

    /**
     * Returns the names of all columns in the given database table as an array.
     * The column names will appear in the array in their natural order in which
     * they occur in the internal ordering of the database.
     * @throws SQLiteCantOpenDatabaseException if the given database does not exist
     */
    public String[] getColumnNames(String databaseName, String tableName) {
        return getColumnNames(openOrThrow(databaseName), tableName);
    }

    /**
     * Returns the names of all columns in the given database table as an array.
     * The column names will appear in the array in their natural order in which
     * they occur in the internal ordering of the database.
     * @throws SQLiteCantOpenDatabaseException if the given database does not exist
     * @throws SQLiteException if the given table does not exist
     */
    public String[] getColumnNames(SQLiteDatabase db, String tableName) {
        String query = "SELECT * FROM " + tableName + " LIMIT 1";
        Cursor cr = db.rawQuery(query, null);
        String[] names = getColumnNames(cr);
        cr.close();
        return names;
    }

    /**
     * Returns an array of the names of all databases in this app.
     */
    public String[] getDatabaseNames() {
        return context.databaseList();
    }

    /**
     * Returns the names of all tables in the given database as an array.
     * The table names will appear in the array in their natural order in which
     * they occur in the internal ordering of the database.
     * (Omits the private SQLite/Android table names like android_metadata.)
     * Reference: http://stackoverflow.com/questions/15383847/how-to-get-all-table-names-in-android-sqlite-database
     * @throws SQLiteCantOpenDatabaseException if the given database does not exist
     */
    public String[] getTableNames(String databaseName) {
        return getTableNames(openOrThrow(databaseName));
    }

    /**
     * Returns the names of all tables in the given database as an array.
     * The table names will appear in the array in their natural order in which
     * they occur in the internal ordering of the database.
     * (Omits the private SQLite/Android table names like android_metadata.)
     * Reference: http://stackoverflow.com/questions/15383847/how-to-get-all-table-names-in-android-sqlite-database
     */
    public String[] getTableNames(SQLiteDatabase db) {
        // here are the columns of the sqlite_master table:
        // type="table", name="courses", tbl_name="courses", rootpage=4, sql="CREATE TABLE courses (...)"

        List<String> list = new ArrayList<>();
        for (SimpleRow row : query(db, "SELECT name FROM sqlite_master WHERE type='table'")) {
            String table = row.get("name");
            if (!PRIVATE_TABLE_NAMES.contains(table)) {
                list.add(table);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Opens the database with the given name, or creates it if it doesn't exist.
     * Equivalent to openOrCreateDatabase on an activity.
     */
    public SQLiteDatabase open(String databaseName) {
        return context.openOrCreateDatabase(databaseName);
    }

    // helper to open an existing database, or throw an exception if it doesn't exist
    private SQLiteDatabase openOrThrow(String databaseName) {
        if (exists(databaseName)) {
            return open(databaseName);
        } else {
            throw new SQLiteCantOpenDatabaseException("no such database: " + databaseName);
        }
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
     *
     * @throws SQLiteCantOpenDatabaseException if the given database does not exist
     */
    public SimpleCursor query(String databaseName, String query) {
        SQLiteDatabase db = openOrThrow(databaseName);
        return query(db, query);
    }

    /**
     * Performs all of the given database queries on the given database
     * as a transaction to speed them up as well as making sure that all of them
     * are completed successfully.
     * Intended usage:
     *
     * <pre>
     * SimpleDatabase.with(this).queryTransaction(db, query1, query2, query3));
     * </pre>
     */
    public void queryTransaction(SQLiteDatabase db, String... queries) {
        queryTransaction(db, /* listener */ null, queries);
    }

    /**
     * Performs all of the given database queries on the given database
     * as a transaction to speed them up as well as making sure that all of them
     * are completed successfully.
     * Intended usage:
     *
     * <pre>
     * SimpleDatabase.with(this).queryTransaction(db, query1, query2, query3));
     * </pre>
     */
    public void queryTransaction(String databaseName, String... queries) {
        queryTransaction(databaseName, /* listener */ null, queries);
    }

    /**
     * Performs all of the given database queries on the given database
     * as a transaction to speed them up as well as making sure that all of them
     * are completed successfully.
     * Contacts the given listener after each individual query to notify it of the
     * overall progress. (If the listener is null, it is ignored.)
     *
     * Intended usage:
     *
     * <pre>
     * SimpleDatabase.with(this).queryTransaction(db, listener, query1, query2, query3));
     * </pre>
     */
    public void queryTransaction(SQLiteDatabase db, QueryProgressListener listener, String... queries) {
        db.beginTransaction();
        int complete = 0;
        for (String query : queries) {
            db.rawQuery(query, null);
            complete++;
            if (listener != null) {
                listener.queryUpdated(query, (double) complete / queries.length);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * Performs all of the given database queries on the given database
     * as a transaction to speed them up as well as making sure that all of them
     * are completed successfully.
     * Contacts the given listener after each individual query to notify it of the
     * overall progress. (If the listener is null, it is ignored.)
     *
     * Intended usage:
     *
     * <pre>
     * SimpleDatabase.with(this).queryTransaction(db, listener, query1, query2, query3));
     * </pre>
     *
     * @throws SQLiteCantOpenDatabaseException if the given database does not exist
     */
    public void queryTransaction(String databaseName, QueryProgressListener listener, String... queries) {
        SQLiteDatabase db = openOrThrow(databaseName);
        queryTransaction(db, listener, queries);
    }

    /**
     * Returns an object that can be used to iterate over the rows of a given
     * query cursor.
     *
     * <pre>
     * for (SimpleRow row : SimpleDatabase.rows(myCursor)) { ... }
     * </pre>
     */
    public static SimpleCursor rows(Cursor cursor) {
        return new SimpleCursor(cursor);
    }

    /**
     * Sets whether Log.d statements should print as queries are run.
     */
    public SimpleDatabase setLogging(boolean logging) {
        SimpleDatabase.logging = logging;
        return INSTANCE;
    }

    /**
     * Returns a {@code JSONObject} representing all of the data from the given database.
     * The JSON object will store each table of data using its name as its key.
     * The data of each table will be represented as described in {@code tableToJson}.
     *
     * @throws SQLiteCantOpenDatabaseException if the given database does not exist
     */
    public JSONObject toJson(String databaseName) {
        return toJson(openOrThrow(databaseName));
    }

    /**
     * Returns a {@code JSONObject} representing all of the data from the given database.
     * The JSON object will store each table of data using its name as its key.
     * The data of each table will be represented as described in {@code tableToJson}.
     * @throws IllegalStateException if JSON data cannot be converted
     */
    public JSONObject toJson(SQLiteDatabase db) {
        JSONObject dbJson = new JSONObject();
        for (String tableName : getTableNames(db)) {
            JSONObject tableJson = toJson(db, tableName);
            try {
                dbJson.put(tableName, tableJson);
            } catch (JSONException jsone) {
                throw new IllegalStateException("unable to convert table into JSON data", jsone);
            }
        }
        return dbJson;
    }

    /**
     * Returns a {@code JSONObject} representing all of the data from the given table
     * from the given database.
     * The JSON object will store each row of data using its "id" column as its key,
     * or if there is no "id" column, by ascending integer indexes.
     *
     * @throws SQLiteCantOpenDatabaseException if the given database does not exist
     * @throws SQLiteException if the given table does not exist in the given database
     */
    public JSONObject toJson(String databaseName, String tableName) {
        return toJson(openOrThrow(databaseName), tableName);
    }

    /**
     * Returns a {@code JSONObject} representing all of the data from the given table
     * from the given database.
     * The JSON object will store each row of data using its "id" column as its key,
     * or if there is no "id" column, by ascending integer indexes.
     *
     * @throws SQLiteException if the given table does not exist in the given database
     * @throws IllegalStateException if JSON data cannot be converted
     */
    public JSONObject toJson(SQLiteDatabase db, String tableName) {
        JSONObject tableJson = new JSONObject();
        int i = 0;
        for (SimpleRow row : query(db, "SELECT * FROM " + tableName)) {
            // todo: figure out primary key
            JSONObject rowJson = row.asJSON();
            try {
                if (rowJson.has("id")) {
                    // use "id" as primary key column
                    tableJson.put(String.valueOf(rowJson.get("id")), rowJson);
                } else {
                    // store with integer indexes; pseudo-array
                    tableJson.put(String.valueOf(i), rowJson);
                    i++;
                }
            } catch (JSONException jsone) {
                throw new IllegalStateException("unable to convert table rows into JSON data", jsone);
            }
        }

        return tableJson;
    }
}
