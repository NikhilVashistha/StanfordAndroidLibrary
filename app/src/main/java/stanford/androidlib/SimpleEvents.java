/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

/**
 * A utility class for event handling.
 *
 * <pre>
 * SimpleEvents.with(context).handleEnterKeyPress(myEditText);
 * </pre>
 */
public final class SimpleEvents {
    private static Context context = null;
    private static final SimpleEvents INSTANCE = new SimpleEvents();

    /**
     * Returns a singleton SimpleEvents instance bound to the given context.
     */
    public static SimpleEvents with(Context context) {
        SimpleEvents.context = context;
        return INSTANCE;
    }

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
}
