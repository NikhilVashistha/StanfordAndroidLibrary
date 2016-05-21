/*
 * @version 2016/02/26
 * - initial version
 */

package stanford.androidlib;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.*;

import stanford.androidlib.util.IntentUtils;

/**
 * This class is a drop-in replacement for Notification.Builder that adds a few methods for convenience.
 * Notably you can call .send(id); to build and send the notification without having to explicitly
 * grab the NotificationManager service.
 *
 * <p>
 *     This class is not currently well documented.
 *     It will be documented fully in a future release.
 * </p>
 */
public class SimpleNotification extends NotificationCompat.Builder {
    /**
     * Returns a singleton SimpleNotification instance bound to the given context.
     */
    public static SimpleNotification with(Context context) {
        return new SimpleNotification(context);
    }

    /**
     * Returns a singleton SimpleNotification instance bound to the given view's context.
     */
    public static SimpleNotification with(View context) {
        return with(context.getContext());
    }

    private Context context;
    private int id;

    private SimpleNotification(Context context) {
        super(context);
        this.context = context;

        // some defaults
        setAutoCancel(true);
    }

    public SimpleNotification setID(int id) {
        this.id = id;
        return this;
    }

    public SimpleNotification send() {
        return send(id);
    }

    @SuppressWarnings("deprecation")
    public SimpleNotification send(int id) {
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = build();
        } else {
            notification = getNotification();
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
        return this;
    }

    public SimpleNotification setTitle(String title) {
        return setContentTitle(title);
    }

    public SimpleNotification setText(String text) {
        return setContentText(text);
    }

    public SimpleNotification setInfo(String info) {
        return setContentInfo(info);
    }

    public SimpleNotification setIntent(Class<? extends Activity> activityClass, Object... params) {
        return setIntent(activityClass, /* requestCode */ 0, params);
    }

    public SimpleNotification setIntent(Class<? extends Activity> activityClass, int requestCode, Object... params) {
        setContentIntent(IntentUtils.with(context).makePendingIntent(activityClass, requestCode, params));
        return this;
    }

    public SimpleNotification setContentIntent(Class<? extends Activity> activityClass, Object... params) {
        return setContentIntent(activityClass, /* requestCode */ 0, params);
    }

    public SimpleNotification setContentIntent(Class<? extends Activity> activityClass, int requestCode, Object... params) {
        return setIntent(activityClass, requestCode, params);
    }

    public SimpleNotification setDeleteIntent(Class<? extends Activity> activityClass, Object... params) {
        setDeleteIntent(IntentUtils.with(context).makePendingIntent(activityClass, params));
        return this;
    }

    public SimpleNotification setDeleteIntent(Class<? extends Activity> activityClass, int requestCode, Object... params) {
        setDeleteIntent(IntentUtils.with(context).makePendingIntent(activityClass, requestCode, params));
        return this;
    }

    /// begin methods that are wrappers of other methods that accept various resources,
    /// modified to take resource IDs for easier coding or localization

    public SimpleNotification setTitle(@StringRes int id) {
        return setContentTitle(context.getResources().getString(id));
    }

    public SimpleNotification setContentTitle(@StringRes int id) {
        return setContentTitle(context.getResources().getString(id));
    }

    public SimpleNotification setText(@StringRes int id) {
        return setContentText(context.getResources().getString(id));
    }

    public SimpleNotification setContentText(@StringRes int id) {
        return setContentText(context.getResources().getString(id));
    }

    public SimpleNotification setSubText(@StringRes int id) {
        return setSubText(context.getResources().getString(id));
    }

    public SimpleNotification setInfo(@StringRes int id) {
        return setContentInfo(context.getResources().getString(id));
    }

    public SimpleNotification setContentInfo(@StringRes int id) {
        return setContentInfo(context.getResources().getString(id));
    }

    public SimpleNotification setTicker(@StringRes int id) {
        return setTicker(context.getResources().getString(id));
    }

