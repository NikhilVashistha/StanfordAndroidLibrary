/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

/**
 * A version of ProgressDialog that auto-closes when it reaches 100% progress.
 */
public class SimpleProgressDialog extends ProgressDialog {
    /**
     * Constructs a new dialog.
     */
    public SimpleProgressDialog(Context context) {
        super(context);
    }

    /**
     * Constructs a new dialog.
     */
    public SimpleProgressDialog(View context) {
        super(context.getContext());
    }

    /**
     * Increases progress by the given amount.
     * If it reaches its max amount, the dialog closes.
     */
    @Override
    public void incrementProgressBy(int amount) {
        super.incrementProgressBy(amount);
        if (isFinished()) {
            dismiss();
        }
    }

    /**
     * Returns true if the dialog's progress has reached its max.
     * Always returns false for indeterminate dialogs because by nature
     * we do not know when they are done.
     */
    public boolean isFinished() {
        return !isInProgress();
    }

    /**
     * Returns true if the dialog's progress has not yet reached its max,
     * or true if the dialog is indeterminate.
     */
    public boolean isInProgress() {
        return isIndeterminate() || getMax() == 0 || getProgress() < getMax();
    }

    /**
     * Sets progress to the given value.
     * If it reaches its max amount, the dialog closes.
     */
    @Override
    public void setProgress(int value) {
        boolean wasFinished = isFinished();
        super.setProgress(value);
        if (!wasFinished && isFinished() && isShowing()) {
            dismiss();
        }
    }
}

