/*
 * @version 2016/02/23
 * - initial version
 */

package stanford.androidlib.data;

import android.content.ContentResolver;
import android.database.*;
import android.net.Uri;
import android.os.*;
import java.util.*;

/**
 * A simplified version of the Cursor class.
 * You can construct one of these by passing it an existing Cursor (which will be wrapped),
 * or implicitly by calling the {@code SimpleDatabase.rows} method.
 *
 * <p>
 * For detailed documentation of each member, see the documentation of the {@code Cursor} interface.
 * </p>
 */
public class SimpleCursor implements Cursor, Iterable<SimpleRow> {
    private Cursor cursor;

    /**
     * Constructs a new simple cursor to wrap the given cursor.
     * All calls on this cursor will be forwarded to the given cursor.
     */
    public SimpleCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Returns an int from a column with the given name.
     * Looks up the column's name using getColumnIndex.
     * If no such column exists, an exception is thrown.
     */
    public int getInt(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    /**
     * Returns a long from a column with the given name.
     * Looks up the column's name using getColumnIndex.
     * If no such column exists, an exception is thrown.
     */
    public long getLong(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return cursor.getLong(columnIndex);
    }

    /**
     * Returns a float from a column with the given name.
     * Looks up the column's name using getColumnIndex.
     * If no such column exists, an exception is thrown.
     */
    public float getFloat(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return cursor.getFloat(columnIndex);
    }

    /**
     * Returns a double from a column with the given name.
     * Looks up the column's name using getColumnIndex.
     * If no such column exists, an exception is thrown.
     */
    public double getDouble(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return cursor.getFloat(columnIndex);
    }

    /**
     * Returns a string from a column with the given name.
     * Looks up the column's name using getColumnIndex.
     * If no such column exists, an exception is thrown.
     */
    public String getString(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    /**
     * Returns a blob from a column with the given name.
     * Looks up the column's name using getColumnIndex.
     * If no such column exists, an exception is thrown.
     */
    public byte[] getBlob(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return cursor.getBlob(columnIndex);
    }

    /**
     * Returns a column's value of any type.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(int columnIndex) {
        int type = getType(columnIndex);
        Object result = null;
        if (type == Cursor.FIELD_TYPE_INTEGER) {
            result = getInt(columnIndex);
        } else if (type == Cursor.FIELD_TYPE_FLOAT) {
            result = getFloat(columnIndex);
        } else if (type == Cursor.FIELD_TYPE_STRING) {
            result = getString(columnIndex);
        } else if (type == Cursor.FIELD_TYPE_BLOB) {
            result = getBlob(columnIndex);
        }
        return (T) result;
    }

    /**
     * Returns a column's value of any type.
     */
    public <T> T get(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return get(columnIndex);
    }

    @Override
    public Iterator<SimpleRow> iterator() {
        return new ResultIterator();
    }

    private class ResultIterator implements Iterator<SimpleRow> {
        private Boolean lastHasNext = null;

        @Override
        public boolean hasNext() {
            if (lastHasNext == null) {
                lastHasNext = isBeforeFirst() ?  moveToFirst() : moveToNext();
                if (lastHasNext != null && !lastHasNext) {
                    // close the cursor when all rows have been consumed
                    close();
                }
            }
            return lastHasNext;
        }

        @Override
        public SimpleRow next() {
            if (!hasNext()) {   // does the actual moving
                throw new NoSuchElementException("past end of data");
            }
            lastHasNext = null;
            return new SimpleRow(SimpleCursor.this);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /// BEGIN METHODS THAT JUST FORWARD TO THE UNDERLYING CURSOR

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public int getPosition() {
        return cursor.getPosition();
    }

    @Override
    public boolean move(int offset) {
        return cursor.move(offset);
    }

    @Override
    public boolean moveToPosition(int position) {
        return cursor.moveToPosition(position);
    }

    @Override
    public boolean moveToFirst() {
        return cursor.moveToFirst();
    }

    @Override
    public boolean moveToLast() {
        return cursor.moveToLast();
    }

    @Override
    public boolean moveToNext() {
        return cursor.moveToNext();
    }

    @Override
    public boolean moveToPrevious() {
        return cursor.moveToPrevious();
    }

    @Override
    public boolean isFirst() {
        return cursor.isFirst();
    }

    @Override
    public boolean isLast() {
        return cursor.isLast();
    }

    @Override
    public boolean isBeforeFirst() {
        return cursor.isBeforeFirst();
    }

    @Override
    public boolean isAfterLast() {
        return cursor.isAfterLast();
    }

    @Override
    public int getColumnIndex(String columnName) {
        return cursor.getColumnIndex(columnName);
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        return cursor.getColumnIndexOrThrow(columnName);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return cursor.getColumnName(columnIndex);
    }

    @Override
    public String[] getColumnNames() {
        return cursor.getColumnNames();
    }

    @Override
    public int getColumnCount() {
        return cursor.getColumnCount();
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return cursor.getBlob(columnIndex);
    }

    @Override
    public String getString(int columnIndex) {
        return cursor.getString(columnIndex);
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        cursor.copyStringToBuffer(columnIndex, buffer);
    }

    @Override
    public short getShort(int columnIndex) {
        return cursor.getShort(columnIndex);
    }

    @Override
    public int getInt(int columnIndex) {
        return cursor.getInt(columnIndex);
    }

    @Override
    public long getLong(int columnIndex) {
        return cursor.getLong(columnIndex);
    }

    @Override
    public float getFloat(int columnIndex) {
        return cursor.getFloat(columnIndex);
    }

    @Override
    public double getDouble(int columnIndex) {
        return cursor.getDouble(columnIndex);
    }

    @Override
    public int getType(int columnIndex) {
        return cursor.getType(columnIndex);
    }

    @Override
    public boolean isNull(int columnIndex) {
        return cursor.isNull(columnIndex);
    }

    @Override
    public void deactivate() {
        cursor.deactivate();
    }

    @Override
    public boolean requery() {
        return cursor.requery();
    }

    @Override
    public void close() {
        cursor.close();
    }

    @Override
    public boolean isClosed() {
        return cursor.isClosed();
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        cursor.registerContentObserver(observer);
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        cursor.unregisterContentObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        cursor.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        cursor.unregisterDataSetObserver(observer);
    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
        cursor.setNotificationUri(cr, uri);
    }

    @Override
    public Uri getNotificationUri() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return cursor.getNotificationUri();
        } else {
            return null;
        }
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return cursor.getWantsAllOnMoveCalls();
    }

    @Override
    public void setExtras(Bundle extras) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cursor.setExtras(extras);
        }
    }

    @Override
    public Bundle getExtras() {
        return cursor.getExtras();
    }

    @Override
    public Bundle respond(Bundle extras) {
        return cursor.respond(extras);
    }

}