    public SimpleNotification setLargeIcon(@DrawableRes int icon) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        return setLargeIcon(bitmap);
    }

    public SimpleNotification setContent(@LayoutRes int id) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), id);
        return setContent(remoteViews);
    }

    public SimpleNotification setLayout(@LayoutRes int id) {
        return setContent(id);
    }

    /// end methods that are wrappers of methods that accept resources

    /// begin action methods

    public SimpleNotification addAction(@DrawableRes int icon, CharSequence title, PendingIntent intent) {
        super.addAction(new NotificationCompat.Action.Builder(icon, title, intent).build());
        return this;
    }

    public SimpleNotification addAction(@DrawableRes int icon, @StringRes int titleID, PendingIntent intent) {
        String title = context.getResources().getString(titleID);
        return addAction(icon, title, intent);
    }

    public SimpleNotification addAction(@DrawableRes int icon, CharSequence title, Class<? extends Activity> activityClass, Object... params) {
        PendingIntent intent = IntentUtils.with(context).makePendingIntent(activityClass, params);
        return addAction(icon, title, intent);
    }

    public SimpleNotification addAction(@DrawableRes int icon, CharSequence title, Class<? extends Activity> activityClass, int requestCode, Object... params) {
        PendingIntent intent = IntentUtils.with(context).makePendingIntent(activityClass, requestCode, params);
        return addAction(icon, title, intent);
    }

    public SimpleNotification addAction(@DrawableRes int icon, @StringRes int titleID, Class<? extends Activity> activityClass, Object... params) {
        PendingIntent intent = IntentUtils.with(context).makePendingIntent(activityClass, params);
        return addAction(icon, titleID, intent);
    }

    public SimpleNotification addAction(@DrawableRes int icon, @StringRes int titleID, Class<? extends Activity> activityClass, int requestCode, Object... params) {
        PendingIntent intent = IntentUtils.with(context).makePendingIntent(activityClass, requestCode, params);
        return addAction(icon, titleID, intent);
    }

    /// end action methods

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public SimpleNotification addAction(Notification.Action action) {
        super.addAction(new NotificationCompat.Action(action.icon, action.title, action.actionIntent));
        return this;
    }

    /// begin methods copy/pasted from Notification.Builder

    @Override
    public SimpleNotification setWhen(long when) {
        super.setWhen(when);
        return this;
    }

    @Override
    public SimpleNotification setShowWhen(boolean show) {
        super.setShowWhen(show);
        return this;
    }

    @Override
    public SimpleNotification setUsesChronometer(boolean b) {
        super.setUsesChronometer(b);
        return this;
    }

    @Override
    public SimpleNotification setSmallIcon(@DrawableRes int icon) {
        super.setSmallIcon(icon);
        return this;
    }

    @Override
    public SimpleNotification setSmallIcon(@DrawableRes int icon, int level) {
        super.setSmallIcon(icon, level);
        return this;
    }

//    @Override
//    public SimpleNotification setSmallIcon(Icon icon) {
//        super.setSmallIcon(icon);
//        return this;
//    }

    @Override
    public SimpleNotification setContentTitle(CharSequence title) {
        super.setContentTitle(title);
        return this;
    }

    @Override
    public SimpleNotification setContentText(CharSequence text) {
        super.setContentText(text);
        return this;
    }

    @Override
    public SimpleNotification setSubText(CharSequence text) {
        super.setSubText(text);
        return this;
    }

    @Override
    public SimpleNotification setNumber(int number) {
        super.setNumber(number);
        return this;
    }

    @Override
    public SimpleNotification setContentInfo(CharSequence info) {
        super.setContentInfo(info);
        return this;
    }

    @Override
    public SimpleNotification setProgress(int max, int progress, boolean indeterminate) {
        super.setProgress(max, progress, indeterminate);
        return this;
    }

    @Override
    public SimpleNotification setContent(RemoteViews views) {
        super.setContent(views);
        return this;
    }

    @Override
    public SimpleNotification setContentIntent(PendingIntent intent) {
        super.setContentIntent(intent);
        return this;
    }

    @Override
    public SimpleNotification setDeleteIntent(PendingIntent intent) {
        super.setDeleteIntent(intent);
        return this;
    }

    @Override
    public SimpleNotification setFullScreenIntent(PendingIntent intent, boolean highPriority) {
        super.setFullScreenIntent(intent, highPriority);
        return this;
    }

    @Override
    public SimpleNotification setTicker(CharSequence tickerText) {
        super.setTicker(tickerText);
        return this;
    }

    @Override
    public SimpleNotification setLargeIcon(Bitmap b) {
        super.setLargeIcon(b);
        return this;
    }

