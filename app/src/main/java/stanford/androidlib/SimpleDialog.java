/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TimePicker;
import java.util.Calendar;

/**
 * A utility class for showing various kinds of dialog boxes.
 *
 * <pre>
 * SimpleDialog.with(this).showAlertDialog("Hello!");
 * </pre>
 */
public final class SimpleDialog {
    private static Context context = null;
    private static DialogListener listener = null;
    private static final SimpleDialog INSTANCE = new SimpleDialog();

    /**
     * Returns a singleton SimpleDialog instance bound to the given context.
     */
    public static SimpleDialog with(Context context) {
        SimpleDialog.context = context;
        if (context instanceof DialogListener) {
            listener = (DialogListener) context;
        } else {
            throw new IllegalArgumentException("context passed must be a SimpleActivity or a class that implements the DialogListener interface");
        }
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleDialog instance bound to the given view's context.
     */
    public static SimpleDialog with(View context) {
        return with(context.getContext());
    }

    /**
     * Returns a singleton SimpleMedia instance bound to the given context and listener.
     * The given listener will be notified when dialogs are closed or canceled.
     * A SimpleActivity serves as a DialogListener already.
     */
    public static SimpleDialog with(Context context, DialogListener listener) {
        SimpleDialog.context = context;
        SimpleDialog.listener = listener;
        return INSTANCE;
    }

    public interface DialogListener {
        /**
         * Empty event listener method to be overridden.
         */
        void onAlertDialogClose(AlertDialog dialog);
        /**
         * Empty event listener method to be overridden.
         */
        void onDateInputDialogClose(View view, int year, int month, int day);

        /**
         * Empty event listener method to be overridden.
         */
        void onInputDialogClose(AlertDialog dialog, String input);

        /**
         * Empty event listener method to be overridden.
         */
        void onMultiInputDialogClose(AlertDialog dialog, String[] inputs);

        /**
         * Empty event listener method to be overridden.
         */
        void onProgressDialogClose(ProgressDialog dialog);

        /**
         * Empty event listener method to be overridden.
         */
        void onTimeInputDialogClose(View view, int hour, int minute);

        /**
         * Empty event listener method to be overridden.
         */
        void onDialogNegativeClick(DialogInterface dialog);

        /**
         * Empty event listener method to be overridden.
         */
        void onDialogCancel(DialogInterface dialog);
    }

    /**
     * A class with empty implementations of all methods in the DialogListener interface.
     */
    public static class DialogAdapter implements DialogListener {
        /**
         * Empty event listener method to be overridden.
         */
        public void onAlertDialogClose(AlertDialog dialog) {
            // empty
        }

        /**
         * Empty event listener method to be overridden.
         */
        public void onDateInputDialogClose(View view, int year, int month, int day) {
            // empty
        }

        /**
         * Empty event listener method to be overridden.
         */
        public void onInputDialogClose(AlertDialog dialog, String input) {
            // empty
        }

        /**
         * Empty event listener method to be overridden.
         */
        public void onMultiInputDialogClose(AlertDialog dialog, String[] inputs) {
            // empty
        }

        /**
         * Empty event listener method to be overridden.
         */
        public void onProgressDialogClose(ProgressDialog dialog) {
            // empty
        }

        /**
         * Empty event listener method to be overridden.
         */
        public void onTimeInputDialogClose(View view, int hour, int minute) {
            // empty
        }

        /**
         * Empty event listener method to be overridden.
         */
        public void onDialogNegativeClick(DialogInterface dialog) {
            // empty
        }

        /**
         * Empty event listener method to be overridden.
         */
        public void onDialogCancel(DialogInterface dialog) {
            // empty
        }
    }


    private DialogSettings dialogSettings = new DialogSettings();

    private SimpleDialog() {
        // empty
    }

    /**
     * Shows an alert dialog to display a message to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you when they press OK.
     */
    public AlertDialog showAlertDialog(String message) {
        return showAlertDialog(message, /* title */ "");
    }

    /**
     * Shows an alert dialog to display a message to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you when they press OK.
     */
    public AlertDialog showAlertDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(String.valueOf(message));
        if (title != null && !title.isEmpty()) {
            builder.setTitle(title);
        }
        builder.setPositiveButton(dialogSettings.getOkText(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onAlertDialogClose((AlertDialog) dialog);
            }
        });
        __checkDialogSettings(builder, /* checkCancelable */ false);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * Shows an alert dialog to display a message to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you when they press OK.
     */
    public AlertDialog showAlertDialog(@StringRes int messageID) {
        String message = context.getResources().getString(messageID);
        return showAlertDialog(message);
    }

