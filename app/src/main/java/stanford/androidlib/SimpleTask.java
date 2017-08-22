/*
 * @version 2017/02/19
 * - added getStart/End/ElapsedTime
 * @version 2017/02/17
 * - initial version
 */

package stanford.androidlib;

import android.os.AsyncTask;

/**
 * A simplified version of the AsyncTask class that does not require the use of generics
 * or nested classes.  A SimpleTask operates on a collection of string items.
 * If you don't want to use strings, use the regular AsyncTask.
 *
 * <pre>
 * SimpleTask.with(this).execute(item1, item2, ..., itemN);
 * </pre>
 */
public final class SimpleTask extends AsyncTask</* params */ String, /* progress */ Integer, /* result */ Void> {
    /**
     * An interface for the object that actually runs the task.
     * A SimpleActivity or SimpleFragment implements this interface,
     * or you can supply your own anonymous object that implements it.
     */
    public interface TaskExecutor {
        public void onPreExecute();
        public void doInBackground(String... items);
        public void onProgressUpdate(int progress);
        public void onPostExecute();
    }

    private TaskExecutor context = null;
    private long startTime = -1;
    private long endTime = -1;

    /**
     * Returns a singleton SimpleTask instance bound to the given context.
     */
    public static SimpleTask with(SimpleActivity context) {
        SimpleTask task = new SimpleTask();
        task.context = context;
        return task;
    }

    /**
     * Returns a singleton SimpleTask instance bound to the given context.
     */
    public static SimpleTask with(SimpleFragment context) {
        SimpleTask task = new SimpleTask();
        task.context = context;
        return task;
    }

    /**
     * Returns a singleton SimpleTask instance bound to the given context.
     */
    public static SimpleTask with(TaskExecutor context) {
        SimpleTask task = new SimpleTask();
        task.context = context;
        return task;
    }

    /*
     * Private constructor forces client to use with(...).
     */
    private SimpleTask() {
        // empty
    }

    /**
     * Runs the task in the background on the given set of items.
     */
    protected Void doInBackground(String... items) {
        context.doInBackground(items);
        return null;
    }

    /**
     * Returns the amount of time in MS that elapsed while this SimpleTask was running.
     * If the task has not yet finished running, returns -1.
     */
    public long getElapsedTime() {
        if (startTime < 0 || endTime < 0) {
            return -1;
        } else {
            return endTime - startTime;
        }
    }

    /**
     * Returns the MS time at which this task finished running, as a long integer of MS
     * since the epoch (Jan 1 1970 12:00:00am).
     * If the task has not yet finished running, returns -1.
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Returns the MS time at which this task began running, as a long integer of MS
     * since the epoch (Jan 1 1970 12:00:00am).
     * If the task has not yet been started, returns -1.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Called before the task begins.
     * This method logs the start time and calls your activity or context's onPreExecute method.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
        context.onPreExecute();
    }

    /**
     * Called when the task reports that progress has been made via its publishProgress method.
     * This method calls your activity or context's onProgressUpdate(int) method.
     */
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if (progress.length > 0) {
            context.onProgressUpdate(progress[0]);
        }
    }

    /**
     * Called after the task ends.
     * This method logs the end time and calls your activity or context's onPostExecute method.
     */
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        endTime = System.currentTimeMillis();
        context.onPostExecute();
    }

    /**
     * Your task code can call this to indicate that progress has been made.
     */
    public void publishProgress(int progress) {
        super.publishProgress(progress);
    }
}
