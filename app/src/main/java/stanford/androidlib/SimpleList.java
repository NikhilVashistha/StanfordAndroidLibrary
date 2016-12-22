/*
 * @version 2016/04/07
 * - added get/setItems methods that take list ID rather than ListView reference for convenience
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.*;
import java.util.*;

/**
 * A utility class for ListViews and Adapters.
 *
 * <pre>
 * SimpleList.with(context).setItems(R.id.mylist, "foo", "bar", "baz");
 * </pre>
 */
public final class SimpleList {
    private static Context context = null;
    private static final SimpleList INSTANCE = new SimpleList();

    /**
     * Returns a singleton SimpleList instance bound to the given context.
     */
    public static SimpleList with(Context context) {
        SimpleList.context = context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleList instance bound to the given view's context.
     */
    public static SimpleList with(View context) {
        return with(context.getContext());
    }

    private SimpleList() {
        // empty
    }

    /**
     * Creates and returns an ArrayAdapter to store the given items.
     */
    @SafeVarargs
    public final <T> ArrayAdapter<String> createAdapter(T... items) {
        ArrayList<String> list = new ArrayList<>();
        for (T item : items) {
            list.add(String.valueOf(item));
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
    }

    /**
     * Creates and returns an ArrayAdapter to store the given items, using the given layout ID and TextView ID.
     */
    @SafeVarargs
    public final <T> ArrayAdapter<String> createAdapter(@LayoutRes int layoutID, @IdRes int textViewID, T... items) {
        ArrayList<String> list = new ArrayList<>();
        for (T item : items) {
            list.add(String.valueOf(item));
        }
        return new ArrayAdapter<>(context, layoutID, textViewID, list);
    }

    /**
     * Creates and returns an ArrayAdapter to store the given items.
     */
    public <T> ArrayAdapter<T> createAdapter(List<T> list) {
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
    }

    /**
     * Creates and returns an ArrayAdapter to store the given items, using the given layout ID and TextView ID.
     */
    public <T> ArrayAdapter<T> createAdapter(List<T> list, @LayoutRes int layoutID, @IdRes int textViewID) {
        return new ArrayAdapter<>(context, layoutID, textViewID, list);
    }

    /**
     * Returns the text of the items currently in the given list view as an ArrayList.
     */
    public ArrayList<String> getItems(AdapterView<?> listView) {
        ArrayList<String> list = new ArrayList<>();
        if (listView.getAdapter() instanceof ArrayAdapter) {
            Adapter adapter = listView.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                list.add(String.valueOf(adapter.getItem(i)));
            }
        }
        return list;
    }

    /**
     * Returns the text of the items currently in the given list view as an ArrayList.
     * For this method to work, the context passed to with() must be an activity.
     */
    public ArrayList<String> getItems(@IdRes int id) {
        AdapterView<?> listView = (AdapterView<?>) ((Activity) context).findViewById(id);
        return getItems(listView);
    }

    /**
     * Sets the items currently in the given list view to those stored in the given ArrayList.
     */
    @SuppressWarnings("unchecked")
    public SimpleList setItems(AdapterView<?> listView, Collection<String> items) {
        if (listView.getAdapter() instanceof ArrayAdapter) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
            adapter.setNotifyOnChange(false);

            // BUGFIX: need to copy list in case adapter already wraps context list
            ArrayList<String> copyOfItems = new ArrayList<>(items);
            adapter.clear();
            for (String item : copyOfItems) {
                adapter.add(item);
            }
            adapter.setNotifyOnChange(true);
            adapter.notifyDataSetChanged();
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new ArrayList<>(items));
            if (listView instanceof ListView) {
                ((ListView) listView).setAdapter(adapter);
            } else if (listView instanceof Spinner) {
                ((Spinner) listView).setAdapter(adapter);
            }
        }
        return this;
    }

    /**
     * Sets the items currently in the list with the given ID to those stored in the given ArrayList.
     * For this method to work, the context passed to with() must be an activity.
     */
    public SimpleList setItems(@IdRes int id, Collection<String> items) {
        AdapterView<?> listView = (AdapterView<?>) ((Activity) context).findViewById(id);
        return setItems(listView, items);
    }

    /**
     * Sets the items currently in the given list view to those passed in the given array.
     */
    public SimpleList setItems(AdapterView<?> listView, String[] items) {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, items);
        setItems(listView, list);
        return this;
    }

    /**
     * Sets the items currently in the list with the given ID view to those stored in the given array.
     * For this method to work, the context passed to with() must be an activity.
     */
    public SimpleList setItems(@IdRes int id, String[] items) {
        AdapterView<?> listView = (AdapterView<?>) ((Activity) context).findViewById(id);
        return setItems(listView, items);
    }

    /**
     * Sets the items currently in the given list view to those passed.
     */
    public SimpleList setItems(AdapterView<?> listView, Object... items) {
        ArrayList<String> list = new ArrayList<>();
        for (Object item : items) {
            list.add(String.valueOf(item));
        }
        setItems(listView, list);
        return this;
    }

    /**
     * Sets the items currently in the list with the given ID to those passed.
     * For this method to work, the context passed to with() must be an activity.
     */
    public SimpleList setItems(@IdRes int id, Object... items) {
        AdapterView<?> listView = (AdapterView<?>) ((Activity) context).findViewById(id);
        return setItems(listView, items);
    }
}