//    @Override
//    public SimpleNotification setLargeIcon(Icon icon) {
//        super.setLargeIcon(icon);
//        return this;
//    }

    @Override
    public SimpleNotification setSound(Uri sound) {
        super.setSound(sound);
        return this;
    }

//    public SimpleNotification setSound(Uri sound, AudioAttributes audioAttributes) {
//        super.setSound(sound, audioAttributes);
//        return this;
//    }

    @Override
    public SimpleNotification setVibrate(long[] pattern) {
        super.setVibrate(pattern);
        return this;
    }

    @Override
    public SimpleNotification setLights(int argb, int onMs, int offMs) {
        super.setLights(argb, onMs, offMs);
        return this;
    }

    @Override
    public SimpleNotification setOngoing(boolean ongoing) {
        super.setOngoing(ongoing);
        return this;
    }

    public SimpleNotification setOnlyAlertOnce(boolean onlyAlertOnce) {
        super.setOnlyAlertOnce(onlyAlertOnce);
        return this;
    }

    @Override
    public SimpleNotification setAutoCancel(boolean autoCancel) {
        super.setAutoCancel(autoCancel);
        return this;
    }

    @Override
    public SimpleNotification setLocalOnly(boolean localOnly) {
        super.setLocalOnly(localOnly);
        return this;
    }

    @Override
    public SimpleNotification setDefaults(int defaults) {
        super.setDefaults(defaults);
        return this;
    }

    @Override
    public SimpleNotification setPriority(int pri) {
        super.setPriority(pri);
        return this;
    }

    @Override
    public SimpleNotification setCategory(String category) {
        super.setCategory(category);
        return this;
    }

    @Override
    public SimpleNotification addPerson(String uri) {
        super.addPerson(uri);
        return this;
    }

    @Override
    public SimpleNotification setGroup(String groupKey) {
        super.setGroup(groupKey);
        return this;
    }

    @Override
    public SimpleNotification setGroupSummary(boolean isGroupSummary) {
        super.setGroupSummary(isGroupSummary);
        return this;
    }

    @Override
    public SimpleNotification setSortKey(String sortKey) {
        super.setSortKey(sortKey);
        return this;
    }

    @Override
    public SimpleNotification addExtras(Bundle extras) {
        super.addExtras(extras);
        return this;
    }

    @Override
    public SimpleNotification setExtras(Bundle extras) {
        super.setExtras(extras);
        return this;
    }

    @Override
    public SimpleNotification addAction(NotificationCompat.Action action) {
        super.addAction(action);
        return this;
    }

    @Override
    public SimpleNotification setStyle(NotificationCompat.Style style) {
        super.setStyle(style);
        return this;
    }

    @Override
    public SimpleNotification setVisibility(int visibility) {
        super.setVisibility(visibility);
        return this;
    }

    @Override
    public SimpleNotification setPublicVersion(Notification n) {
        super.setPublicVersion(n);
        return this;
    }

    @Override
    public SimpleNotification extend(NotificationCompat.Extender extender) {
        super.extend(extender);
        return this;
    }

    @Override
    public SimpleNotification setColor(@ColorInt int argb) {
        super.setColor(argb);
        return this;
    }
}
