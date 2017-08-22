/*
 * @version 2017-02-11
 * - initial version
 */

package stanford.androidlib;

import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;

/**
 * A simpler version of the Adapter used by ListViews.
 * You can extend this class and override the getView method to customize
 * the way that items in the list should be displayed.
 * You can also call SimpleAdapter.with(this, data, ...) to get an adapter without
 * providing an entire subclass.
 */
public abstract class SimpleAdapter extends BaseAdapter {
    /**
     * Interface for objects that return views for a SimpleAdapter.
     */
    public interface ViewProvider {
        View provideViewToAdapter(SimpleAdapter adapter, int index);
    }

    /**
     * Returns an adapter that gets its items from the given list and its views from
     * the given view provider.
     */
    public static SimpleAdapter with(final Context context, final Iterable<String> data) {
        SimpleAdapter adapter = new SimpleAdapter.Impl();
        adapter.init(context, data);
        return adapter;
    }

    /**
     * Returns an adapter that gets its items from the given array and its views from
     * the given view provider.
     */
    public static SimpleAdapter with(final Context context, final String[] data) {
        SimpleAdapter adapter = new SimpleAdapter.Impl();
        adapter.init(context, data);
        return adapter;
    }

    /**
     * Returns an adapter that gets its items from the given list and its views from
     * the given view provider.
     */
    public static SimpleAdapter with(final Context context, final Iterable<String> data, final ViewProvider provider) {
        return new SimpleAdapter() {
            {
                // instance initializer
                init(context, data);
            }

            @Override
            public View getView(int index) {
                return provider.provideViewToAdapter(this, index);
            }

            @Override
            public void setView(int index, View view) {
                // empty
            }
        };
    }

    /**
     * Returns an adapter that gets its items from the given array and its views from
     * the given view provider.
     */
    public static SimpleAdapter with(final Context context, final String[] data, final ViewProvider provider) {
        return new SimpleAdapter() {
            {
                // instance initializer
                init(context, data);
            }

            @Override
            public View getView(int index) {
                return provider.provideViewToAdapter(this, index);
            }

            @Override
            public void setView(int index, View view) {
                // empty
            }
        };
    }

    private Context context;
    private View convertView;
    private View parent;
    private List<String> mData;

    /**
     * Constructs an adapter without any items.
     */
    private SimpleAdapter() {
        // empty
    }

    /**
     * Constructs an adapter to hold the given list of items.
     */
    @SuppressWarnings("unchecked")
    protected SimpleAdapter(Context context, Iterable<String> data) {
        init(context, data);
    }

    /**
     * Constructs an adapter to hold the given array of items.
     */
    @SuppressWarnings("unchecked")
    protected SimpleAdapter(Context context, String... data) {
        init(context, data);
    }

    /*
     * Initializes the adapter with the given context and data.
     */
    void init(Context context, Iterable<String> data) {
        this.context = context;
        if (data instanceof List) {
            this.mData = (List<String>) data;
        } else {
            this.mData = new ArrayList<>();
            for (String element : data) {
                mData.add(element);
            }
        }
    }

    /*
     * Initializes the adapter with the given context and data.
     */
    protected void init(Context context, String... data) {
        this.context = context;
        this.mData = new ArrayList<>();
        for (String element : data) {
            mData.add(element);
        }
    }

    /**
     * Returns the Context passed to the constructor.
     */
    @SuppressWarnings("unchecked")
    public final <T extends Context> T getContext() {
        return (T) context;
    }

    /**
     * Returns the "convert view" passed to getView.
     * Will be null if getView has never been called.
     */
    protected final View getConvertView() {
        return convertView;
    }

    /**
     * Returns the number of elements in the list passed to the constructor.
     * @return
     */
    @Override
    public final int getCount() {
        return mData.size();
    }

    /**
     * Returns the item at the given index as passed to the constructor.
     */
    @Override
    public final String getItem(int index) {
        return String.valueOf(mData.get(index));
    }

    /**
     * Returns a unique ID for the item at the given index,
     * which is just that index itself.
     */
    @Override
    public final long getItemId(int index) {
        return index;
    }


    /**
     * Returns the "parent" view passed to getView.
     * Will be null if getView has never been called.
     */
    protected final View getParent() {
        return parent;
    }

    /**
     * Builds and returns a View for displaying the item at the given index.
     * This method simply calls the other simpler overload of getView, which you must override
     * in your SimpleAdapter subclass.
     * If your overload needs the removed parameters "convertView" or "parent",
     * you should call getConvertView or getParent from that method.
     */
    @Override
    public final View getView(final int index, View convertView, ViewGroup parent) {
        return getView(index);
    }

    /**
     * Returns the number of items in the adapter.
     */
    public int getItemCount() {
        return mData.size();
    }

    /**
     * In your subclass, you must override this method to return a View
     * that best displays the item of your adapter's list at the given index.
     */
    public abstract View getView(int index);

    /**
     * Sets the given view to be returned for the item in the list at the given index.
     */
    public abstract void setView(int index, View view);

    /**
     * A default implementation.
     */
    private static class Impl extends SimpleAdapter {
        private List<View> mViews = new ArrayList<>();

        /**
         * In your subclass, you must override this method to return a View
         * that best displays the item of your adapter's list at the given index.
         */
        public View getView(int index) {
            if (mViews != null && index < mViews.size()) {
                return mViews.get(index);
            } else if (index >= 0 && index < this.getItemCount()) {
                throw new IllegalStateException("No View found at index " + index + ". You must call setView() to add views to this adapter.");
            } else {
                throw new IndexOutOfBoundsException("" + index);
            }
        }

        @Override
        public void setView(int index, View view) {
            while (mViews.size() <= index) {
                mViews.add(null);
            }
            mViews.set(index, view);
        }
    }
}
