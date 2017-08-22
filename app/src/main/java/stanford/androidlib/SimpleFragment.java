/**
 * @version 2017/02/17
 * - added SimpleTask.TaskExecutor implementation
 * @version 2017/02/12
 * - made it so that if you don't write onCreateView, I try to auto-inflate your fragment for you
 *   by auto-inferring its layout ID (e.g. FooFragment => R.layout.fragment_foo)
 * @version 2017/02/06
 * - added more getXxxExtra methods
 * - added $A() to get simple activity
 * - added log, println methods (mirror of SimpleActivity versions, for convenience)
 * @version 2016/12/22
 * - added layoutID to avoid need for on*** lifecycle methods
 * - added init(), start() to match Stanford/ACM Java lib
 * @version 2016/01/27
 * - added setTraceLifecycle
 * @version 2016/01/25
 * - improved JavaDoc comments
 */

package stanford.androidlib;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

/**
 * A SimpleFragment is meant as a drop-in replacement for Android's Fragment
 * or DialogFragment classes.
 * It provides convenience methods to make basic Android programming easier for students
 * and new developers.  In your fragment class, you should write:
 *
 * <pre>
 * public class MyFragment extends SimpleFragment { ... }
 * </pre>
 */
public class SimpleFragment extends DialogFragment implements
        View.OnClickListener,
        View.OnTouchListener,
        View.OnDragListener,
        View.OnFocusChangeListener,
        View.OnGenericMotionListener,
        View.OnHoverListener,
        ViewStub.OnInflateListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        AdapterView.OnItemSelectedListener,
        GestureDetector.OnDoubleTapListener,
        GestureDetector.OnGestureListener,
        View.OnKeyListener,
        View.OnLongClickListener,
        MenuItem.OnMenuItemClickListener,
        ScaleGestureDetector.OnScaleGestureListener,
        CompoundButton.OnCheckedChangeListener,
        RadioGroup.OnCheckedChangeListener,
        SimpleTask.TaskExecutor,
        TextToSpeech.OnInitListener,
        OnSwipeListener.OnSwipeListenerImpl,
        OnSwipeListener.OnScaleListenerImpl {

    private @LayoutRes int layoutID = -1;
    private boolean traceLifecycleMethods = false;

    /**
     * Constructs a new simple fragment with no known layout resource ID.
     * Suggested idiom: In your subclass, write a zero-arg constructor that calls setLayoutID.
     */
    public SimpleFragment() {
        // empty
    }

    /**
     * Returns the resource ID of the layout to use for this fragment, as passed to the
     * constructor or setLayoutID.
     */
    public @LayoutRes int getLayoutID() {
        return layoutID;
    }

    /**
     * Returns the activity that contains this fragment as a SimpleActivity.
     * @throws ClassCastException If the activity containing this fragment does not extend SimpleActivity.
     */
    public SimpleActivity getSimpleActivity() {
        return (SimpleActivity) super.getActivity();
    }

    /**
     * Returns the activity that contains this fragment as a SimpleActivity.
     * @throws ClassCastException If the activity containing this fragment does not extend SimpleActivity.
     */
    public SimpleActivity $A() {
        return getSimpleActivity();
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns false.
     */
    public boolean getBooleanExtra(@NonNull String name) {
        return getBooleanExtra(name, /* defaultValue */ false);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public boolean getBooleanExtra(@NonNull String name, boolean defaultValue) {
        Intent intent = getActivity().getIntent();
        return intent.getBooleanExtra(name, defaultValue);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns 0.0.
     */
    public double getDoubleExtra(@NonNull String name) {
        return getDoubleExtra(name, /* defaultValue */ 0.0);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public double getDoubleExtra(@NonNull String name, double defaultValue) {
        Intent intent = getActivity().getIntent();
        return intent.getFloatExtra(name, (float) defaultValue);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns 0.
     */
    public int getIntExtra(@NonNull String name) {
        return getIntExtra(name, /* defaultValue */ 0);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public int getIntExtra(@NonNull String name, int defaultValue) {
        Intent intent = getActivity().getIntent();
        return intent.getIntExtra("name", defaultValue);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns an empty list of 0 elements.
     */
    public ArrayList<Integer> getIntegerArrayListExtra(@NonNull String name) {
        return getIntegerArrayListExtra(name, /* defaultValue */ new ArrayList<Integer>());
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public ArrayList<Integer> getIntegerArrayListExtra(@NonNull String name, ArrayList<Integer> defaultValue) {
        Intent intent = getActivity().getIntent();
        ArrayList<Integer> result = intent.getIntegerArrayListExtra(name);
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns 0.
     */
    public long getLongExtra(@NonNull String name) {
        return getLongExtra(name, /* defaultValue */ 0L);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public long getLongExtra(@NonNull String name, long defaultValue) {
        Intent intent = getActivity().getIntent();
        return intent.getLongExtra("name", defaultValue);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns null.
     */
    public <T extends Serializable> T getSerializableExtra(@NonNull String name) {
        return getSerializableExtra(name, /* defaultValue */ null);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getSerializableExtra(@NonNull String name, T defaultValue) {
        Intent intent = getActivity().getIntent();
        T result = (T) intent.getSerializableExtra(name);
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns an empty string.
     */
    public String getStringExtra(@NonNull String name) {
        return getStringExtra(name, /* defaultValue */ "");
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public String getStringExtra(@NonNull String name, String defaultValue) {
        Intent intent = getActivity().getIntent();
        String result = intent.getStringExtra(name);
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns an empty array of 0 elements.
     */
    public String[] getStringArrayExtra(@NonNull String name) {
        return getStringArrayExtra(name, /* defaultValue */ new String[0]);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public String[] getStringArrayExtra(@NonNull String name, String[] defaultValue) {
        Intent intent = getActivity().getIntent();
        String[] result = intent.getStringArrayExtra(name);
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns an empty list of 0 elements.
     */
    public ArrayList<String> getStringArrayListExtra(@NonNull String name) {
        return getStringArrayListExtra(name, /* defaultValue */ new ArrayList<String>());
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public ArrayList<String> getStringArrayListExtra(@NonNull String name, ArrayList<String> defaultValue) {
        Intent intent = getActivity().getIntent();
        ArrayList<String> result = intent.getStringArrayListExtra(name);
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns null.
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtra(@NonNull String name) {
        return getExtra(name, /* defaultValue */ null);
    }

    /**
     * Returns an 'extra' parameter with the given name from this fragment's activity's intent.
     * If there is no such 'extra' parameter, returns null.
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtra(@NonNull String name, T defaultValue) {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle.containsKey(name)) {
            return (T) bundle.get(name);
        } else {
            return defaultValue;
        }
    }

    /**
     * Sets the resource ID of the layout to use for this fragment.
     * Does not re-lay-out the fragment.
     */
    public void setLayoutID(@LayoutRes int layoutID) {
        this.layoutID = layoutID;
    }

    /// begin findViewById convenience methods



    /// end findViewById convenience methods


    /// begin lifecycle methods

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        traceLifecycleLog("onActivityCreated", "bundle=" + savedInstanceState);
        init();
    }

    /**
     * Code to run when fragment is being created.
     * This implementation is empty, but can be overridden in subclass.
     */
    protected void init() {
        // empty; override me
    }

    /**
     * Code to run when activity is being started.
     * This implementation is empty, but can be overridden in subclass.
     */
    protected void start() {
        // empty; override me
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        traceLifecycleLog("onActivityResult",
                    "requestCode=" + requestCode + " resultCode=" + resultCode + " intent=" + data);
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
        traceLifecycleLog("onAttach");
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        traceLifecycleLog("onCreate", "bundle=" + savedInstanceState);
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    // commenting out CallSuper because auto-generated onCreateView doesn't do it,
    // so I don't want the default generated class to already contain a compiler error
    // @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        traceLifecycleLog("onCreateView", "bundle=" + savedInstanceState);
        if (layoutID >= 0) {
            return inflater.inflate(layoutID, container, /* attachToRoot */ false);
        }

        // see if this class has an onCreateView method
        Method method = null;
        try {
            method = getClass().getDeclaredMethod("onCreateView");
        } catch (NoSuchMethodException nsme) {
            // empty
        }

        if (method == null) {
            // subclass has no onCreateView method; stub one in

            // get layout ID, e.g. R.layout.fragment_foo
            String layoutIdName = SimpleActivity.getDefaultLayoutIdName(this);
            layoutIdName = layoutIdName.replace("R.layout.", "");   // "fragment_foo"
            Activity act = getActivity();
            int layoutID = act.getResources().getIdentifier(
                    layoutIdName, "layout", act.getPackageName());  // R.layout.fragment_foo
            if (layoutID > 0) {
                return inflater.inflate(layoutID, container, /* attachToRoot */ false);
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        traceLifecycleLog("onDestroy");
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        traceLifecycleLog("onDestroyView");
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onDetach() {
        super.onDetach();
        traceLifecycleLog("onDetach");
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onPause() {
        super.onPause();
        traceLifecycleLog("onPause");
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        traceLifecycleLog("onResume");
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        traceLifecycleLog("onStart");
        start();
    }

    /**
     * Stops the activity, performing some needed cleanup such as shutting down the text-to-speech
     * system if it was in use.
     */
    @Override
    @CallSuper
    public void onStop() {
        super.onStop();
        traceLifecycleLog("onStop");
    }

    private void traceLifecycleLog(String method) {
        traceLifecycleLog(method, "");
    }

    private void traceLifecycleLog(String method, String message) {
        if (traceLifecycleMethods) {
            String className = getClass().getName();
            int dot = className.lastIndexOf(".");
            if (dot >= 0) {
                className = className.substring(dot + 1);
            }
            Log.i("SimpleFragment", className + " #" + this.hashCode() + " " + method
                    + "(" + message + ")");
        }
    }

    /**
     * Sets whether or not to print a log message every time an activity lifecycle
     * method such as onPause or onStart is called.
     */
    public void setTraceLifecycle(boolean trace) {
        traceLifecycleMethods = trace;
    }

    /// end lifecycle methods

    /// begin empty event listener methods (copied from SimpleActivity)

    /**
     * Empty event listener method to be overridden.
     */
    public void onSwipeLeft(float distance) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onSwipeRight(float distance) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onSwipeUp(float distance) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onSwipeDown(float distance) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onScale(float factor) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onClick(View v) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onLongClick(View v) {
        // empty; override me
        return false;
    }

    /**
     * Handles ListView and Spinner clicks and calls the other overload of onItemClick.
     */
    @Override
    @CallSuper
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        if (parent instanceof ListView) {
            onItemClick((ListView) parent, index);
        } else if (parent instanceof Spinner) {
            onItemClick((Spinner) parent, index);
        }
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onItemClick(ListView list, int index) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onItemClick(Spinner spinner, int index) {
        // empty; override me
    }

    /**
     * Handles ListView and Spinner clicks and calls the other overload of onItemLongClick.
     */
    @Override
    @CallSuper
    public boolean onItemLongClick(AdapterView<?> parent, View view, int index, long id) {
        if (parent instanceof ListView) {
            return onItemLongClick((ListView) parent, index);
        } else if (parent instanceof Spinner) {
            return onItemLongClick((Spinner) parent, index);
        }
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    public boolean onItemLongClick(ListView list, int index) {
        // empty; override me
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    public boolean onItemLongClick(Spinner spinner, int index) {
        // empty; override me
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // empty; override me
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // empty; override me
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // empty; override me
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // empty; override me
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onDrag(View v, DragEvent event) {
        // empty; override me
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onGenericMotion(View v, MotionEvent event) {
        // empty; override me
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onShowPress(MotionEvent e) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onLongPress(MotionEvent e) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onHover(View v, MotionEvent event) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onInflate(ViewStub stub, View inflated) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // empty; override me
    }

    /**
     * Handles ListView and Spinner clicks and calls the other overload of onItemSelected.
     */
    @Override
    @CallSuper
    public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
        if (parent instanceof ListView) {
            onItemSelected((ListView) parent, index);
        } else if (parent instanceof Spinner) {
            onItemSelected((Spinner) parent, index);
        }
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onItemSelected(ListView list, int index) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onItemSelected(Spinner spinner, int index) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    @Override
    public void onInit(int status) {
        // empty; override me
    }

    /**
     * Required method of SimpleTask.TaskExecutor interface.
     * This implementation is empty; override it if you want to be notified before your SimpleTask executes.
     */
    public void onPreExecute() {
        // empty; override me
    }

    /**
     * Required method of SimpleTask.TaskExecutor interface.
     * This implementation is empty; override it to supply code for your SimpleTask.
     */
    public void doInBackground(String... items) {
        // empty; override me
    }

    /**
     * Required method of SimpleTask.TaskExecutor interface.
     * This implementation is empty; override it if you want to be notified of progress of your SimpleTask.
     */
    public void onProgressUpdate(int progress) {
        // empty; override me
    }

    /**
     * Required method of SimpleTask.TaskExecutor interface.
     * This implementation is empty; override it if you want to be notified after your SimpleTask executes.
     */
    public void onPostExecute() {
        // empty; override me
    }

    /// end empty event listener methods (copied from SimpleActivity)

    /// begin log/print/toast convenience methods

    /**
     * Prints a debug (.d) log message containing the given text.
     */
    public void log(Object message) {
        Log.d("SimpleFragment log", String.valueOf(message));
    }

    /**
     * Prints a debug (.d) log message containing the given text.
     */
    public void log(String message) {
        Log.d("SimpleFragment log", message);
    }

    /**
     * Prints a debug (.d) log message containing the given text.
     */
    public void log(String tag, Object message) {
        Log.d(tag, String.valueOf(message));
    }

    /**
     * Prints a debug (.d) log message containing the given text.
     */
    public void log(String tag, String message) {
        Log.d(tag, message);
    }

    /**
     * Prints a WTF (.wtf) log message containing the given text and exception.
     */
    public void log(String message, Throwable exception) {
        Log.wtf("SimpleFragment log", message, exception);
    }

    /**
     * Prints a WTF (.wtf) log message containing the given exception.
     */
    public void log(Throwable exception) {
        Log.wtf("SimpleFragment log", "exception was thrown", exception);
    }

    /**
     * Prints a verbose (.v) log message containing the given formatted string.
     */
    public void printf(String message, Object... args) {
        Log.v("SimpleActivity printf", String.format(message, args));
    }

    /**
     * Prints a verbose (.v) log message containing the given text.
     */
    public void println(Object message) {
        Log.v("SimpleFragment println", String.valueOf(message));
    }

    /**
     * Prints a verbose (.v) log message containing the given text.
     */
    public void println(String message) {
        Log.v("SimpleFragment println", message);
    }
    
    /// end log/print/toast convenience methods
}
