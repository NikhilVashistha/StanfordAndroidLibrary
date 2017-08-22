/*
 * @version 2017/02/06
 * - added listen()
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * A utility class for event handling.
 *
 * <pre>
 * SimpleEvents.with(context).handleEnterKeyPress(myEditText);
 * </pre>
 */
public final class SimpleEvents {
    // set of all event names currently supported by the listen()/event() methods
    private static final Set<String> LISTEN_EVENTS_SUPPORTED = new HashSet<>(Arrays.asList(
            "click",
            "onclick",
            "longclick",
            "onlongclick",
            "drag",
            "ondrag",
            "focus",
            "onfocus",
            "focuschange",
            "onfocuschange",
            "hover",
            "onhover",
            "touch",
            "ontouch",
            "itemclick",
            "onitemclick",
            "itemlongclick",
            "onitemlongclick",
            "itemselected",
            "onitemselected"
    ));

    private static Context context = null;
    private static final SimpleEvents INSTANCE = new SimpleEvents();

    /**
     * Returns a singleton SimpleEvents instance bound to the given context.
     */
    public static SimpleEvents with(Context context) {
        SimpleEvents.context = context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleEvents instance bound to the given fragment's context.
     */
    public static SimpleEvents with(android.app.Fragment context) {
        return with(context.getActivity());
    }

    /**
     * Returns a singleton SimpleEvents instance bound to the given view's context.
     */
    public static SimpleEvents with(View context) {
        return with(context.getContext());
    }

    /**
     * A simple event listening interface that listens for Enter keypresses on an editable text view.
     */
    public interface EnterKeyPressListener {
        void onEnterKeyPress(View editText);
    }

    /*
     * A key listener that simply catches Enter keypresses and gets rid of them.
     * We want this in our input dialogs, else the text fields grow taller when
     * the user presses Enter, which is undesirable.
     */
    public static class EnterKeyPressSuppressor implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // If the event is a key-down event on the "enter" button,
            // consume Enter key press
            return (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER);
        }
    }

    private SimpleEvents() {
        // empty
    }

    /**
     * Attaches a listener so that this activity's onEnterKeyPress method will be called
     * when the Enter key is pressed on the given view.
     * Now also supports keyboard cursor movement with arrow keys and Home/End for physical keyboards.
     */
    public void handleEnterKeyPress(@NonNull final View editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (context instanceof EnterKeyPressListener) {
                        ((EnterKeyPressListener) context).onEnterKeyPress(editText);
                    }
                    return true;
                } else {
                    // this listener has not consumed the event
                    return false;
                }
            }
        });
    }

    /**
     * Attaches an event listener for the appropriate event that will call a method whose name is
     * the same as the given event name,
     * passing it the given parameters as appropriate.
     * @param viewID the ID of the widget on which to listen for the event
     * @param eventName event's name such as "click" or "onClick" (case-insensitive);
     * @throws IllegalArgumentException if the given event is not supported, the given method does not exist, is static, abstract, non-accessible, or does not accept the given number of parameters
     * @throws RuntimeException if the given method experiences any run-time error related to its reflection.
     */
    public void listen(@IdRes int viewID, @NonNull String eventName) {
        View view = ((Activity) context).findViewById(viewID);
        listen(view, eventName, /* methodName */ eventName);   // no params
    }

    /**
     * Attaches an event listener for the appropriate event that will call a method whose name is
     * the same as the given event name,
     * passing it the given parameters as appropriate.
     * @param view the widget on which to listen for the event
     * @param eventName event's name such as "click" or "onClick" (case-insensitive);
     * @throws IllegalArgumentException if the given event is not supported, the given method does not exist, is static, abstract, non-accessible, or does not accept the given number of parameters
     * @throws RuntimeException if the given method experiences any run-time error related to its reflection.
     */
    public void listen(@NonNull View view, @NonNull String eventName) {
        listen(view, eventName, /* methodName */ eventName);   // no params
    }

    /**
     * Attaches an event listener for the appropriate event that will call the method with the given name,
     * passing it the given parameters as appropriate.
     * @param viewID the ID of the widget on which to listen for the event
     * @param eventName event's name such as "click" or "onClick" (case-insensitive);
     *                  currently supported events are "click", ...
     * @param methodName the exact name of the method to invoke on the context object (case-sensitive) such as "fooBar" for "public void fooBar()"
     * @param params any parameters that you would like to pass to the given method (can be omitted if it accepts no parameters or only a View arg)
     * @throws IllegalArgumentException if the given event is not supported, the given method does not exist, is static, abstract, non-accessible, or does not accept the given number of parameters
     * @throws RuntimeException if the given method experiences any run-time error related to its reflection.
     */
    public void listen(@IdRes int viewID, @NonNull String eventName, @NonNull String methodName, Object... params) {
        View view = ((Activity) context).findViewById(viewID);
        listen(view, eventName, methodName, params);
    }

    /**
     * Attaches an event listener for the appropriate event that will call the method with the given name,
     * passing it the given parameters as appropriate.
     * @param view the widget on which to listen for the event
     * @param eventName event's name such as "click" or "onClick" (case-insensitive);
     *                  currently supported events are "click", ...
     * @param methodName the exact name of the method to invoke on the context object (case-sensitive) such as "fooBar" for "public void fooBar()"
     * @param params any parameters that you would like to pass to the given method (can be omitted if it accepts no parameters or only a View arg)
     * @throws IllegalArgumentException if the given event is not supported, the given method does not exist, is static, abstract, non-accessible, or does not accept the given number of parameters
     * @throws RuntimeException if the given method experiences any run-time error related to its reflection.
     */
    public void listen(@NonNull View view, @NonNull String eventName, @NonNull String methodName, Object... params) {
        final Class<?> contextClass = context.getClass();
        final String event = eventName.trim().toLowerCase();

        if (!LISTEN_EVENTS_SUPPORTED.contains(event)) {
            throw new IllegalArgumentException("Event '" + eventName + "' is not supported by this library.");
        }

        Method method = null;
        for (Method meth : contextClass.getDeclaredMethods()) {
            if (meth.getName().equals(methodName)) {
                method = meth;
                break;
            }
        }
        if (method == null) {
            throw new IllegalArgumentException("No method named '" + methodName + "' found in class " + contextClass.getName());
        } else if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("Method '" + methodName + "' in class " + contextClass.getName() + " cannot be static");
        } else if (Modifier.isAbstract(method.getModifiers())) {
            throw new IllegalArgumentException("Method '" + methodName + "' in class " + contextClass.getName() + " cannot be abstract");
        } else if (!Modifier.isPublic(method.getModifiers())) {
            try {
                // make it so that I can call this private/protected method by setting it as 'accessible'
                method.setAccessible(true);
            } catch (Exception e) {
                throw new IllegalArgumentException("Method '" + methodName + "' in class " + contextClass.getName() + " must be public");
            }
        }

        // make sure they pass correct # of parameters
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != params.length) {
            // special but common case: if the method accepts exactly one arg that is a View,
            // make a 'params' array on the fly that consists only of the 'view' param passed here.
            if (paramTypes.length == 1 && paramTypes[0] == View.class && params.length == 0) {
                params = new Object[] {view};
            } else if (params.length != 0) {
                throw new IllegalArgumentException("Method '" + methodName + "' in class " + contextClass.getName()
                        + " requires " + paramTypes.length + " parameters, but you passed "
                        + params.length + " parameter values: " + Arrays.toString(params));
            } // else params.length == 0, construct later
        }

        // let's (really) do it!
        listen(view, event, method, params);
    }

    // helper that actually does the event listening
    // TODO: add more supported event types
    // - onSwipeLRUD
    // x onLongClick
    // ~ onDrag
    // ~ onFocusChange
    // ~ onHover
    // ~ onKey
    // ~ onTouch(View, MotionEvent)

    // ~ AdapterView: onItemClick(parent, view, index, id)
    // ~ AdapterView: itemLongClick
    // ~ onItemSelected

    // - onScale
    // - onLongPress
    // - onSingleTap, onDoubleTap
    // - onScroll
    // - onFling
    // - onMenuItemClick
    // - onScale[Begin|End]
    // - onCheckedChanged
    // - onNothingSelected (meh)
    private void listen(@NonNull final View view, @NonNull final String event, @NonNull final Method method, @NonNull final Object... params) {
        final int paramCount = method.getParameterTypes().length;
        if (event.equals("click") || event.equals("onclick")) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invokeVoid(method, params);
                }
            });
        } else if (event.equals("longclick") || event.equals("onlongclick")) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return invokeBoolean(method, params);
                }
            });
        } else if (event.equals("drag") || event.equals("ondrag")) {
            view.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    final Object[] altParams = {v, event};
                    return invokeBoolean(method, params, altParams);
                }
            });
        } else if (event.equals("focus") || event.equals("onfocus") || event.equals("focuschange") || event.equals("onfocuschange")) {
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    final Object[] altParams = {v, hasFocus};
                    invokeVoid(method, params, altParams);
                }
            });
        } else if (event.equals("hover") || event.equals("onhover")) {
            view.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View v, MotionEvent event) {
                    final Object[] altParams = {v, event};
                    return invokeBoolean(method, params, altParams);
                }
            });
        } else if (event.equals("key") || event.equals("onkey")) {
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    final Object[] altParams = {v, keyCode, event};
                    return invokeBoolean(method, params, altParams);
                }
            });
        } else if (event.equals("touch") || event.equals("ontouch")) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final Object[] altParams = {v, event};
                    return invokeBoolean(method, params, altParams);
                }
            });
        } else if (event.equals("itemclick") || event.equals("onitemclick")
                || event.equals("itemselected") || event.equals("onitemselected")) {
            if (view instanceof Spinner) {
                Spinner spin = (Spinner) view;
                spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                        // because SimpleActivity has another overload of onItem* with just AdapterView and index as params
                        final Object[] altParams = (paramCount == 2)
                                ? new Object[] {parent, index}
                                : new Object[] {parent, view, index, id};
                        invokeVoid(method, params, altParams);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // empty
                    }
                });
            } else if (view instanceof AdapterView) {
                AdapterView<?> adapterView = (AdapterView<?>) view;
                adapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                        // because SimpleActivity has another overload of onItem* with just AdapterView and index as params
                        final Object[] altParams = (paramCount == 2)
                                ? new Object[] {parent, index}
                                : new Object[] {parent, view, index, id};
                        invokeVoid(method, params, altParams);
                    }
                });
            } else {
                throw new IllegalArgumentException("cannot listen to event '" + event + " on a View of type " + view.getClass().getName());
            }
        } else if (event.equals("itemlongclick") || event.equals("onitemlongclick")) {
            if (view instanceof AdapterView) {
                AdapterView<?> adapterView = (AdapterView<?>) view;
                adapterView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int index, long id) {
                        // because SimpleActivity has another overload of onItem* with just AdapterView and index as params
                        final Object[] altParams = (paramCount == 2)
                                ? new Object[] {parent, index}
                                : new Object[] {parent, view, index, id};
                        return invokeBoolean(method, params, altParams);
                    }
                });
            } else {
                throw new IllegalArgumentException("cannot listen to event '" + event + " on a View of type " + view.getClass().getName());
            }
        }
    }

    private boolean invokeBoolean(final Method method, final Object[] params) {
        return invokeBoolean(method, params, /* altParams */ new Object[0]);
    }

    private boolean invokeBoolean(final Method method, final Object[] params, final Object[] altParams) {
        Object[] paramsToUse = params;
        int paramCount = method.getParameterTypes().length;
        if (altParams != null && altParams.length == paramCount && params.length != paramCount) {
            paramsToUse = altParams;
        }

        try {
            Object result = method.invoke(/* this */ context, paramsToUse);
            if (result instanceof Boolean) {
                return ((Boolean) result);
            }
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException(iae);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getCause();
            throw new RuntimeException(t == null ? ite : t);
        }
        return false;
    }

    private void invokeVoid(final Method method, final Object[] params) {
        invokeVoid(method, params, /* altParams */ new Object[0]);
    }

    private void invokeVoid(final Method method, final Object[] params, final Object[] altParams) {
        Object[] paramsToUse = params;
        int paramCount = method.getParameterTypes().length;
        if (altParams != null && altParams.length == paramCount && params.length != paramCount) {
            paramsToUse = altParams;
        }

        try {
            method.invoke(/* this */ context, paramsToUse);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException(iae);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getCause();
            throw new RuntimeException(t == null ? ite : t);
        }
    }
}
