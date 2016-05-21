/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.Locale;

/**
 * A utility class for converting text-to-speech and speech-to-text.
 *
 * <pre>
 * SimpleSpeech.with(this).speak("hello!");
 * </pre>
 */
public final class SimpleSpeech {
    /**
     * Request code for Intent for speech-to-text.
     */
    public static final int REQ_CODE_SPEECH_TO_TEXT = 0x193b;

    private static Context context = null;
    private static final SimpleSpeech INSTANCE = new SimpleSpeech();

    /**
     * Returns a singleton SimpleSpeech instance bound to the given context.
     */
    public static SimpleSpeech with(Context context) {
        SimpleSpeech.context = context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleSpeech instance bound to the given view's context.
     */
    public static SimpleSpeech with(View context) {
        return with(context.getContext());
    }

    private TextToSpeech textToSpeech = null;
    private boolean textToSpeechReady = false;

    private SimpleSpeech() {
        // empty
    }

    /**
     * Says the given text aloud using Text-to-speech.
     * If any other speech is occurring, it is halted and this new speech begins immediately.
     */
    public SimpleSpeech speak(@StringRes int id) {
        String text = context.getResources().getString(id);
        speak(text);
        return this;
    }

    /**
     * Says the given text aloud using Text-to-speech.
     * If the 'immediately' boolean parameter is true, and if any other speech is occurring,
     * it is halted and this new speech begins immediately.
     * If the 'immediately' parameter is false, this text is spoken after any other text
     * previously sent to speak() is done being spoken.
     */
    public SimpleSpeech speak(@StringRes int id, boolean immediately) {
        String text = context.getResources().getString(id);
        speak(text, immediately);
        return this;
    }

    /**
     * Says the given text aloud using Text-to-speech.
     * If any other speech is occurring, it is halted and this new speech begins immediately.
     */
    public SimpleSpeech speak(@NonNull String text) {
        speak(text, /* immediately */ true);
        return this;
    }

    /**
     * Says the given text aloud using Text-to-speech.
     * If the 'immediately' boolean parameter is true, and if any other speech is occurring,
     * it is halted and this new speech begins immediately.
     * If the 'immediately' parameter is false, this text is spoken after any other text
     * previously sent to speak() is done being spoken.
     */
    @SuppressWarnings("deprecation")
    public SimpleSpeech speak(@NonNull final String text, final boolean immediately) {
        if (textToSpeech == null) {
            // need to create the speech system, wait for it to load, then play the sound
            textToSpeechReady = false;
            textToSpeech = new TextToSpeech(context, new TextToSpeechListener());
            new Thread(new Runnable() {
                public void run() {
                    while (!textToSpeechReady) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ie) {
                            // empty
                        }
                    }
                    textToSpeech.speak(text,
                            immediately ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD,
                            /* params */ null);
                }
            }).start();
        } else {
            // speech system already loaded; play sound now
            textToSpeech.speak(text,
                    immediately ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD,
                    /* params */ null);
        }
        return this;
    }

    /**
     * Shuts down the text-to-speech services and releases resources used.
     */
    public SimpleSpeech shutdown() {
        if (textToSpeech != null) {
            try {
                textToSpeech.stop();
                textToSpeech.shutdown();
                textToSpeech = null;
            } catch (RuntimeException re) {
                Log.wtf("SimpleSpeech", "exception when shutting down text-to-speech service", re);
            }
        }
        return this;
    }

    /**
     * Returns true if the current device supports text-to-speech capability.
     */
    public boolean textToSpeechSupported() {
        try {
            if (textToSpeech == null) {
                speak("");   // kludge to get the TTS object initialized
            }
            int support = textToSpeech.isLanguageAvailable(Locale.getDefault());
            return support != TextToSpeech.LANG_MISSING_DATA
                    && support != TextToSpeech.LANG_NOT_SUPPORTED;
        } catch (Exception e) {
            return false;
        }
    }

    private class TextToSpeechListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            // code to run when done loading
            textToSpeechReady = true;
        }
    }

    /**
     * Asks the device to start recording speech and converting it to text.
     * The prompt text represented by the given string resource ID is shown on the screen
     * to prompt the user to speak.
     * When the spoken words are done converting to text, the method onSpeechToTextReady
     * will be called.  Subclasses should override that method to grab the spoken text
     * as a string.
     * Logs the error if the action failed (e.g. if this device cannot support speech-to-text).
     * You may want to call speechToTextSupported first.
     */
    public SimpleSpeech speechToText(@StringRes int promptID) {
        String prompt = context.getResources().getString(promptID);
        speechToText(prompt);
        return this;
    }

    /**
     * Asks the device to start recording speech and converting it to text.
     * The given prompt text is shown on the screen to prompt the user to speak.
     * When the spoken words are done converting to text, the method onSpeechToTextReady
     * will be called.  Subclasses should override that method to grab the spoken text
     * as a string.
     * Logs the error if the action failed (e.g. if this device cannot support speech-to-text).
     * You may want to call speechToTextSupported first.
     */
    public SimpleSpeech speechToText(@NonNull String prompt) {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // prompt text is shown on screen to tell user what to say
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, REQ_CODE_SPEECH_TO_TEXT);
            } else {
                throw new IllegalStateException("context must be an activity");
            }
        } catch (ActivityNotFoundException anfe) {
            Log.wtf("SimpleSpeech", anfe);
        }
        return this;
    }

    /**
     * Returns true if the current device supports speech-to-text recognition.
     */
    public boolean speechToTextSupported() {
        // Check to see if a recognition activity is present
        PackageManager pm = context.getPackageManager();
        List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return activities.size() != 0;
    }
}