    /**
     * Shows an alert dialog to display a message to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you when they press OK.
     */
    public AlertDialog showAlertDialog(@StringRes int messageID, @StringRes int titleID) {
        String message = context.getResources().getString(messageID);
        String title = context.getResources().getString(titleID);
        return showAlertDialog(message, title);
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onAlertDialogCancel if they press No.
     */
    public AlertDialog showConfirmDialog(String message) {
        return showConfirmDialog(message, /* yesText */ null, /* noText */ null);
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onAlertDialogCancel if they press No.
     */
    public AlertDialog showConfirmDialog(String message,
                                         String positiveButtonText,
                                         String negativeButtonText) {
        return showConfirmDialog(message, positiveButtonText, negativeButtonText, /* title */ "");
    }
    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onAlertDialogCancel if they press No.
     */
    public AlertDialog showConfirmDialog(String message,
                                         String positiveButtonText,
                                         String negativeButtonText,
                                         String title) {
        positiveButtonText = dialogSettings.getYesText(positiveButtonText);
        negativeButtonText = dialogSettings.getNoText(negativeButtonText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(String.valueOf(message));
        if (title != null && !title.isEmpty()) {
            builder.setTitle(title);
        }
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Button button = ((AlertDialog) dialog).getButton(id);
                String textClicked = button.getText().toString();
                listener.onInputDialogClose((AlertDialog) dialog, textClicked);
            }
        });
        builder.setNegativeButton(negativeButtonText, new SimpleActivityOnNegativeListener());
        __checkDialogSettings(builder);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onAlertDialogCancel if they press No.
     */
    public AlertDialog showConfirmDialog(@StringRes int messageID) {
        return showConfirmDialog(messageID, /* yesTextID */ -1, /* noTextID */ -1);
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onDialogNegativeClick if they press No.
     */
    public AlertDialog showConfirmDialog(@StringRes int messageID,
                                         @StringRes int positiveButtonTextID,
                                         @StringRes int negativeButtonTextID) {
        return showConfirmDialog(messageID, positiveButtonTextID, negativeButtonTextID, /* titleID */ -1);
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onDialogNegativeClick if they press No.
     */
    public AlertDialog showConfirmDialog(@StringRes int messageID,
                                         @StringRes int positiveButtonTextID,
                                         @StringRes int negativeButtonTextID,
                                         @StringRes int titleID) {
        String message = messageID > 0 ? context.getResources().getString(messageID) : null;
        String positiveButtonText = positiveButtonTextID > 0 ? context.getResources().getString(positiveButtonTextID) : null;
        String negativeButtonText = negativeButtonTextID > 0 ? context.getResources().getString(negativeButtonTextID) : null;
        String title = titleID > 0 ? context.getResources().getString(titleID) : null;
        return showConfirmDialog(message, positiveButtonText, negativeButtonText, title);
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(String message) {
        return showInputDialog(message, /* okText */ null);
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(String message, String positiveButtonText) {
        return showInputDialog(message, positiveButtonText, /* title */ "");
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(String message, String positiveButtonText, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        if (title != null && !title.isEmpty()) {
            builder.setTitle(title);
        }

        // hack so that inner key listener can find the OK button later
        final Button[] buttons = new Button[1];
        final EditText editText = new EditText(context);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // close dialog if user presses Enter
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    buttons[0].callOnClick();
                    return true;
                } else {
                    return false;
                }
            }
        });
        builder.setView(editText);

        positiveButtonText = dialogSettings.getOkText(positiveButtonText);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onInputDialogClose((AlertDialog) dialog, editText.getText().toString());
            }
        });
        __checkDialogSettings(builder);
        AlertDialog dialog = builder.create();
        dialog.show();
        buttons[0] = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        return dialog;
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(@StringRes int messageID) {
        return showInputDialog(messageID, /* yesID */ -1);
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(@StringRes int messageID,
                                       @StringRes int positiveButtonTextID) {
        return showInputDialog(messageID, positiveButtonTextID, /* titleID */ -1);
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(@StringRes int messageID,
                                       @StringRes int positiveButtonTextID,
                                       @StringRes int titleID) {
        String message = messageID > 0 ? context.getResources().getString(messageID) : null;
        String positiveButtonText = positiveButtonTextID > 0 ? context.getResources().getString(positiveButtonTextID) : null;
        String title = titleID > 0 ? context.getResources().getString(titleID) : null;
        return showInputDialog(message, positiveButtonText, title);
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     */
    public AlertDialog showMultiInputDialog(String... prompts) {
        return __showMultiInputDialogHelper(/* message */ null, prompts);
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     */
    public AlertDialog showMultiInputDialogWithMessage(String message, String... prompts) {
        return __showMultiInputDialogHelper(message, prompts);
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     * Pass a boolean to indicate whether a Cancel button should be present in the dialog.
     */
    private AlertDialog __showMultiInputDialogHelper(String message, final String... prompts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (message != null && !message.isEmpty()) {
            builder.setTitle(message);
        }
        LinearLayout layout = new TableLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        final EditText[] editTexts = new EditText[prompts.length];
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i] = new EditText(context);
            editTexts[i].setHint(prompts[i]);
            editTexts[i].setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            editTexts[i].setOnKeyListener(new EnterKeyPressSuppressor());
            layout.addView(editTexts[i]);
        }
        builder.setView(layout);

        String positiveText = dialogSettings.getOkText();
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String[] results = new String[prompts.length];
                for (int i = 0; i < editTexts.length; i++) {
                    results[i] = editTexts[i].getText().toString();
                }
                listener.onMultiInputDialogClose((AlertDialog) dialog, results);
            }
        });
        __checkDialogSettings(builder);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     */
    public AlertDialog showMultiInputDialog(@ArrayRes int promptsID) {
        return showMultiInputDialog(/* messageID */ -1, promptsID);
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     */
    public AlertDialog showMultiInputDialog(@StringRes int messageID, @ArrayRes int promptsID) {
        String message = messageID > 0 ? context.getResources().getString(messageID) : null;
        final String[] prompts = context.getResources().getStringArray(promptsID);
        return __showMultiInputDialogHelper(message, prompts);
    }

    /**
     * Shows a list input dialog to allow the user to tap one of several choices.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showListInputDialog(@ArrayRes int itemsID) {
        String[] items = context.getResources().getStringArray(itemsID);
        return showListInputDialog(items);
    }

    /**
     * Shows a list input dialog to allow the user to tap one of several choices.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showListInputDialog(String message, @ArrayRes int itemsID) {
        String[] items = context.getResources().getStringArray(itemsID);
        return showListInputDialogHelper(message, items);
    }

    /**
     * Shows a list input dialog to allow the user to tap one of several choices.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showListInputDialog(final String... items) {
        return showListInputDialogHelper(/* message */ "", items);
    }

