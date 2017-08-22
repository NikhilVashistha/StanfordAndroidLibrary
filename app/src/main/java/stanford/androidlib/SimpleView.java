/*
 * @version 2017/02/06
 * - initial version
 */

package stanford.androidlib;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.*;
import java.util.*;

/**
 * A utility class for view-related features.
 *
 * <pre>
 * SimpleView.with(this).findChildren(parent, Button.class);
 * </pre>
 */
public final class SimpleView {
    private static Activity context = null;
    private static final SimpleView INSTANCE = new SimpleView();

    /**
     * Returns a singleton SimpleView instance bound to the given context.
     */
    public static SimpleView with(@NonNull Activity context) {
        SimpleView.context = context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleView instance bound to the given context.
     * The context must be an Activity, or an IllegalArgumentException will be thrown.
     */
    public static SimpleView with(@NonNull Context context) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("context must be an Activity");
        }
        SimpleView.context = (Activity) context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleView instance bound to the given view's context.
     */
    public static SimpleView with(@NonNull View context) {
        return with(context.getContext());
    }

    private SimpleView() {
        // empty
    }

    /**
     * Returns all direct children of this view group that are instances of the given View class.
     */
    @SuppressWarnings("unchecked")
    public final <T extends View> ArrayList<T> findChildren(@IdRes int parentID, @NonNull Class<T> viewClass) {
        ViewGroup parent = (ViewGroup) context.findViewById(parentID);
        return findChildren(parent, viewClass);
    }

    /**
     * Returns all direct children of this view group that are instances of the given View class.
     */
    @SuppressWarnings("unchecked")
    public final <T extends View> ArrayList<T> findChildren(ViewGroup parent, @NonNull Class<T> viewClass) {
        ArrayList<T> list = new ArrayList<>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (viewClass.isAssignableFrom(child.getClass())) {
                list.add((T) child);
            }
        }
        return list;
    }

    /**
     * Returns all direct children of this view group that are instances of one of the given classes.
     */
    @SafeVarargs
    public final ArrayList<View> findChildren(@IdRes int parentID, Class<? extends View>... viewClasses) {
        ViewGroup parent = (ViewGroup) context.findViewById(parentID);
        return findChildren(parent, viewClasses);
    }

    /**
     * Returns all direct children of this view group that are instances of one of the given classes.
     */
    @SafeVarargs
    public final ArrayList<View> findChildren(ViewGroup parent, Class<? extends View>... viewClasses) {
        ArrayList<View> list = new ArrayList<>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            for (Class<? extends View> clazz : viewClasses) {
                if (clazz.isAssignableFrom(child.getClass())) {
                    list.add(child);
                }
            }
        }
        return list;
    }

    /**
     * Returns all direct children of this view group that use the given "tag".
     */
    public ArrayList<View> findChildren(@IdRes int parentID, @NonNull Object tag) {
        ViewGroup parent = (ViewGroup) context.findViewById(parentID);
        return findChildren(parent, tag);
    }

    /**
     * Returns all direct children of this view group that use the given "tag".
     */
    public ArrayList<View> findChildren(ViewGroup parent, @NonNull Object tag) {
        ArrayList<View> list = new ArrayList<>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            Object childTag = child.getTag();
            if (childTag != null && childTag.equals(tag)) {
                list.add(child);
            }
        }
        return list;
    }

    /**
     * Returns all children, grandchildren, etc. of this view group that are instances
     * of the given View class.
     */
    public final <T extends View> ArrayList<T> findDescendents(@IdRes int parentID, @NonNull Class<T> viewClass) {
        ViewGroup parent = (ViewGroup) context.findViewById(parentID);
        return findDescendents(parent, viewClass);
    }

    /**
     * Returns all children, grandchildren, etc. of this view group that are instances
     * of the given View class.
     */
    @SuppressWarnings("unchecked")
    public final <T extends View> ArrayList<T> findDescendents(ViewGroup parent, @NonNull Class<T> viewClass) {
        ArrayList<T> list = new ArrayList<>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (viewClass.isAssignableFrom(child.getClass())) {
                list.add((T) child);
            }

            // recursively visit descendents
            if (child instanceof ViewGroup) {
                ViewGroup childGroup = (ViewGroup) child;
                ArrayList<T> list2 = findDescendents(childGroup, viewClass);
                list.addAll(list2);
            }
        }
        return list;
    }

    /**
     * Returns all children, grandchildren, etc. of this view group that are instances
     * of one of the given classes.
     */
    @SafeVarargs
    public final ArrayList<View> findDescendents(@IdRes int parentID, Class<? extends View>... viewClasses) {
        ViewGroup parent = (ViewGroup) context.findViewById(parentID);
        return findDescendents(parent, viewClasses);
    }

    /**
     * Returns all children, grandchildren, etc. of this view group that are instances
     * of one of the given classes.
     */
    @SafeVarargs
    public final ArrayList<View> findDescendents(ViewGroup parent, Class<? extends View>... viewClasses) {
        ArrayList<View> list = new ArrayList<>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            for (Class<? extends View> clazz : viewClasses) {
                if (clazz.isAssignableFrom(child.getClass())) {
                    list.add(child);
                }
            }

            // recursively visit descendents
            if (child instanceof ViewGroup) {
                ViewGroup childGroup = (ViewGroup) child;
                ArrayList<View> list2 = findDescendents(childGroup, viewClasses);
                list.addAll(list2);
            }
        }
        return list;
    }

    /**
     * Returns all children, grandchildren, etc. of this view group that use the given "tag".
     */
    public ArrayList<View> findDescendents(@IdRes int parentID, @NonNull Object tag) {
        ViewGroup parent = (ViewGroup) context.findViewById(parentID);
        return findDescendents(parent, tag);
    }

    /**
     * Returns all children, grandchildren, etc. of this view group that use the given "tag".
     */
    public ArrayList<View> findDescendents(ViewGroup parent, @NonNull Object tag) {
        ArrayList<View> list = new ArrayList<>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            Object childTag = child.getTag();
            if (childTag != null && childTag.equals(tag)) {
                list.add(child);
            }

            // recursively visit descendents
            if (child instanceof ViewGroup) {
                ViewGroup childGroup = (ViewGroup) child;
                ArrayList<View> list2 = findDescendents(childGroup, tag);
                list.addAll(list2);
            }
        }
        return list;
    }
}
