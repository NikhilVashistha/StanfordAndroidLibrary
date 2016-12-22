/**
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
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns null.
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtra(@NonNull String name) {
        return getExtra(name, /* defaultValue */ null);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
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
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
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

    /// end empty event listener methods (copied from SimpleActivity)
}