    /**
     * Shows a list input dialog to allow the user to tap one of several choices.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showListInputDialogWithMessage(final String message, final String... items) {
        return showListInputDialogHelper(message, items);
    }

    /**
     * Shows a list input dialog to allow the user to tap one of several choices.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    private AlertDialog showListInputDialogHelper(final String message, final String... items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (message != null && !message.isEmpty()) {
            builder.setTitle(message);
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                listener.onInputDialogClose((AlertDialog) dialog, items[index]);
            }
        });
        __checkDialogSettings(builder);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     * No items will be initially checked.
     */
    public AlertDialog showCheckboxInputDialog(final String... items) {
        final boolean[] checkedItems = new boolean[items.length];
        return showCheckboxInputDialog(checkedItems, items);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     * Pass an array of booleans to indicate which items are initially checked, if any.
     */
    public AlertDialog showCheckboxInputDialog(final boolean[] checkedItems,
                                               final String... items) {
        return showCheckboxInputDialog(/* message */ "", checkedItems, items);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     * No items will be initially checked.
     */
    public AlertDialog showCheckboxInputDialogWithMessage(final String message, final String... items) {
        final boolean[] checkedItems = new boolean[items.length];
        return showCheckboxInputDialog(message, checkedItems, items);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     * Pass an array of booleans to indicate which items are initially checked, if any.
     */
    public AlertDialog showCheckboxInputDialog(final String message,
                                               final boolean[] checkedItems,
                                               final String... items) {
        final boolean[] checkedItemsToUse = checkedItems == null ? new boolean[items.length] : checkedItems;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (message != null && !message.isEmpty()) {
            builder.setTitle(message);
        }
        builder.setMultiChoiceItems(items, checkedItemsToUse, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                if (index >= 0 && index < checkedItemsToUse.length) {
                    checkedItemsToUse[index] = isChecked;
                }
            }
        });
        String positiveText = dialogSettings.getOkText();
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // build an array of all items that are checked
                int checkedCount = 0;
                for (boolean bool : checkedItemsToUse) {
                    if (bool) {
                        checkedCount++;
                    }
                }
                String[] checkedItemStrings = new String[checkedCount];
                int index = 0;
                for (int i = 0; i < checkedItemsToUse.length; i++) {
                    if (checkedItemsToUse[i]) {
                        checkedItemStrings[index] = items[i];
                        index++;
                    }
                }
                listener.onMultiInputDialogClose((AlertDialog) dialog, checkedItemStrings);
            }
        });
        __checkDialogSettings(builder);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     */
    public AlertDialog showCheckboxInputDialog(@ArrayRes int itemsID) {
        String[] items = context.getResources().getStringArray(itemsID);
        return showCheckboxInputDialog(items);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     * Pass an array of booleans to indicate which items are initially checked, if any.
     */
    public AlertDialog showCheckboxInputDialog(boolean[] checkedItems, @ArrayRes int itemsID) {
        String[] items = context.getResources().getStringArray(itemsID);
        return showCheckboxInputDialog(checkedItems, items);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     */
    public AlertDialog showCheckboxInputDialog(@StringRes int messageID, @ArrayRes int itemsID) {
        String message = context.getResources().getString(messageID);
        String[] items = context.getResources().getStringArray(itemsID);
        return showCheckboxInputDialog(message, /* checkedItems */ null, items);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     * Pass an array of booleans to indicate which items are initially checked, if any.
     */
    public AlertDialog showCheckboxInputDialog(@StringRes int messageID, boolean[] checkedItems, @ArrayRes int itemsID) {
        String message = context.getResources().getString(messageID);
        String[] items = context.getResources().getStringArray(itemsID);
        return showCheckboxInputDialog(message, checkedItems, items);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     */
    public AlertDialog showRadioInputDialog(String... items) {
        return showRadioInputDialog(/* checkedIndex */ -1, items);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     */
    public AlertDialog showRadioInputDialogWithMessage(String message, String... items) {
        return showRadioInputDialog(message, /* checkedIndex */ -1, items);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     * Pass an integer to represent the index that should be initially checked.
     */
    public AlertDialog showRadioInputDialog(int checkedIndex, final String... items) {
        return showRadioInputDialog(/* message */ "", checkedIndex, items);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     * Pass an integer to represent the index that should be initially checked.
     */
    public AlertDialog showRadioInputDialog(String message, int checkedIndex, final String... items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (message != null && !message.isEmpty()) {
            builder.setTitle(message);
        }
        if (checkedIndex >= items.length) {
            checkedIndex = items.length - 1;
        }
        final int[] checkedItemIndex = new int[1];
        checkedItemIndex[0] = checkedIndex;
        builder.setSingleChoiceItems(items, checkedIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                checkedItemIndex[0] = index;
            }
        });
        String okText = dialogSettings.getOkText();
        builder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String checkedItem = "";
                if (checkedItemIndex[0] >= 0 && checkedItemIndex[0] < items.length) {
                    checkedItem = items[checkedItemIndex[0]];
                }
                listener.onInputDialogClose((AlertDialog) dialog, checkedItem);
            }
        });
        __checkDialogSettings(builder);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     */
    public AlertDialog showRadioInputDialog(@ArrayRes int itemsID) {
        return showRadioInputDialog(/* checkedIndex */ 0, itemsID);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     */
    public AlertDialog showRadioInputDialogWithMessage(@StringRes int messageID, @ArrayRes int itemsID) {
        return showRadioInputDialog(messageID, /* checkedIndex */ 0, itemsID);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     * Pass an integer to represent the index that should be initially checked.
     */
    public AlertDialog showRadioInputDialog(int checkedIndex, @ArrayRes int itemsID) {
        String[] items = context.getResources().getStringArray(itemsID);
        return showRadioInputDialog(checkedIndex, items);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     * Pass an integer to represent the index that should be initially checked.
     */
    public AlertDialog showRadioInputDialog(@StringRes int messageID, int checkedIndex, @ArrayRes int itemsID) {
        String message = context.getResources().getString(messageID);
        String[] items = context.getResources().getStringArray(itemsID);
        return showRadioInputDialog(message, checkedIndex, items);
    }

    /**
     * Shows a time picker dialog to prompt the user to select a time of day.
     * When the user is done with the dialog, your activity's onTimeInputDialogClose method
     * will be called, passing the hour and minute that were chosen by the user.
     */
    public TimePickerDialog showTimeInputDialog() {
        return showTimeInputDialog(/* message */ null);
    }

    /**
     * Shows a time picker dialog to prompt the user to select a time of day.
     * When the user is done with the dialog, your activity's onTimeInputDialogClose method
     * will be called, passing the hour and minute that were chosen by the user.
     */
    public TimePickerDialog showTimeInputDialog(String message) {
        Calendar c = Calendar.getInstance();
        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(Calendar.MINUTE);
        return showTimeInputDialog(message, nowHour, nowMinute);
    }

    /**
     * Shows a time picker dialog to prompt the user to select a time of day.
     * When the user is done with the dialog, your activity's onTimeInputDialogClose method
     * will be called, passing the hour and minute that were chosen by the user.
     */
    public TimePickerDialog showTimeInputDialog(int startHour, int startMinute) {
        return showTimeInputDialog(/* message */ null, startHour, startMinute);
    }

    /**
     * Shows a time picker dialog to prompt the user to select a time of day.
     * When the user is done with the dialog, your activity's onTimeInputDialogClose method
     * will be called, passing the hour and minute that were chosen by the user.
     */
    public TimePickerDialog showTimeInputDialog(String message, int startHour, int startMinute) {
        // http://developer.android.com/guide/topics/ui/controls/pickers.html
        boolean is24hour = android.text.format.DateFormat.is24HourFormat(context);
        TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                listener.onTimeInputDialogClose(view, hour, minute);
            }
        }, startHour, startMinute, is24hour);
        if (message != null && !message.isEmpty()) {
            dialog.setMessage(message);
        }
        __checkDialogSettings(dialog);
        dialog.show();
        return dialog;
    }


    /**
     * Shows a date picker dialog to prompt the user to select a date.
     * When the user is done with the dialog, your activity's onDateInputDialogClose method
     * will be called, passing the year, month, and day (1-based) that were chosen by the user.
     */
    public DatePickerDialog showDateInputDialog() {
        return showDateInputDialog(/* message */ null);
    }

    /**
     * Shows a date picker dialog to prompt the user to select a date.
     * When the user is done with the dialog, your activity's onDateInputDialogClose method
     * will be called, passing the year, month, and day (1-based) that were chosen by the user.
     */
    public DatePickerDialog showDateInputDialog(String message) {
        return showDateInputDialog(message, /* year */ -1, /* month */ -1, /* day */ -1);
    }

    /**
     * Shows a date picker dialog to prompt the user to select a date.
     * When the user is done with the dialog, your activity's onDateInputDialogClose method
     * will be called, passing the year, month, and day (1-based) that were chosen by the user.
     */
    public DatePickerDialog showDateInputDialog(int startYear, int startMonth, int startDay) {
        return showDateInputDialog(/* message */ null, startYear, startMonth, startDay);
    }

    /**
     * Shows a date picker dialog to prompt the user to select a date.
     * When the user is done with the dialog, your activity's onDateInputDialogClose method
     * will be called, passing the year, month, and day (1-based) that were chosen by the user.
     */
    public DatePickerDialog showDateInputDialog(String message, int startYear, int startMonth, int startDay) {
        if (startMonth < 0 || startMonth < 0 || startDay < 0) {
            Calendar c = Calendar.getInstance();
            startYear = c.get(Calendar.YEAR);
            // add 1 to month here because Java's dumb months are 0-based (0=Jan, 11=Dec)
            startMonth = c.get(Calendar.MONTH) + 1;
            startDay = c.get(Calendar.DAY_OF_MONTH);
        }

        // subtract 1 from month passed to DatePickerDialog constructor here
        // because Java's dumb months are 0-based (0=Jan, 11=Dec)
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // add 1 to month here because Java's dumb months are 0-based (0=Jan, 11=Dec)
                listener.onDateInputDialogClose(view, year, month + 1, day);
            }
        }, startYear, startMonth - 1, startDay);

        if (message != null && !message.isEmpty()) {
            dialog.setMessage(message);
        }
        __checkDialogSettings(dialog);
        dialog.show();
        return dialog;
    }

    /**
     * Shows a progress dialog with a progress bar.
     * The dialog shows a default message of "Working ..."
     * The dialog progresses up to a default max value of 100.
     */
    public SimpleProgressDialog showProgressDialog() {
        return showProgressDialog(/* message */ "Working ...");
    }

    /**
     * Shows a progress dialog with a progress bar.
     * The dialog progresses up to a default max value of 100.
     */
    public SimpleProgressDialog showProgressDialog(String message) {
        return showProgressDialog(message, /* max */ 100);
    }

    /**
     * Shows a progress dialog with a progress bar.
     * The given max is the highest value the progress bar can reach; once
     * it reaches that point, the client should presumably tell the progress
     * dialog to dismiss() itself.
     * Pass a max &lt;= 0 for an indeterminate dialog that does not have a specific maximum.
     */
    public SimpleProgressDialog showProgressDialog(String message, int max) {
        boolean indeterminate = max <= 0;
        final SimpleProgressDialog dialog = new SimpleProgressDialog(context);
        if (message != null && !message.isEmpty()) {
            dialog.setMessage(message);
        }
        dialog.setIndeterminate(indeterminate);
        if (indeterminate) {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        } else {
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(max);
            dialog.setProgress(0);
        }
        __checkDialogSettings(dialog);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog2) {
                listener.onProgressDialogClose(dialog);
            }
        });
        dialog.show();
        return dialog;
    }

    // internal helper class for notifying activity when various dialogs are canceled
    private class SimpleActivityOnCancelListener implements
            DialogInterface.OnCancelListener,
            DialogInterface.OnClickListener {
        @Override
        public void onCancel(DialogInterface dialog) {
            listener.onDialogCancel(dialog);
        }

        @Override
        public void onClick(DialogInterface dialog, int id) {
            listener.onDialogCancel(dialog);
        }
    }

    private class SimpleActivityOnNegativeListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            listener.onDialogNegativeClick(dialog);
        }
    }

    /**
     * Globally sets whether dialogs created with show_Xxx_Dialog(...)
     * will display a Cancel button.
     */
    public void setDialogsCancelable(boolean cancelable) {
        dialogSettings.cancelable = cancelable;
    }

    /**
     * Globally sets whether dialogs created with show_Xxx_Dialog(...)
     * to display the given icon.
     * Initially by default no icon will be shown on any dialogs.
     */
    public void setDialogsIcon(@DrawableRes int iconID) {
        Log.d("SimpleActivity", "setDialogsIcon " + iconID);
        dialogSettings.iconID = iconID;
    }

    /**
     * Globally sets the title that dialogs created with show_Xxx_Dialog(...)
     * should display.
     * Initially by default no title will be shown.
     * Set to empty string or null to remove the title.
     */
    public void setDialogsTitle(String title) {
        dialogSettings.title = title;
    }

    /**
     * Globally sets the title that dialogs created with show_Xxx_Dialog(...)
     * should display.
     * Initially by default no title will be shown.
     * Set to empty string or null to remove the title.
     */
    public void setDialogsTitle(@StringRes int titleID) {
        dialogSettings.title = context.getResources().getString(titleID);
    }

    private void __checkDialogSettings(AlertDialog.Builder builder) {
        __checkDialogSettings(builder, /* checkCancelable */ true);
    }

    private void __checkDialogSettings(AlertDialog.Builder builder, boolean checkCancelable) {
        dialogSettings.ensureInitialized();
        if (checkCancelable) {
            if (dialogSettings.cancelable) {
                builder.setCancelable(true);
                builder.setNeutralButton(dialogSettings.getCancelText(), new SimpleActivityOnCancelListener());
            } else {
                builder.setCancelable(false);
            }
        }

        boolean hasIcon = dialogSettings.iconID != null && dialogSettings.iconID > 0;
        if (hasIcon) {
            builder.setIcon(dialogSettings.iconID);
        }

        if (dialogSettings.title != null && !dialogSettings.title.isEmpty()) {
            builder.setTitle(dialogSettings.title);
        } else if (hasIcon) {
            builder.setTitle(" ");
        }
    }

    private void __checkDialogSettings(Dialog dialog) {
        dialogSettings.ensureInitialized();
        if (dialogSettings.cancelable) {
            dialog.setCancelable(true);
            if (dialog instanceof AlertDialog) {
                AlertDialog alertDialog = (AlertDialog) dialog;
                alertDialog.setOnCancelListener(new SimpleActivityOnCancelListener());
            }
        } else {
            dialog.setCancelable(false);
        }

        boolean hasIcon = dialogSettings.iconID != null && dialogSettings.iconID > 0;
        if (hasIcon) {
            if (dialog instanceof AlertDialog) {
                ((AlertDialog) dialog).setIcon(dialogSettings.iconID);
            }
        }

        if (dialogSettings.title != null && !dialogSettings.title.isEmpty()) {
            dialog.setTitle(dialogSettings.title);
        } else if (hasIcon) {
            dialog.setTitle("");
        }
    }

    /*
     * A key listener that simply catches Enter keypresses and gets rid of them.
     * We want this in our input dialogs, else the text fields grow taller when
     * the user presses Enter, which is undesirable.
     */
    private static class EnterKeyPressSuppressor implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // If the event is a key-down event on the "enter" button,
            // consume Enter key press
            return (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER);
        }
    }

    // helper for shared settings for dialog boxes
    private class DialogSettings {
        public String title = null;
        public Integer iconID = null;
        public Integer year = null;
        public Integer month = null;
        public Integer day = null;
        public Integer hour = null;
        public Integer minute = null;
        public Boolean cancelable = false;
        public String okText;
        public String yesText;
        public String noText;
        public String cancelText;
        private boolean initialized = false;

        private void initialize() {
            int titleID = context.getResources().getIdentifier("dialogDefaultTitle", "string", context.getPackageName());
            if (titleID > 0) {
                title = context.getResources().getString(titleID);
            }
            int cancelableID = context.getResources().getIdentifier("dialogCancelable", "string", context.getPackageName());
            if (titleID > 0) {
                cancelable = context.getResources().getBoolean(cancelableID);
            }
            int defaultIconID = context.getResources().getIdentifier("dialogIcon", "string", context.getPackageName());
            if (defaultIconID > 0) {
                iconID = defaultIconID;
            }
            initialized = true;
        }

        public String getOkText() {
            if (okText == null) {
                okText = context.getResources().getString(android.R.string.ok);
            }
            return okText;
        }

        public String getOkText(String candidate) {
            if (candidate == null || candidate.isEmpty()) {
                return getOkText();
            } else {
                return candidate;
            }
        }

        public String getYesText() {
            if (yesText == null) {
                yesText = context.getResources().getString(android.R.string.yes);
                if (yesText.equalsIgnoreCase("ok")) {
                    yesText = "Yes";
                }
            }
            return yesText;
        }

        public String getYesText(String candidate) {
            if (candidate == null || candidate.isEmpty()) {
                return getYesText();
            } else {
                return candidate;
            }
        }

        public String getNoText() {
            if (noText == null) {
                noText = context.getResources().getString(android.R.string.no);
                if (noText.equalsIgnoreCase("cancel")) {
                    noText = "No";
                }
            }
            return noText;
        }

        public String getNoText(String candidate) {
            if (candidate == null || candidate.isEmpty()) {
                return getNoText();
            } else {
                return candidate;
            }
        }

        public String getCancelText() {
            if (cancelText == null) {
                cancelText = context.getResources().getString(android.R.string.cancel);
            }
            return cancelText;
        }

        public String getCancelText(String candidate) {
            if (candidate == null || candidate.isEmpty()) {
                return getCancelText();
            } else {
                return candidate;
            }
        }

        private void ensureInitialized() {
            if (!initialized) {
                initialize();
            }
        }
    }
}
