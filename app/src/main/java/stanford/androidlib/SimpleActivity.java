/**
 * @version 2016/12/23
 * - bug fix in $/find methods to return T extends View
 * @version 2016/12/22
 * - added layoutID to avoid need for onCreate
 * - added auto-inference of default layout ID
 * - added debug log messages displaying library version
 * - added init() and start() methods like Stanford/ACM Java lib
 * @version 2016/12/13
 * - added $ method (alias of find())
 * @version 2016/03/02
 * - major refactor to break up SimpleActivity god class and incorporate:
 *     SimpleCamera,
 *     SimpleDialog,
 *     SimpleEvents,
 *     SimpleIO,
 *     SimpleList,
 *     SimpleMedia,
 *     SimplePreferences,
 *     SimpleSpeech
 * @version 2016/03/01
 * - added findViewGroup
 * @version 2016/02/28
 * - added networkConnectionExists
 * @version 2016/02/26
 * - tuneups to broadcast receiver methods
 * @version 2016/02/23
 * - added openOrCreateDatabase(String)
 * @version 2016/02/15
 * - added getScreenWidthInches/HeightInches
 * - added getScreenDpi, getScreenDpiX, getScreenDpiY
 * @version 2016/02/03
 * - added getScreenWidth/Height
 * - added hasPermission/requestPermission
 * @version 2016/02/01
 * - added hasPreference, hasSharedPreference
 * - added readUrlText/Lines(URL) but temporarily disabled due to thread issues
 * @version 2016/01/30
 * - refactored ShowXxxDialog methods to use global settings for cancelable and icon
 * - enabled global dialog icon, title, cancelable, etc. settings in res/values/strings.xml
 * @version 2016/01/29
 * - added showDateDialog, showTimeDialog, showProgressDialog
 * - refactored showXxxDialog methods to use DialogSettings
 * - added method annotations for better compile-time safety checking
 * @version 2016/01/28
 * - added showXxxDialog methods
 * @version 2016/01/27
 * - added setTraceLifecycle
 * - handleEnterKeyPress, onEnterKeyPress
 * - media play bug fix
 * - getExtra (serializable)
 * @version 2016/01/26
 * - added fragment methods
 * - added methods to get system photo, music, docs directories
 * - added getAllResourceIds, names, etc.
 * @version 2016/01/25
 * - added isPortrait, isLandscape
 * - added photoGallery
 * - improved JavaDoc comments for all methods
 * @version 2016/01/19
 * - added save/restoreAllFields
 * - added openFileScanner method
 * - added cameraExists method
 */

package stanford.androidlib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.*;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.speech.RecognizerIntent;
import android.support.annotation.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import stanford.androidlib.util.IntentUtils;

/**
 * A SimpleActivity is meant as a drop-in replacement for Android's Activity class.
 * It provides many convenience methods to make basic Android programming easier for students
 * and new developers.  In your activity class, you should write:
 *
 * <pre>
 * public class MyActivity extends SimpleActivity { ... }
 * </pre>
 */
public abstract class SimpleActivity extends AppCompatActivity implements
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
        OnSwipeListener.OnSwipeListenerImpl,
        OnSwipeListener.OnScaleListenerImpl,
        SimpleDialog.DialogListener,
        SimpleEvents.EnterKeyPressListener {

    /// begin class constants

    /**
     * Request code for Intent to take a photo.
     */
    protected static final int REQ_CODE_TAKE_PICTURE = SimpleCamera.REQ_CODE_TAKE_PICTURE;

    /**
     * Request code for Intent to launch photo gallery.
     */
    protected static final int REQ_CODE_PHOTO_GALLERY = SimpleCamera.REQ_CODE_PHOTO_GALLERY;

    /**
     * Request code for Intent to request app permissions.
     */
    protected static final int REQ_CODE_REQUEST_PERMISSIONS = 0x193d & 0xff;   // must be lower 8 bits only (?)

    // set of resources not to return from methods like getAllResourceIds
    // (because they are part of any default app and not specifically your app)
    // (this is a bit of a kludge and will probably be removed in a future version)
    private static final Set<String> RESOURCE_NAMES_TO_FILTER = new HashSet<>(Arrays.asList(
            "abc_ab_share_pack_mtrl_alpha",
            "abc_action_bar_item_background_material",
            "abc_btn_borderless_material",
            "abc_btn_check_material",
            "abc_btn_check_to_on_mtrl_000",
            "abc_btn_check_to_on_mtrl_015",
            "abc_btn_colored_material",
            "abc_btn_default_mtrl_shape",
            "abc_btn_radio_material",
            "abc_btn_radio_to_on_mtrl_000",
            "abc_btn_radio_to_on_mtrl_015",
            "abc_btn_rating_star_off_mtrl_alpha",
            "abc_btn_rating_star_on_mtrl_alpha",
            "abc_btn_switch_to_on_mtrl_00001",
            "abc_btn_switch_to_on_mtrl_00012",
            "abc_cab_background_internal_bg",
            "abc_cab_background_top_material",
            "abc_cab_background_top_mtrl_alpha",
            "abc_control_background_material",
            "abc_dialog_material_background_dark",
            "abc_dialog_material_background_light",
            "abc_edit_text_material",
            "abc_ic_ab_back_mtrl_am_alpha",
            "abc_ic_clear_mtrl_alpha",
            "abc_ic_commit_search_api_mtrl_alpha",
            "abc_ic_go_search_api_mtrl_alpha",
            "abc_ic_menu_copy_mtrl_am_alpha",
            "abc_ic_menu_cut_mtrl_alpha",
            "abc_ic_menu_moreoverflow_mtrl_alpha",
            "abc_ic_menu_paste_mtrl_am_alpha",
            "abc_ic_menu_selectall_mtrl_alpha",
            "abc_ic_menu_share_mtrl_alpha",
            "abc_ic_search_api_mtrl_alpha",
            "abc_ic_voice_search_api_mtrl_alpha",
            "abc_item_background_holo_dark",
            "abc_item_background_holo_light",
            "abc_list_divider_mtrl_alpha",
            "abc_list_focused_holo",
            "abc_list_longpressed_holo",
            "abc_list_pressed_holo_dark",
            "abc_list_pressed_holo_light",
            "abc_list_selector_background_transition_holo_dark",
            "abc_list_selector_background_transition_holo_light",
            "abc_list_selector_disabled_holo_dark",
            "abc_list_selector_disabled_holo_light",
            "abc_list_selector_holo_dark",
            "abc_list_selector_holo_light",
            "abc_menu_hardkey_panel_mtrl_mult",
            "abc_popup_background_mtrl_mult",
            "abc_ratingbar_full_material",
            "abc_scrubber_control_off_mtrl_alpha",
            "abc_scrubber_control_to_pressed_mtrl_000",
            "abc_scrubber_control_to_pressed_mtrl_005",
            "abc_scrubber_primary_mtrl_alpha",
            "abc_scrubber_track_mtrl_alpha",
            "abc_seekbar_thumb_material",
            "abc_seekbar_track_material",
            "abc_spinner_mtrl_am_alpha",
            "abc_spinner_textfield_background_material",
            "abc_switch_thumb_material",
            "abc_switch_track_mtrl_alpha",
            "abc_tab_indicator_material",
            "abc_tab_indicator_mtrl_alpha",
            "abc_text_cursor_material",
            "abc_textfield_activated_mtrl_alpha",
            "abc_textfield_default_mtrl_alpha",
            "abc_textfield_search_activated_mtrl_alpha",
            "abc_textfield_search_default_mtrl_alpha",
            "abc_textfield_search_material",
            "notification_template_icon_bg"
    ));

    // whether to log a message when an expected event handler has not been overwritten
    private static final boolean SHOULD_WARN_ABOUT_MISSING_METHODS = false;

    // semi-global access to app's currently active activity
    private static SimpleActivity currentActivity;

    // next unique view ID to hand out from generateViewId
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    // whether we have log-printed the Stanford Android lib version yet
    private static boolean sPrintedVersionYet = false;

    /**
     * Return the activity that is currently active on the screen.
     * This method is used as a convenience by some other classes like SimpleCanvas.
     */
    static SimpleActivity getCurrentActivity() {
        return currentActivity;
    }

    /**
     * Returns the current version of the library as a string, in format "YYYY/MM/DD HH:MMam".
     */
    public static String getLibraryVersion() {
        return Version.getLibraryVersion();
    }

    /**
     * Generates and returns a unique ID to be programmatically attached to a view.
     */
    @SuppressLint("NewApi")
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    /// begin fields (instance variables)

    private boolean traceLifecycleMethods = false;
    private boolean isContentViewSet = false;   // has setContentView been called?
    private @LayoutRes int layoutID = -1;
    private PowerManager.WakeLock wakeLock = null;

    /// end fields (instance variables)

    // instance initializer (runs before any constructor)
    {
        currentActivity = this;
    }

    /**
     * Constructs a new simple activity with no known layout resource ID.
     * Suggested idiom: In your subclass, write a zero-arg constructor that calls setLayoutID.
     */
    public SimpleActivity() {
        if (!sPrintedVersionYet) {
            sPrintedVersionYet = true;
            final String stars = "******************************************************************************************";
            Log.d("StanfordAndroidLibrary", stars);
            Log.d("StanfordAndroidLibrary", stars);
            Log.d("StanfordAndroidLibrary", "** Stanford Android Library, by Marty Stepp, version " + Version.getLibraryVersion() + "                 **");
            Log.d("StanfordAndroidLibrary", "** For documentation and updates, visit " + Version.getLibraryUrl() + " **");
            Log.d("StanfordAndroidLibrary", stars);
            Log.d("StanfordAndroidLibrary", stars);
        }
    }

    /**
     * Sets the layout to use for this activity as it is being created.
     * This class's version of this method prevents it from being called multiple times.
     * @param layoutID the ID of the layout to use
     */
    @Override
    public void setContentView(@LayoutRes int layoutID) {
        if (!isContentViewSet) {
            super.setContentView(layoutID);
            isContentViewSet = true;
        }
    }

    /**
     * Creates and returns an ArrayAdapter to store the given items.
     */
    @SafeVarargs
    public final <T> ArrayAdapter<String> listCreateAdapter(T... items) {
        return SimpleList.with(this).createAdapter(items);
    }

    /**
     * Creates and returns an ArrayAdapter to store the given items, using the given layout ID and TextView ID.
     */
    @SafeVarargs
    public final <T> ArrayAdapter<String> listCreateAdapter(@LayoutRes int layoutID, @IdRes int textViewID, T... items) {
        return SimpleList.with(this).createAdapter(layoutID, textViewID, items);
    }

    /**
     * Creates and returns an ArrayAdapter to store the given items.
     */
    public <T> ArrayAdapter<T> listCreateAdapter(List<T> list) {
        return SimpleList.with(this).createAdapter(list);
    }

    /**
     * Creates and returns an ArrayAdapter to store the given items, using the given layout ID and TextView ID.
     */
    public <T> ArrayAdapter<T> listCreateAdapter(List<T> list, @LayoutRes int layoutID, @IdRes int textViewID) {
        return SimpleList.with(this).createAdapter(list, layoutID, textViewID);
    }

    /**
     * Returns the text of the items currently in the given list view as an ArrayList.
     */
    public ArrayList<String> listGetItems(AdapterView<?> listView) {
        return SimpleList.with(this).getItems(listView);
    }

    /**
     * Sets the items currently in the given list view to those stored in the given ArrayList.
     */
    public void listSetItems(AdapterView<?> listView, ArrayList<String> items) {
        SimpleList.with(this).setItems(listView, items);
    }

    /**
     * Sets the items currently in the given list view to those passed in the given array.
     */
    public void listSetItems(AdapterView<?> listView, String[] items) {
        SimpleList.with(this).setItems(listView, items);
    }

    /**
     * Sets the items currently in the given list view to those passed.
     */
    public void listSetItems(AdapterView<?> listView, Object... items) {
        SimpleList.with(this).setItems(listView, items);
    }

    /// begin methods for finding various widgets by ID

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T find(@IdRes int id) {
        return (T) super.findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T findById(@IdRes int id) {
        return (T) super.findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public Button $B(@IdRes int id) {
        return (Button) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public Button findButton(@IdRes int id) {
        return (Button) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public CalendarView $CV(@IdRes int id) {
        return (CalendarView) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public CalendarView findCalendarView(@IdRes int id) {
        return (CalendarView) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public CheckBox $CB(@IdRes int id) {
        return (CheckBox) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public CheckBox findCheckBox(@IdRes int id) {
        return (CheckBox) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public DatePicker $DP(@IdRes int id) {
        return (DatePicker) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public DatePicker findDatePicker(@IdRes int id) {
        return (DatePicker) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public EditText $ET(@IdRes int id) {
        return (EditText) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public EditText findEditText(@IdRes int id) {
        return (EditText) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public GridView $GV(@IdRes int id) {
        return (GridView) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public GridView findGridView(@IdRes int id) {
        return (GridView) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public ImageButton $IB(@IdRes int id) {
        return (ImageButton) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public ImageButton findImageButton(@IdRes int id) {
        return (ImageButton) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public ImageView $IV(@IdRes int id) {
        return (ImageView) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public ImageView findImageView(@IdRes int id) {
        return (ImageView) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public ListView $LV(@IdRes int id) {
        return (ListView) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public ListView findListView(@IdRes int id) {
        return (ListView) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public ProgressBar $PB(@IdRes int id) {
        return (ProgressBar) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public ProgressBar findProgressBar(@IdRes int id) {
        return (ProgressBar) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public RadioButton $RB(@IdRes int id) {
        return (RadioButton) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public RadioButton findRadioButton(@IdRes int id) {
        return (RadioButton) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public RadioGroup $RG(@IdRes int id) {
        return (RadioGroup) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public RadioGroup findRadioGroup(@IdRes int id) {
        return (RadioGroup) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public RatingBar $RBar(@IdRes int id) {
        return (RatingBar) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public RatingBar findRatingBar(@IdRes int id) {
        return (RatingBar) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public ScrollView $SCV(@IdRes int id) {
        return (ScrollView) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public ScrollView findScrollView(@IdRes int id) {
        return (ScrollView) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public SearchView $SEV(@IdRes int id) {
        return (SearchView) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public SearchView findSearchView(@IdRes int id) {
        return (SearchView) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public SeekBar $SB(@IdRes int id) {
        return (SeekBar) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public SeekBar findSeekBar(@IdRes int id) {
        return (SeekBar) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public Space $Space(@IdRes int id) {
        return (Space) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public Space findSpace(@IdRes int id) {
        return (Space) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public Spinner $SP(@IdRes int id) {
        return (Spinner) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public Spinner findSpinner(@IdRes int id) {
        return (Spinner) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public StackView $SV(@IdRes int id) {
        return (StackView) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public StackView findStackView(@IdRes int id) {
        return (StackView) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public Switch $SW(@IdRes int id) {
        return (Switch) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public Switch findSwitch(@IdRes int id) {
        return (Switch) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public TextView $TV(@IdRes int id) {
        return (TextView) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public TextView findTextView(@IdRes int id) {
        return (TextView) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public TimePicker $TP(@IdRes int id) {
        return (TimePicker) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public TimePicker findTimePicker(@IdRes int id) {
        return (TimePicker) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public ToggleButton $TB(@IdRes int id) {
        return (ToggleButton) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public ToggleButton findToggleButton(@IdRes int id) {
        return (ToggleButton) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public Toolbar $TBar(@IdRes int id) {
        return (Toolbar) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public Toolbar findToolbar(@IdRes int id) {
        return (Toolbar) findViewById(id);
    }

    /**
     * Returns a ViewGroup of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    @SuppressWarnings("unchecked")
    public <T extends ViewGroup> T findViewGroup(@IdRes int id) {
        return (T) findViewById(id);
    }

    /**
     * An alias for findViewById, but written in such a way that the caller
     * does not need to typecast the returned result.
     */
    @SuppressWarnings("unchecked")
    public ZoomButton $ZB(@IdRes int id) {
        return (ZoomButton) super.findViewById(id);
    }

    /**
     * Returns a widget of the given specific type so that casting is not needed as it would be
     * with findViewById.
     */
    public ZoomButton findZoomButton(@IdRes int id) {
        return (ZoomButton) findViewById(id);
    }

    /// end methods for finding various widgets by ID

    /// begin methods related to fragments

    /**
     * Returns a fragment with the given ID found inside this activity.
     */
    @SuppressWarnings("unchecked")
    public <T extends Fragment> T $F(@IdRes int id) {
        return (T) getFragmentManager().findFragmentById(id);
    }

    /**
     * Returns a fragment with the given ID found inside this activity.
     */
    @SuppressWarnings("unchecked")
    public <T extends Fragment> T findFragment(@IdRes int id) {
        return (T) getFragmentManager().findFragmentById(id);
    }

    /**
     * Returns a fragment with the given ID found inside this activity.
     */
    @SuppressWarnings("unchecked")
    public <T extends Fragment> T findFragmentById(@IdRes int id) {
        return (T) getFragmentManager().findFragmentById(id);
    }

    /**
     * Creates a fragment object from the given class and adds it to this activity
     * in the view container with the given ID.
     * @throws ReflectionRuntimeException if the fragment cannot be constructed.
     */
    public void addFragment(@IdRes int containerID, @NonNull Class<? extends Fragment> fragmentClass) {
        try {
            Fragment fragment = fragmentClass.newInstance();
            addFragment(containerID, fragment);
        } catch (InstantiationException e) {
            throw new ReflectionRuntimeException("Cannot create a new fragment object from class "
                    + fragmentClass.getName() + "; does the class have a parameterless constructor?", e);
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException("Cannot create a new fragment object from class "
                    + fragmentClass.getName() + "; does the class have a parameterless public constructor?", e);
        }
    }

    /**
     * Adds the given fragment to this activity in the view container with the given ID.
     */
    public void addFragment(@IdRes int containerID, @NonNull Fragment fragment) {
        getFragmentManager().beginTransaction()
                .add(containerID, fragment)
                .commit();
    }

    /**
     * Removes the given fragment from this activity.
     * If the given fragment is not present in this activity, there is no effect.
     */
    public void removeFragment(@NonNull Fragment fragment) {
        getFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();
    }

    /**
     * Removes the fragment with the given ID from this activity, if it is contained in this activity.
     * If the given ID does not correspond to any fragment in this activity, there is no effect.
     */
    public void removeFragment(@IdRes int fragmentID) {
        Fragment fragment = findFragment(fragmentID);
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    /**
     * Replaces the fragment in the view container with given ID with a new fragment of the given type.
     * @throws ReflectionRuntimeException if the fragment cannot be constructed.
     */
    public void replaceFragment(@IdRes int containerID, @NonNull Class<? extends Fragment> fragmentClass) {
        try {
            Fragment fragment = fragmentClass.newInstance();
            addFragment(containerID, fragment);
        } catch (InstantiationException e) {
            throw new ReflectionRuntimeException("Cannot create a new fragment object from class "
                    + fragmentClass.getName() + "; does the class have a parameterless constructor?", e);
        } catch (IllegalAccessException e) {
            throw new ReflectionRuntimeException("Cannot create a new fragment object from class "
                    + fragmentClass.getName() + "; does the class have a parameterless public constructor?", e);
        }
    }

    /**
     * Replaces the fragment in the view container with given ID with the given new fragment.
     */
    public void replaceFragment(@IdRes int containerID, @NonNull Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(containerID, fragment)
                .commit();
    }

    /**
     * Hides the given fragment.
     */
    public void hideFragment(@NonNull Fragment fragment) {
        getFragmentManager().beginTransaction().hide(fragment).commit();
    }

    /**
     * Hides the given fragment.
     */
    public void hideFragment(@IdRes int fragmentID) {
        Fragment fragment = findFragment(fragmentID);
        getFragmentManager().beginTransaction().hide(fragment).commit();
    }

    /**
     * Pops up the given dialog fragment as a dialog on top of this activity.
     */
    public void showDialogFragment(@NonNull DialogFragment fragment) {
        FragmentManager fm = getFragmentManager();
        fragment.show(fm, /* tag */ fragment.getClass().getName() + fragment.getTag() + fragment.getId());
    }

    /**
     * Shows the given fragment.
     */
    public void showFragment(@NonNull Fragment fragment) {
        getFragmentManager().beginTransaction().show(fragment).commit();
    }

    /**
     * Shows the given fragment.
     */
    public void showFragment(@IdRes int fragmentID) {
        Fragment fragment = findFragment(fragmentID);
        getFragmentManager().beginTransaction().show(fragment).commit();
    }

    /// end methods related to fragments

    /// begin methods related to app resources

    /**
     * Returns the short name of the resource with the given ID, such as R.drawable.foobar = "foobar".
     */
    public String getResourceName(@IdRes int id) {
        return getResources().getResourceEntryName(id);
    }

    /**
     * Returns the full name of the resource with the given ID, such as R.drawable.foobar = "drawable/foobar".
     * Full resource names take the form "package:type/entry".
     */
    public String getResourceFullName(@IdRes int id) {
        return getResources().getResourceName(id);
    }

    /**
     * Returns a the ID of the resource with the given name and type, such as "foobar", "drawable" = R.drawable.foobar.
     */
    public int getResourceId(@NonNull String name, @NonNull String type) {
        return getResources().getIdentifier(name, type, getPackageName());
    }

    /**
     * Returns a the ID of the resource with the given full name, such as "R.drawable.foobar" = R.drawable.foobar
     * or "@drawable/foobar" = R.drawable.foobar.
     */
    public int getResourceId(@NonNull String fullName) {
        if (fullName.matches("^R\\.[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+$")) {
            // e.g. "R.drawable.foobar"
            fullName = fullName.replace("R.", "");
            String[] parts = fullName.split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid resource name format: \"" + fullName + "\"");
            }
            String type = parts[0];
            String name = parts[1];
            return getResources().getIdentifier(name, type, getPackageName());
        } else if (fullName.matches("^@([a-zA-Z0-9_]:)?\\[a-zA-Z0-9_]+/[a-zA-Z0-9_]+$")) {
            // e.g. "@android:drawable/ic_dialog_email"
            fullName = fullName.substring(1);   // remove @
            String[] parts = fullName.split("[:/]");
            String packageName = parts[0];
            String type = parts[1];
            String name = parts[2];
            return getResources().getIdentifier(name, type, packageName);
        } else {
            throw new IllegalArgumentException("Invalid resource name format: \"" + fullName + "\"");
        }
    }

    /**
     * Returns true if the given resource name maps to a resource in this app.
     * @param fullName a full resource name such as "R.id.foo"
     */
    public boolean hasResource(@NonNull String fullName) {
        int id = getResourceId(fullName);
        return id >= 0;
    }

    /**
     * Returns true if the given resource name/type maps to a resource in this app.
     * @param name a resource name such as "foo" for R.drawable.foo
     * @param type a resource type such as "drawable" for R.drawable.foo
     */
    public boolean hasResource(@NonNull String name, @NonNull String type) {
        int id = getResourceId(name, type);
        return id >= 0;
    }

    /**
     * Returns true if the given resource ID maps to a resource in this app.
     * @param id a resource ID such as R.drawable.foo
     */
    public boolean hasResource(@IdRes int id) {
        String resourceName = getResourceName(id);
        return resourceName != null && !resourceName.isEmpty();
    }

    // whether this field should be filtered out or not by getAllResourceIds
    private boolean shouldFilterField(String fieldName) {
        return RESOURCE_NAMES_TO_FILTER.contains(fieldName)
                || fieldName == null
                || fieldName.startsWith("abc_");
    }

    private List<Integer> getAllResourceIds(@NonNull Class<?> clazz) {
        List<Integer> result = new ArrayList<>();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                int resourceId = field.getInt(null);
                if (!shouldFilterField(field.getName())) {
                    result.add(resourceId);
                }
            } catch (Exception e) {
                // empty
            }
        }
        return result;
    }

    private List<Integer> getAllResourceIds(@NonNull String rClassName) {
        Class<?> thisClass = getClass();
        Package thisPackage = thisClass.getPackage();
        String drawableClassName = thisPackage.getName() + ".R$" + rClassName;
        try {
            Class<?> drawableClass = Class.forName(drawableClassName);
            return getAllResourceIds(drawableClass);
        } catch (Exception e) {
            throw new ReflectionRuntimeException(e);
        }
    }

    private List<String> getAllResourceNames(@NonNull String rClassName) {
        List<Integer> allIds = getAllResourceIds(rClassName);
        List<String> allNames = new ArrayList<>();
        for (int id : allIds) {
            allNames.add(getResourceName(id));
        }
        return allNames;
    }

    /**
     * Returns the IDs of all drawables (images) in this app as a list of integers.
     * If there are no such resources, returns an empty list.
     */
    public List<Integer> getAllDrawableResourceIds() {
        return getAllResourceIds("drawable");
    }

    /**
     * Returns the names of all drawables (images) in this app as a list of strings.
     * For example, if you have images in your res/drawable folder named abc.jpg and defg.png,
     * this method will return the list ["abc", "defg"].
     * If there are no such resources, returns an empty list.
     */
    public List<String> getAllDrawableResourceNames() {
        return getAllResourceNames("drawable");
    }

    /**
     * Returns the IDs of all raw files in this app as a list of integers.
     * If there are no such resources, returns an empty list.
     */
    public List<Integer> getAllRawResourceIds() {
        return getAllResourceIds("raw");
    }

    /**
     * Returns the names of all raw files in this app as a list of strings.
     * For example, if you have images in your res/raw folder named abcd.txt and efg.mp3,
     * this method will return the list ["abcd", "efg"].
     * If there are no such resources, returns an empty list.
     */
    public List<String> getAllRawResourceNames() {
        return getAllResourceNames("raw");
    }

    /**
     * Returns the bitmap image for the resource file with the given ID.
     */
    public Bitmap getBitmap(@DrawableRes int id) {
        return SimpleBitmap.with(this).get(id);
    }

    /**
     * Returns the bitmap image for the file located at the given web URL.
     * @throws IORuntimeException if the URL cannot be read or is not a valid image.
     */
    public Bitmap getBitmap(@NonNull String url) {
        return SimpleBitmap.with(this).get(url);
    }

    /// end methods related to app resources

    /// begin empty event listener methods

    /**
     * Attaches a listener so that this activity's onEnterKeyPress method will be called
     * when the Enter key is pressed on the view with the given ID.
     * Now also supports keyboard cursor movement with arrow keys and Home/End for physical keyboards.
     */
    public void handleEnterKeyPress(@IdRes int viewID) {
        handleEnterKeyPress(findViewById(viewID));
    }

    /**
     * Attaches a listener so that this activity's onEnterKeyPress method will be called
     * when the Enter key is pressed on the given view.
     * Now also supports keyboard cursor movement with arrow keys and Home/End for physical keyboards.
     */
    public void handleEnterKeyPress(@NonNull final View editText) {
        SimpleEvents.with(this).handleEnterKeyPress(editText);
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onEnterKeyPress(View editText) {
        // empty; override me
    }

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
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
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
     * Called when various activities return their results.
     * Subclasses of SimpleActivity that override this method should make sure to call
     * super.onActivityResult(...); to make sure this functionality is not lost.
     */
    @Override
    @CallSuper
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQ_CODE_TAKE_PICTURE) {
            if (resultCode == RESULT_OK && intent != null) {
                Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                onPhotoReady(bitmap);
            }
        } else if (requestCode == SimpleSpeech.REQ_CODE_SPEECH_TO_TEXT) {
            if (resultCode == RESULT_OK && intent != null) {
                ArrayList<String> list = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = list.get(0);
                onSpeechToTextReady(spokenText);
            }
        } else if (requestCode == REQ_CODE_PHOTO_GALLERY) {
            if (resultCode == RESULT_OK && intent != null) {
                Uri targetUri = intent.getData();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    onPhotoReady(bitmap);
                } catch (FileNotFoundException fnfe) {
                    throw new IORuntimeException("SimpleActivity.onActivityResult: unable to decode bitmap from photo gallery", fnfe);
                }
            }
        }
    }

    /// end event listener methods

    /// begin methods related to reading/writing files and directories

    /**
     * Returns the directory where documents are stored on this device.
     */
    public File getDocumentsDirectory() {
        return SimpleIO.with(this).getDocumentsDirectory();
    }

    /**
     * Returns the directory where downloads are stored on this device.
     */
    public File getDownloadsDirectory() {
        return SimpleIO.with(this).getDownloadsDirectory();
    }

    /**
     * Returns the directory where movies are stored on this device.
     */
    public File getMoviesDirectory() {
        return SimpleIO.with(this).getMoviesDirectory();
    }

    /**
     * Returns the directory where music is stored on this device.
     */
    public File getMusicDirectory() {
        return SimpleIO.with(this).getMusicDirectory();
    }

    /**
     * Returns the directory where photos are stored on this device.
     */
    public File getPhotosDirectory() {
        return SimpleIO.with(this).getPhotosDirectory();
    }

    /**
     * Returns the directory where podcasts are stored on this device.
     */
    public File getPodcastsDirectory() {
        return SimpleIO.with(this).getPodcastsDirectory();
    }

    /**
     * Opens and returns a FileInputStream to read the given file in the given
     * external directory.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    // @RequiresPermission("android.permission.READ_EXTERNAL_STORAGE")
    public BufferedReader openExternalFileBufferedReader(String dir, String filename) {
        return SimpleIO.with(this).openExternalFileBufferedReader(dir, filename);
    }

    /**
     * Opens and returns a Scanner to read the given file in the given
     * external directory.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    // @RequiresPermission("android.permission.READ_EXTERNAL_STORAGE")
    public Scanner openExternalFileScanner(String dir, String filename) {
        return SimpleIO.with(this).openExternalFileScanner(dir, filename);
    }

    /**
     * Opens and returns a PrintStream to write the given file in the given
     * external directory.
     * If the file already exists, its contents will be overwritten.
     * @throws IORuntimeException if the file cannot be written.
     */
    // @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public PrintStream openExternalFilePrintStream(String dir, String filename) {
        return SimpleIO.with(this).openExternalFilePrintStream(dir, filename);
    }

    /**
     * Opens and returns a PrintStream to write the given file in the given
     * external directory.
     * The append parameter dictates whether existing file contents will be overwritten or appended to.
     * @throws IORuntimeException if the file cannot be written.
     */
    // @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public PrintStream openExternalFilePrintStream(String dir, String filename, boolean append) {
        return SimpleIO.with(this).openExternalFilePrintStream(dir, filename, append);
    }

    /**
     * Opens and returns a Scanner to read the given file in the given
     * external directory.
     * @throws IORuntimeException if the file cannot be written.
     */
    // @RequiresPermission("android.permission.READ_EXTERNAL_STORAGE")
    public FileInputStream openExternalFileInputStream(String dir, String filename) {
        return SimpleIO.with(this).openExternalFileInputStream(dir, filename);
    }

    /**
     * Opens an external file for reading.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    public FileInputStream openFileInput(@NonNull File file) {
        try {
            return super.openFileInput(file.toString());
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file in the given directory for reading.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    public FileInputStream openFileInput(@NonNull File directory, @NonNull String filename) {
        try {
            return super.openFileInput(new File(directory, filename).toString());
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file for reading.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    @Override
    public FileInputStream openFileInput(@NonNull String filename) {
        try {
            return super.openFileInput(filename);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file for writing in the given mode, such as MODE_PRIVATE or MODE_APPEND.
     * @throws IORuntimeException if the file does not exist or cannot be written.
     */
    public FileOutputStream openFileOutput(@NonNull File file, int mode) {
        try {
            return super.openFileOutput(file.toString(), mode);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file in the given directory for writing in the given mode,
     * such as MODE_PRIVATE or MODE_APPEND.
     * @throws IORuntimeException if the file does not exist or cannot be written.
     */
    public FileOutputStream openFileOutput(@NonNull File directory, @NonNull String name, int mode) {
        try {
            return super.openFileOutput(new File(directory, name).toString(), mode);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file for writing in the given mode, such as MODE_PRIVATE or MODE_APPEND.
     * @throws IORuntimeException if the file does not exist or cannot be written.
     */
    @Override
    public FileOutputStream openFileOutput(@NonNull String name, int mode) {
        try {
            return super.openFileOutput(name, mode);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens a BufferedReader for reading from the given file.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public BufferedReader openInternalFileBufferedReader(@NonNull File file) {
        return SimpleIO.with(this).openInternalFileBufferedReader(file);
    }

    /**
     * Opens a BufferedReader for reading from the given file in the given directory.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public BufferedReader openInternalFileBufferedReader(@NonNull File directory, @NonNull String filename) {
        return SimpleIO.with(this).openInternalFileBufferedReader(directory, filename);
    }

    /**
     * Opens a BufferedReader for reading from the given file.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public BufferedReader openInternalFileBufferedReader(@NonNull String filename) {
        return SimpleIO.with(this).openInternalFileBufferedReader(filename);
    }

    /**
     * Opens a PrintStream for writing into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull File file) {
        return SimpleIO.with(this).openInternalFilePrintStream(file);
    }

    /**
     * Opens a PrintStream for writing into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull File directory, String filename) {
        return SimpleIO.with(this).openInternalFilePrintStream(directory, filename);
    }

    /**
     * Opens a PrintStream for writing into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull String filename) {
        return SimpleIO.with(this).openInternalFilePrintStream(filename);
    }

    /**
     * Opens a PrintStream for writing into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull File file, boolean append) {
        return SimpleIO.with(this).openInternalFilePrintStream(file, append);
    }

    /**
     * Opens a PrintStream for writing into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull File directory, @NonNull String filename, boolean append) {
        return SimpleIO.with(this).openInternalFilePrintStream(directory, filename, append);
    }

    /**
     * Opens a PrintStream for writing into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull String filename, boolean append) {
        return SimpleIO.with(this).openInternalFilePrintStream(filename, append);
    }

    /**
     * Opens a Scanner to read the file with the given name.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public Scanner openInternalFileScanner(@NonNull File file) {
        return SimpleIO.with(this).openInternalFileScanner(file);
    }

    /**
     * Opens a Scanner to read the file with the given name.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public Scanner openInternalFileScanner(@NonNull File directory, @NonNull String filename) {
        return SimpleIO.with(this).openInternalFileScanner(directory, filename);
    }

    /**
     * Opens a Scanner to read the file with the given name.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public Scanner openInternalFileScanner(@NonNull String filename) {
        return SimpleIO.with(this).openInternalFileScanner(filename);
    }

    /**
     * Opens a BufferedReader to read the file with the given ID.
     */
    public BufferedReader openInternalFileBufferedReader(@RawRes int id) {
        return SimpleIO.with(this).openInternalFileBufferedReader(id);
    }

    /**
     * Opens a Scanner to read the file with the given ID.
     */
    public Scanner openInternalFileScanner(@RawRes int id) {
        return SimpleIO.with(this).openInternalFileScanner(id);
    }

    /**
     * Opens an InputStream to read the file with the given ID.
     */
    public InputStream openInternalFileStream(@RawRes int id) {
        return SimpleIO.with(this).openInternalFileStream(id);
    }

    /**
     * Reads the entire text of the file with the given ID, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     */
    public ArrayList<String> readFileLines(@RawRes int id) {
        return SimpleIO.with(this).readFileLines(id);
    }

    /**
     * Reads the entire text of the given file, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the file cannot be read.
     */
    public ArrayList<String> readFileLines(@NonNull File file) {
        return SimpleIO.with(this).readFileLines(file);
    }

    /**
     * Reads the entire text of the given file, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the file cannot be read.
     */
    public ArrayList<String> readFileLines(@NonNull File directory, @NonNull String filename) {
        return SimpleIO.with(this).readFileLines(directory, filename);
    }

    /**
     * Reads the entire text from the given stream, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the file cannot be read.
     */
    public ArrayList<String> readFileLines(@NonNull InputStream stream) {
        return SimpleIO.with(this).readFileLines(stream);
    }

    /**
     * Reads the entire text of the given file, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the file cannot be read.
     */
    public ArrayList<String> readFileLines(@NonNull String filename) {
        return SimpleIO.with(this).readFileLines(filename);
    }

    /**
     * Reads the entire text of the file with the given ID, returning it as a string.
     */
    public String readFileText(@RawRes int id) {
        return SimpleIO.with(this).readFileText(id);
    }

    /**
     * Reads the entire text of the given file, returning it as a string.
     * @throws IORuntimeException if the file cannot be read.
     */
    public String readFileText(@NonNull File file) {
        return SimpleIO.with(this).readFileText(file);
    }

    /**
     * Reads the entire text from the given stream, returning it as a string.
     * @throws IORuntimeException if the file cannot be read.
     */
    public String readFileText(@NonNull InputStream stream) {
        return SimpleIO.with(this).readFileText(stream);
    }

    /**
     * Reads the entire text of the given file, returning it as a string.
     * @throws IORuntimeException if the file cannot be read.
     */
    public String readFileText(@NonNull File directory, @NonNull String filename) {
        return SimpleIO.with(this).readFileText(directory, filename);
    }

    /**
     * Reads the entire text of the given file, returning it as a string.
     * @throws IORuntimeException if the file cannot be read.
     */
    public String readFileText(@NonNull String filename) {
        return SimpleIO.with(this).readFileText(filename);
    }

    // returns true if this string begins with http:, https:, etc.
    // (representing a likely URL)
    private boolean isHttpUrl(String filename) {
        try {
            URL url = new URL(filename);
            return "ftp".equals(url.getProtocol())
                    || "http".equals(url.getProtocol())
                    || "https".equals(url.getProtocol());
        } catch (MalformedURLException mfurle) {
            return false;
        }
    }

    /**
     * Writes the given list of lines into the given file, replacing any previous data.
     * Each line is suffixed by \n.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileLines(@NonNull File file, @NonNull List<String> lines) {
        SimpleIO.with(this).writeFileLines(file, lines);
    }

    /**
     * Writes the given list of lines into the given file, replacing any previous data.
     * Each line is suffixed by \n.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileLines(@NonNull File directory, @NonNull String filename, @NonNull List<String> lines) {
        SimpleIO.with(this).writeFileLines(directory, filename, lines);
    }

    /**
     * Writes the given list of lines into the given file, replacing any previous data.
     * Each line is suffixed by \n.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileLines(@NonNull String filename, @NonNull List<String> lines) {
        SimpleIO.with(this).writeFileLines(filename, lines);
    }

    /**
     * Writes the given text into the given file.
     * Each line is suffixed by \n.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileLines(@NonNull File file, @NonNull List<String> lines, boolean append) {
        SimpleIO.with(this).writeFileLines(file, lines, append);
    }

    /**
     * Writes the given text into the given file.
     * Each line is suffixed by \n.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileLines(@NonNull File directory, @NonNull String filename, @NonNull List<String> lines, boolean append) {
        SimpleIO.with(this).writeFileLines(directory, filename, lines);
    }

    /**
     * Writes the given text into the given file.
     * Each line is suffixed by \n.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileLines(@NonNull String filename, @NonNull List<String> lines, boolean append) {
        SimpleIO.with(this).writeFileLines(filename, lines, append);
    }

    /**
     * Writes the given text into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileText(@NonNull File file, @NonNull String text) {
        SimpleIO.with(this).writeFileText(file, text);
    }

    /**
     * Writes the given text into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileText(@NonNull File directory, @NonNull String filename, @NonNull String text) {
        SimpleIO.with(this).writeFileText(directory, filename, text);
    }

    /**
     * Writes the given text into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileText(@NonNull String filename, @NonNull String text) {
        SimpleIO.with(this).writeFileText(filename, text);
    }

    /**
     * Writes the given text into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileText(@NonNull File file, @NonNull String text, boolean append) {
        SimpleIO.with(this).writeFileText(file, text, append);
    }

    /**
     * Writes the given text into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileText(@NonNull File directory, @NonNull String filename, @NonNull String text, boolean append) {
        SimpleIO.with(this).writeFileText(directory, filename, text, append);
    }

    /**
     * Writes the given text into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public void writeFileText(@NonNull String filename, @NonNull String text, boolean append) {
        SimpleIO.with(this).writeFileText(filename, text, append);
    }

    /// end methods related to reading/writing files

    /// begin methods related to 'extras', parameters passed in as this activity was invoked

    /**
     * Returns true if this activity was launched with a non-null intent.
     */
    public boolean hasIntent() {
        return getIntent() != null;
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns false.
     */
    public boolean getBooleanExtra(@NonNull String name) {
        return getBooleanExtra(name, /* defaultValue */ false);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public boolean getBooleanExtra(@NonNull String name, boolean defaultValue) {
        Intent intent = getIntent();
        return intent.getBooleanExtra(name, defaultValue);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns 0.0.
     */
    public double getDoubleExtra(@NonNull String name) {
        return getDoubleExtra(name, /* defaultValue */ 0.0);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public double getDoubleExtra(@NonNull String name, double defaultValue) {
        Intent intent = getIntent();
        return intent.getFloatExtra(name, (float) defaultValue);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns 0.
     */
    public int getIntExtra(@NonNull String name) {
        return getIntExtra(name, /* defaultValue */ 0);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public int getIntExtra(@NonNull String name, int defaultValue) {
        Intent intent = getIntent();
        return intent.getIntExtra("name", defaultValue);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns 0.
     */
    public long getLongExtra(@NonNull String name) {
        return getLongExtra(name, /* defaultValue */ 0L);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public long getLongExtra(@NonNull String name, long defaultValue) {
        Intent intent = getIntent();
        return intent.getLongExtra("name", defaultValue);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns an empty string.
     */
    public String getStringExtra(@NonNull String name) {
        return getStringExtra(name, /* defaultValue */ "");
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    public String getStringExtra(@NonNull String name, String defaultValue) {
        Intent intent = getIntent();
        String result = intent.getStringExtra(name);
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns null.
     */
    public <T> T getExtra(@NonNull String name) {
        return getExtra(name, /* defaultValue */ null);
    }

    /**
     * Returns an 'extra' parameter with the given name from this activity's intent.
     * If there is no such 'extra' parameter, returns the given default value.
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtra(@NonNull String name, T defaultValue) {
        Intent intent = getIntent();
        Object result = intent.getSerializableExtra(name);
        if (result == null) {
            return defaultValue;
        } else {
            return (T) result;
        }
    }

    /// end methods related to 'extras', parameters passed in as this activity was invoked

    /// begin methods related to app preferences

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns false.
     */
    public boolean getPreferenceBoolean(@NonNull String name) {
        return SimplePreferences.with(this).getBoolean(name);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public boolean getPreferenceBoolean(@NonNull String name, boolean defaultValue) {
        return SimplePreferences.with(this).getBoolean(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.0.
     */
    public double getPreferenceDouble(@NonNull String name) {
        return SimplePreferences.with(this).getDouble(name);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public double getPreferenceDouble(@NonNull String name, double defaultValue) {
        return SimplePreferences.with(this).getDouble(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.
     */
    public int getPreferenceInt(@NonNull String name) {
        return SimplePreferences.with(this).getInt(name);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public int getPreferenceInt(@NonNull String name, int defaultValue) {
        return SimplePreferences.with(this).getInt(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.
     */
    public long getPreferenceLong(@NonNull String name) {
        return SimplePreferences.with(this).getLong(name);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public long getPreferenceLong(@NonNull String name, long defaultValue) {
        return SimplePreferences.with(this).getLong(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns an empty string.
     */
    public String getPreferenceString(@NonNull String name) {
        return SimplePreferences.with(this).getString(name);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public String getPreferenceString(@NonNull String name, String defaultValue) {
        return SimplePreferences.with(this).getString(name, defaultValue);
    }

    /**
     * Returns true if this activity contains a preference with the given name.
     */
    public boolean hasPreference(@NonNull String name) {
        return SimplePreferences.with(this).has(name);
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public void setPreference(@NonNull String name, boolean value) {
        SimplePreferences.with(this).set(name, value);
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public void setPreference(@NonNull String name, double value) {
        SimplePreferences.with(this).set(name, value);
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public void setPreference(@NonNull String name, int value) {
        SimplePreferences.with(this).set(name, value);
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public void setPreference(@NonNull String name, long value) {
        SimplePreferences.with(this).set(name, value);
    }

    /**
     * Sets a preference with the given name and value into the app's global preferences.
     */
    public void setPreference(@NonNull String name, String value) {
        SimplePreferences.with(this).set(name, value);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns false.
     */
    public boolean getSharedPreferenceBoolean(@NonNull String filename, @NonNull String name) {
        return SimplePreferences.with(this).getSharedBoolean(filename, name);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public boolean getSharedPreferenceBoolean(@NonNull String filename, @NonNull String name, boolean defaultValue) {
        return SimplePreferences.with(this).getSharedBoolean(filename, name, defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns 0.0.
     */
    public double getSharedPreferenceDouble(@NonNull String filename, @NonNull String name) {
        return SimplePreferences.with(this).getSharedDouble(filename, name);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public double getSharedPreferenceDouble(@NonNull String filename, @NonNull String name, double defaultValue) {
        return SimplePreferences.with(this).getSharedDouble(filename, name, defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns 0.
     */
    public int getSharedPreferenceInt(@NonNull String filename, @NonNull String name) {
        return SimplePreferences.with(this).getSharedInt(filename, name);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public int getSharedPreferenceInt(@NonNull String filename, @NonNull String name, int defaultValue) {
        return SimplePreferences.with(this).getSharedInt(filename, name, defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns 0.
     */
    public long getSharedPreferenceLong(@NonNull String filename, @NonNull String name) {
        return SimplePreferences.with(this).getSharedLong(filename, name);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public long getSharedPreferenceLong(@NonNull String filename, @NonNull String name, long defaultValue) {
        return SimplePreferences.with(this).getSharedLong(filename, name, defaultValue);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns an empty string.
     */
    public String getSharedPreferenceString(@NonNull String filename, @NonNull String name) {
        return SimplePreferences.with(this).getSharedString(filename, name);
    }

    /**
     * Returns the shared preference with the given name and value from the given shared preference filename.
     * If there is no such shared preference, returns the given default value.
     */
    public String getSharedPreferenceString(@NonNull String filename, @NonNull String name, String defaultValue) {
        return SimplePreferences.with(this).getSharedString(filename, name, defaultValue);
    }

    /**
     * Returns true if this activity contains a shared preference with the given name
     * in the given shared preference filename.
     */
    public boolean hasSharedPreference(@NonNull String filename, @NonNull String name) {
        return SimplePreferences.with(this).hasShared(filename, name);
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public void setSharedPreference(@NonNull String filename, @NonNull String name, boolean value) {
        SimplePreferences.with(this).setShared(filename, name, value);
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public void setSharedPreference(@NonNull String filename, @NonNull String name, double value) {
        SimplePreferences.with(this).setShared(filename, name, value);
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public void setSharedPreference(@NonNull String filename, @NonNull String name, int value) {
        SimplePreferences.with(this).setShared(filename, name, value);
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public void setSharedPreference(@NonNull String filename, @NonNull String name, long value) {
        SimplePreferences.with(this).setShared(filename, name, value);
    }

    /**
     * Sets a shared preference with the given name and value into the given shared preference filename.
     */
    public void setSharedPreference(@NonNull String filename, @NonNull String name, String value) {
        SimplePreferences.with(this).setShared(filename, name, value);
    }

    /// end methods related to app preferences

    /// begin methods related to activity lifecycle

    /**
     * Stores all fields' values into the given bundle.
     */
    public void saveAllFields(@NonNull Bundle bundle) {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            boolean canSave = fieldType.isPrimitive() || Serializable.class.isAssignableFrom(fieldType);
            if (!canSave) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value != null) {
                    bundle.putSerializable(field.getName(), (Serializable) value);
                }
            } catch (IllegalAccessException iae) {
                Log.wtf("saveAllFields", "unable to save field '" + field.getName() + "'", iae);
            }
        }
    }

    /**
     * Extracts all fields' values from the given bundle and stores them back into those fields.
     */
    public void restoreAllFields(@NonNull Bundle bundle) {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            boolean canSave = fieldType.isPrimitive() || Serializable.class.isAssignableFrom(fieldType);
            if (!canSave) {
                continue;
            }
            try {
                if (bundle.containsKey(field.getName())) {
                    field.setAccessible(true);
                    Object value = bundle.getSerializable(field.getName());
                    if (value != null) {
                        field.set(this, value);
                    }
                }
            } catch (IllegalAccessException iae) {
                Log.wtf("restoreAllFields", "unable to restore field '" + field.getName() + "'", iae);
            }
        }
    }

    private boolean __fieldCanBeSaved(@NonNull Field field) {
        Class<?> fieldType = field.getType();

        // don't save View objects' state
        if (View.class.isAssignableFrom(fieldType)) {
            return false;
        }

        // save primitives and Serializable objects
        if (!fieldType.isPrimitive()
                && !Serializable.class.isAssignableFrom(fieldType)
                && !Parcelable.class.isAssignableFrom(fieldType)) {
            return false;
        }

        // don't save static or final variables
        int mod = fieldType.getModifiers();
        return !(Modifier.isStatic(mod) || Modifier.isFinal(mod));
    }

    private void traceLifecycleLog(@NonNull String method) {
        traceLifecycleLog(method, "");
    }

    private void traceLifecycleLog(@NonNull String method, @NonNull String message) {
        if (traceLifecycleMethods) {
            String className = getClass().getName();
            int dot = className.lastIndexOf(".");
            if (dot >= 0) {
                className = className.substring(dot + 1);
            }
            Log.i("SimpleActivity", className + " #" + this.hashCode() + " " + method
                    + "(" + message + ")");
        }
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        traceLifecycleLog("onCreate", "bundle=" + savedInstanceState);
        super.onCreate(savedInstanceState);
        if (this.layoutID >= 0) {
            setContentView(layoutID);
        } else if (!isContentViewSet) {
            String layoutGuessIdStr = getDefaultLayoutIdName();
            int layoutID = getResourceId(layoutGuessIdStr);
            if (layoutID > 0) {
                this.layoutID = layoutID;
                setContentView(layoutID);
            }
        }
        init();
    }

    /**
     * Code to run when activity is being created.
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

    // e.g. MySimpleCoolActivity => "R.layout.activity_my_simple_cool"
    private String getDefaultLayoutIdName() {
        String className = this.getClass().getSimpleName();
        className = className.replaceAll("Activity$", "");   // "MySimpleCool"
        String layoutName = "R.layout.activity_";
        for (int i = 0; i < className.length(); i++) {
            char ch = className.charAt(i);
            if (i == 0) {
                layoutName += Character.toLowerCase(ch);
            } else {
                // precede uppercase camel letters by underscore
                char prev = className.charAt(i - 1);
                if (Character.isLowerCase(prev) && Character.isUpperCase(ch)) {
                    layoutName += "_";
                }
                layoutName += Character.toLowerCase(ch);
            }
        }
        return layoutName;
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    protected void onDestroy() {
        traceLifecycleLog("onDestroy");
        super.onDestroy();
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    protected void onPause() {
        traceLifecycleLog("onPause");
        super.onPause();
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    protected void onRestart() {
        traceLifecycleLog("onRestart");
        super.onRestart();
    }

    /**
     * Called when this activity saves its instance state; if the @AutoSaveFields annotation is
     * present on your activity class, automatically loads the value of your class's fields.
     */
    @Override
    @CallSuper
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        traceLifecycleLog("onRestoreInstanceState", "bundle=" + savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
        if (isAutoSaveFields()) {
            restoreAllFields(savedInstanceState);
        }
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    protected void onResume() {
        traceLifecycleLog("onResume");
        super.onResume();
    }

    /**
     * Called when this activity saves its instance state; if the @AutoSaveFields annotation is
     * present on your activity class, automatically saves the value of your class's fields.
     */
    @Override
    @CallSuper
    protected void onSaveInstanceState(Bundle outState) {
        traceLifecycleLog("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (isAutoSaveFields()) {
            saveAllFields(outState);
        }
    }

    /**
     * Activity lifecycle method.
     */
    @Override
    @CallSuper
    protected void onStart() {
        traceLifecycleLog("onStart");
        super.onStart();
        start();
    }

    /**
     * Stops the activity, performing some needed cleanup such as shutting down the text-to-speech
     * system if it was in use.
     */
    @Override
    @CallSuper
    protected void onStop() {
        traceLifecycleLog("onStop");
        super.onStop();
        SimpleSpeech.with(this).shutdown();
    }

    /**
     * Sets whether or not to print a log message every time an activity lifecycle
     * method such as onPause or onStart is called.
     */
    public void setTraceLifecycle(boolean trace) {
        traceLifecycleMethods = trace;
    }

    /**
     * Returns true if your activity class has the @AutoSaveFields annotation.
     */
    protected boolean isAutoSaveFields() {
        Class<?> clazz = getClass();
        return clazz.isAnnotationPresent(AutoSaveFields.class);
    }

    /**
     * Returns true if the device is in landscape orientation.
     */
    public boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Returns true if the device is in portrait orientation.
     */
    public boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /// end methods related to activity lifecycle

    /// begin methods related to starting other activities and services

    /**
     * Shuts down this activity, returning RESULT_OK as its result code and the given 'extra' parameters.
     */
    public void finish(Object... parameters) {
        finish(/* resultCode */ RESULT_OK, parameters);
    }

    /**
     * Shuts down this activity, returning the given result code and the given 'extra' parameters.
     */
    public void finish(int resultCode, Object... parameters) {
        Intent closeIntent = new Intent();
        IntentUtils.putParameters(closeIntent, parameters);
        setResult(resultCode, closeIntent);
        super.finish();
    }

    /**
     * Starts the given activity and passes it the given 'extra' parameters.
     */
    public void startActivity(@NonNull Class<? extends Activity> activityClass, Object... parameters) {
        __startActivityHelper(activityClass, /* forResult */ false, /* requestCode */ -1, parameters);
    }

    /**
     * Starts the given activity with the given request code and passes it the given 'extra' parameters.
     */
    public void startActivityForResult(@NonNull Class<? extends Activity> activityClass, int requestCode, Object... parameters) {
        __startActivityHelper(activityClass, /* forResult */ true, requestCode, parameters);
    }

    private void __startActivityHelper(@NonNull Class<? extends Activity> activityClass,
                                       boolean forResult, int requestCode, Object... parameters) {
        // unpack and store parameters
        Intent intent = new Intent(this, activityClass);
        IntentUtils.putParameters(intent, parameters);

        if (forResult) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(intent);
        }
    }

    /**
     * Starts the given service and passes it the given 'extra' parameters.
     */
    public void startService(@NonNull Class<? extends Service> serviceClass, Object... parameters) {
        startServiceAction(serviceClass, null, parameters);
    }

    /**
     * Starts the given service and passes it the given 'extra' parameters.
     */
    public void startServiceAction(@NonNull Class<? extends Service> serviceClass, String action, Object... parameters) {
        // unpack and store parameters
        Intent intent = new Intent(this, serviceClass);
        if (action != null && !action.isEmpty()) {
            intent.setAction(action);
        }
        IntentUtils.putParameters(intent, parameters);
        startService(intent);
    }

    /// end methods related to starting other activities and services

    /// begin methods related to broadcast receivers

    /**
     * Registers this activity as a broadcast receiver for the given action(s).
     * If a service makes a broadcast with the given action, this activity's
     * onBroadcastReceived method will be called.
     * You should override onBroadcastReceived to handle the broadcast.
     */
    public void registerReceiver(String... actions) {
        if (actions.length > 0) {
            IntentFilter filter = new IntentFilter();
            for (String action : actions) {
                filter.addAction(action);
            }
            super.registerReceiver(new SimpleBroadcastReceiver(), filter);
        }
    }

    // helper class for receiving broadcasts and forwarding them to the activity
    private class SimpleBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SimpleActivity.this.onBroadcastReceived(intent);
        }
    }

    /**
     * Override this method to handle broadcasts registered through {@code registerReceiver(String)}.
     * If you register this activity as a broadcast receiver, this activity's
     * onBroadcastReceived method will be called.
     */
    public void onBroadcastReceived(Intent intent) {
        // empty; override me
    }

    /// end methods related to broadcast receivers

    /// begin sound methods

    /**
     * Returns whether the media clip with the given ID is currently playing in 'loop' mode.
     */
    public boolean soundIsLooping(@RawRes int id) {
        return SimpleMedia.with(this).isLooping(id);
    }

    /**
     * Returns whether the media clip with the given ID is currently playing or looping.
     */
    public boolean soundIsPlaying(@RawRes int id) {
        return SimpleMedia.with(this).isPlaying(id);
    }

    /**
     * Plays the media clip with the given ID in 'looping' mode.
     */
    public void soundLoop(@RawRes int id) {
        SimpleMedia.with(this).loop(id);
    }

    /**
     * Plays the media clip with the given ID.
     */
    public void soundPlay(@RawRes int id) {
        SimpleMedia.with(this).play(id);
    }

    /**
     * Pauses the media clip with the given ID if it is playing.
     */
    public void soundPause(@RawRes int id) {
        SimpleMedia.with(this).pause(id);
    }

    /**
     * Returns the time position in milliseconds for the media clip with the given ID if it is playing.
     */
    public int soundGetPosition(@RawRes int id) {
        return SimpleMedia.with(this).getPosition(id);
    }

    /**
     * Sets the time position in milliseconds for the media clip with the given ID.
     * If the clip was not playing, it starts playing.
     */
    public void soundSetPosition(@RawRes int id, int position) {
        SimpleMedia.with(this).setPosition(id, position);
    }

    /**
     * Stops the media clip with the given ID if it was playing.
     */
    public void soundStop(@RawRes int id) {
        SimpleMedia.with(this).stop(id);
    }

    /**
     * Says the given text aloud using Text-to-speech.
     * If any other speech is occurring, it is halted and this new speech begins immediately.
     */
    public void speak(@NonNull String text) {
        SimpleSpeech.with(this).speak(text);
    }

    /**
     * Says the given text aloud using Text-to-speech.
     * If the 'immediately' boolean parameter is true, and if any other speech is occurring,
     * it is halted and this new speech begins immediately.
     * If the 'immediately' parameter is false, this text is spoken after any other text
     * previously sent to speak() is done being spoken.
     */
    @SuppressWarnings("deprecation")
    public void speak(@NonNull final String text, final boolean immediately) {
        SimpleSpeech.with(this).speak(text, immediately);
    }

    /**
     * Returns true if the current device supports text-to-speech capability.
     */
    public boolean textToSpeechSupported() {
        return SimpleSpeech.with(this).textToSpeechSupported();
    }

    /**
     * Asks the device to start recording speech and converting it to text.
     * The given prompt text is shown on the screen to prompt the user to speak.
     * When the spoken words are done converting to text, the method onSpeechToTextReady
     * will be called.  Subclasses should override that method to grab the spoken text
     * as a string.
     * Returns silently if the action failed (e.g. if this device cannot support speech-to-text).
     */
    public void speechToText(@NonNull String prompt) {
        SimpleSpeech.with(this).speechToText(prompt);
    }

    /**
     * Returns true if the current device supports speech-to-text recognition.
     */
    public boolean speechToTextSupported() {
        return SimpleSpeech.with(this).speechToTextSupported();
    }

    /**
     * Override this method to indicate what to do when a speech-to-text action is done
     * recording the user's voice and translating it to text.
     * The spoken text will be passed to this method as a string.
     */
    public void onSpeechToTextReady(String spokenText) {
        // empty; override me
    }

    /// end sound methods

    /// begin system service methods

    /**
     * Launches the system default phone dialer application to make a call to the given
     * telephone number.
     * Requires the permission android.permission.CALL_PHONE to work.
     */
    // @RequiresPermission(Manifest.permission.CALL_PHONE)
    public void dial(@NonNull String phoneNumber) {
        Uri number = Uri.parse("tel:" + phoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }


    /**
     * Launches the system default mapping application to display a map centered about the given
     * latitude / longitude coordinates at the default zoom level of 14.
     */
    public void map(double lat, double lng) {
        map(lat, lng, /* zoom */ 14);
    }

    /**
     * Launches the system default mapping application to display a map centered about the given
     * latitude / longitude coordinates at the given zoom level.
     */
    public void map(double lat, double lng, int zoom) {
        // open a map pointing at a given latitude/longitude (z=zoom)
        Uri location = Uri.parse("geo:" + lat + "," + lng + "?z=" + zoom);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(mapIntent);
    }

    /**
     * Launches the system default text messaging application to send a message to the given
     * telephone number.
     * Requires the permission android.permission.SEND_SMS to work.
     */
    // @RequiresPermission(Manifest.permission.SEND_SMS)
    public void textMessage(@NonNull String phoneNumber) {
        Uri uri = Uri.fromParts("sms", phoneNumber, null);
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(smsIntent);
    }

    /**
     * Launches the system default text messaging application to send a message to the given
     * telephone number with the given message body text.
     * Requires the permission android.permission.SEND_SMS to work.
     */
    // @RequiresPermission(Manifest.permission.SEND_SMS)
    public void textMessage(@NonNull String phoneNumber, @NonNull String messageBody) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body", messageBody);
        startActivity(smsIntent);
    }

    /**
     * Launches the system default web browser to display the web page at the given URL.
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    public void webBrowser(@NonNull String webPageUrl) {
        // go to a web page in the default browser
        Uri webpage = Uri.parse(webPageUrl);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(webIntent);
    }

    /// end system service methods

    /// begin log/print/toast convenience methods

    /**
     * Prints a debug (.d) log message containing the given text.
     */
    public void log(Object message) {
        Log.d("SimpleActivity log", String.valueOf(message));
    }

    /**
     * Prints a debug (.d) log message containing the given text.
     */
    public void log(String message) {
        Log.d("SimpleActivity log", message);
    }

    /**
     * Prints a WTF (.wtf) log message containing the given text and exception.
     */
    public void log(String message, Throwable exception) {
        Log.wtf("SimpleActivity log", message, exception);
    }

    /**
     * Prints a WTF (.wtf) log message containing the given exception.
     */
    public void log(Throwable exception) {
        Log.wtf("SimpleActivity log", "exception was thrown", exception);
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
        Log.v("SimpleActivity println", String.valueOf(message));
    }

    /**
     * Prints a verbose (.v) log message containing the given text.
     */
    public void println(String message) {
        Log.v("SimpleActivity println", message);
    }

    /// end log/print/toast convenience methods

    /// begin methods related to camera and photos

    /**
     * Returns true if the device has a microphone.
     */
    public boolean microphoneExists() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    /**
     * Returns true if the device has an audio output.
     */
    public boolean audioOutExists() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT);
        } else {
            return getPackageManager().hasSystemFeature("android.hardware.audio.output");
        }
    }

    /**
     * Returns true if the device has Bluetooth capability.
     */
    public boolean bluetoothExists() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    /**
     * Returns true if the device has a camera.
     */
    public boolean cameraExists() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Returns true if the device currently has a working internet connection,
     * as reported by the system connectivity service.
     */
    @RequiresPermission(allOf = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE})
    public boolean networkConnectionExists() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Returns true if the device currently has a working wifi connection,
     * as reported by the system connectivity service.
     * If you just want to see whether any connection to the internet exists,
     * use networkConnectionExists instead.
     */
    @RequiresPermission(allOf = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE})
    @SuppressWarnings("deprecation")
    public boolean wifiConnectionExists() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Returns true if the device currently has a working mobile internet connection,
     * as reported by the system connectivity service.
     * If you just want to see whether any connection to the internet exists,
     * use networkConnectionExists instead.
     */
    @RequiresPermission(allOf = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE})
    @SuppressWarnings("deprecation")
    public boolean mobileConnectionExists() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Instructs the device to take a photo without saving it to a file.
     * The onPhotoReady method will be called when the photo is ready.
     * Silently returns if the action failed (e.g. if this device has no camera).
     */
    // @RequiresPermission(Manifest.permission.CAMERA)
    public void takePhoto() {
        SimpleCamera.with(this).takePhoto();
    }

    /**
     * Instructs the device to take a photo and save it to a file.
     * The onPhotoReady method will be called when the photo is ready.
     */
    // @RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void takePhoto(String filename) {
        SimpleCamera.with(this).takePhoto(filename);
    }

    /**
     * Launches the device's photo gallery activity.
     * Once the photo has been chosen, the onPhotoReady method will be called.
     * Note that your app needs the permissions android.permission.MANAGE_DOCUMENTS and
     * android.permission.READ_EXTERNAL_STORAGE to read photos from the photo gallery.
     */
    // @RequiresPermission("android.permission.READ_EXTERNAL_STORAGE")
    public void photoGallery() {
        SimpleCamera.with(this).photoGallery();
    }

    /**
     * Override this method to indicate what to do when a photo is done being taken or chosen.
     * The photo will be passed to this method as a Bitmap object.
     */
    // @RequiresPermission(Manifest.permission.CAMERA)
    public void onPhotoReady(Bitmap bitmap) {
        // empty; override me
    }

    /// end methods related to camera and photos

    /// begin notification/toast methods

    /**
     * Pops up a short Toast notification to display the given text.
     */
    public void toast(Object text) {
        toast(String.valueOf(text));
    }

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToastLength {
        // empty
    }


    /**
     * Pops up a Toast notification of the given time length to display the given text.
     * The length must be Toast.LENGTH_SHORT or Toast.LENGTH_LONG.
     */
    public void toast(Object text, @ToastLength int length) {
        toast(String.valueOf(text), length);
    }

    /**
     * Pops up a Toast notification of the given time length to display the given text.
     */
    public void toast(String text) {
        toast(text, /* length */ Toast.LENGTH_SHORT);
    }

    /**
     * Pops up a Toast notification of the given time length to display the given text.
     * The length must be Toast.LENGTH_SHORT or Toast.LENGTH_LONG.
     */
    public void toast(String text, @ToastLength int length) {
        Toast.makeText(this, text, length).show();
    }

    /// end notification/toast methods

    /// begin methods related to "wake lock" (stopping screen from locking)

    /**
     * Sets wake lock to be enabled (true) or disabled (false).
     * If true, turns on a wake lock so that your device's screen will not lock until
     * further notice while your app is running.
     * If false, turns off wake lock if it was previously enabled;
     * if wake lock is not enabled, there is no effect.
     * Note that your app needs the permission android.permission.WAKE_LOCK
     * to request a wake lock.
     */
    // @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void setWakeLock(boolean wakeLockEnabled) {
        if (wakeLockEnabled) {
            if (wakeLock == null) {
                PowerManager pwr = (PowerManager) getSystemService(POWER_SERVICE);
                wakeLock = pwr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "SimpleActivity lock");
                wakeLock.acquire();
            }
        } else {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        }
    }

    /**
     * Returns true if the wake lock is currently enabled.
     */
    public boolean wakeLockIsEnabled() {
        return wakeLock != null;
    }

    /**
     * Sets whether the app should hide its title bar and show itself in full-screen mode (true)
     * or have a standard app title bar (false).
     */
    public void setFullScreenMode(boolean fullScreen) {
        if (fullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    /// begin methods related to dialogs

    /**
     * Shows an alert dialog to display a message to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you when they press OK.
     */
    public AlertDialog showAlertDialog(String message) {
        return SimpleDialog.with(this).showAlertDialog(message);
    }

    /**
     * Shows an alert dialog to display a message to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you when they press OK.
     */
    public AlertDialog showAlertDialog(@StringRes int messageID) {
        return SimpleDialog.with(this).showAlertDialog(messageID);
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onAlertDialogCancel if they press No.
     */
    public AlertDialog showConfirmDialog(String message) {
        return SimpleDialog.with(this).showConfirmDialog(message);
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onAlertDialogCancel if they press No.
     */
    public AlertDialog showConfirmDialog(String message,
                                         String positiveButtonText,
                                         String negativeButtonText) {
        return SimpleDialog.with(this).showConfirmDialog(message, positiveButtonText, negativeButtonText);
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onAlertDialogCancel if they press No.
     */
    public AlertDialog showConfirmDialog(@StringRes int messageID) {
        return SimpleDialog.with(this).showConfirmDialog(messageID);
    }

    /**
     * Shows an confirm dialog to ask a yes/no question to the user.
     * When the user is done with the dialog, your activity's onAlertDialogClose method
     * will be called to notify you if they press Yes, or onDialogNegativeClick if they press No.
     */
    public AlertDialog showConfirmDialog(@StringRes int messageID,
                                         @StringRes int positiveButtonTextID,
                                         @StringRes int negativeButtonTextID) {
        return SimpleDialog.with(this).showConfirmDialog(messageID, positiveButtonTextID, negativeButtonTextID);
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(String message) {
        return SimpleDialog.with(this).showInputDialog(message);
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(String message, String positiveButtonText) {
        return SimpleDialog.with(this).showInputDialog(message, positiveButtonText);
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(@StringRes int messageID) {
        return SimpleDialog.with(this).showInputDialog(messageID);
    }

    /**
     * Shows an input dialog to prompt the user for a single text input string.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showInputDialog(@StringRes int messageID,
                                       @StringRes int positiveButtonTextID) {
        return SimpleDialog.with(this).showInputDialog(messageID, positiveButtonTextID);
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     */
    public AlertDialog showMultiInputDialog(String... prompts) {
        return SimpleDialog.with(this).showMultiInputDialog(prompts);
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     */
    public AlertDialog showMultiInputDialogWithMessage(String message, String... prompts) {
        return SimpleDialog.with(this).showMultiInputDialogWithMessage(message, prompts);
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     */
    public AlertDialog showMultiInputDialog(@ArrayRes int promptsID) {
        return SimpleDialog.with(this).showMultiInputDialog(promptsID);
    }

    /**
     * Shows a multiple-input dialog to prompt the user for multiple text input strings.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of strings that were typed by the user.
     */
    public AlertDialog showMultiInputDialog(@StringRes int messageID, @ArrayRes int promptsID) {
        return SimpleDialog.with(this).showMultiInputDialog(messageID, promptsID);
    }

    /**
     * Shows a list input dialog to allow the user to tap one of several choices.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showListInputDialog(final String... items) {
        return SimpleDialog.with(this).showListInputDialog(items);
    }

    /**
     * Shows a list input dialog to allow the user to tap one of several choices.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the string that was typed by the user.
     */
    public AlertDialog showListInputDialog(@ArrayRes int itemsID) {
        return SimpleDialog.with(this).showListInputDialog(itemsID);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     * No items will be initially checked.
     */
    public AlertDialog showCheckboxInputDialog(final String... items) {
        return SimpleDialog.with(this).showCheckboxInputDialog(items);
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
        return SimpleDialog.with(this).showCheckboxInputDialog(checkedItems, items);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     */
    public AlertDialog showCheckboxInputDialog(@ArrayRes int itemsID) {
        return SimpleDialog.with(this).showCheckboxInputDialog(itemsID);
    }

    /**
     * Shows a multiple-checkbox dialog to prompt the user to check/uncheck any subset
     * of a group of multiple checkbox input items.
     * When the user is done with the dialog, your activity's onMultiInputDialogClose method
     * will be called, passing an array of item strings that were checked by the user.
     * Pass an array of booleans to indicate which items are initially checked, if any.
     */
    public AlertDialog showCheckboxInputDialog(boolean[] checkedItems, @ArrayRes int itemsID) {
        return SimpleDialog.with(this).showCheckboxInputDialog(checkedItems, itemsID);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     */
    public AlertDialog showRadioInputDialog(String... items) {
        return SimpleDialog.with(this).showRadioInputDialog(items);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     * Pass an integer to represent the index that should be initially checked.
     */
    public AlertDialog showRadioInputDialog(int checkedIndex, final String... items) {
        return SimpleDialog.with(this).showRadioInputDialog(checkedIndex, items);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     */
    public AlertDialog showRadioInputDialog(@ArrayRes int itemsID) {
        return SimpleDialog.with(this).showRadioInputDialog(itemsID);
    }

    /**
     * Shows a radio button dialog to prompt the user to check exactly one
     * of a group of multiple radio button input items.
     * When the user is done with the dialog, your activity's onInputDialogClose method
     * will be called, passing the item string that was checked by the user.
     * Pass an integer to represent the index that should be initially checked.
     */
    public AlertDialog showRadioInputDialog(int checkedIndex, @ArrayRes int itemsID) {
        return SimpleDialog.with(this).showRadioInputDialog(checkedIndex, itemsID);
    }

    /**
     * Shows a time picker dialog to prompt the user to select a time of day.
     * When the user is done with the dialog, your activity's onTimeInputDialogClose method
     * will be called, passing the hour and minute that were chosen by the user.
     */
    public TimePickerDialog showTimeInputDialog() {
        return SimpleDialog.with(this).showTimeInputDialog();
    }

    /**
     * Shows a time picker dialog to prompt the user to select a time of day.
     * When the user is done with the dialog, your activity's onTimeInputDialogClose method
     * will be called, passing the hour and minute that were chosen by the user.
     */
    public TimePickerDialog showTimeInputDialog(String message) {
        return SimpleDialog.with(this).showTimeInputDialog(message);
    }

    /**
     * Shows a time picker dialog to prompt the user to select a time of day.
     * When the user is done with the dialog, your activity's onTimeInputDialogClose method
     * will be called, passing the hour and minute that were chosen by the user.
     */
    public TimePickerDialog showTimeInputDialog(int startHour, int startMinute) {
        return SimpleDialog.with(this).showTimeInputDialog(startHour, startMinute);
    }

    /**
     * Shows a time picker dialog to prompt the user to select a time of day.
     * When the user is done with the dialog, your activity's onTimeInputDialogClose method
     * will be called, passing the hour and minute that were chosen by the user.
     */
    public TimePickerDialog showTimeInputDialog(String message, int startHour, int startMinute) {
        return SimpleDialog.with(this).showTimeInputDialog(message, startHour, startMinute);
    }


    /**
     * Shows a date picker dialog to prompt the user to select a date.
     * When the user is done with the dialog, your activity's onDateInputDialogClose method
     * will be called, passing the year, month, and day (1-based) that were chosen by the user.
     */
    public DatePickerDialog showDateInputDialog() {
        return SimpleDialog.with(this).showDateInputDialog();
    }

    /**
     * Shows a date picker dialog to prompt the user to select a date.
     * When the user is done with the dialog, your activity's onDateInputDialogClose method
     * will be called, passing the year, month, and day (1-based) that were chosen by the user.
     */
    public DatePickerDialog showDateInputDialog(String message) {
        return SimpleDialog.with(this).showDateInputDialog(message);
    }

    /**
     * Shows a date picker dialog to prompt the user to select a date.
     * When the user is done with the dialog, your activity's onDateInputDialogClose method
     * will be called, passing the year, month, and day (1-based) that were chosen by the user.
     */
    public DatePickerDialog showDateInputDialog(int startYear, int startMonth, int startDay) {
        return SimpleDialog.with(this).showDateInputDialog(startYear, startMonth, startDay);
    }

    /**
     * Shows a date picker dialog to prompt the user to select a date.
     * When the user is done with the dialog, your activity's onDateInputDialogClose method
     * will be called, passing the year, month, and day (1-based) that were chosen by the user.
     */
    public DatePickerDialog showDateInputDialog(String message, int startYear, int startMonth, int startDay) {
        return SimpleDialog.with(this).showDateInputDialog(message, startYear, startMonth, startDay);
    }

    /**
     * Shows a progress dialog with a progress bar.
     * The dialog shows a default message of "Working ..."
     * The dialog progresses up to a default max value of 100.
     */
    public SimpleProgressDialog showProgressDialog() {
        return SimpleDialog.with(this).showProgressDialog();
    }

    /**
     * Shows a progress dialog with a progress bar.
     * The dialog progresses up to a default max value of 100.
     */
    public SimpleProgressDialog showProgressDialog(String message) {
        return SimpleDialog.with(this).showProgressDialog(message);
    }

    /**
     * Shows a progress dialog with a progress bar.
     * The given max is the highest value the progress bar can reach; once
     * it reaches that point, the client should presumably tell the progress
     * dialog to dismiss() itself.
     * Pass a max &lt;= 0 for an indeterminate dialog that does not have a specific maximum.
     */
    public SimpleProgressDialog showProgressDialog(String message, int max) {
        return SimpleDialog.with(this).showProgressDialog(message, max);
    }

    /**
     * Globally sets whether dialogs created with show_Xxx_Dialog(...)
     * will display a Cancel button.
     */
    public void setDialogsCancelable(boolean cancelable) {
        SimpleDialog.with(this).setDialogsCancelable(cancelable);
    }

    /**
     * Globally sets whether dialogs created with show_Xxx_Dialog(...)
     * to display the given icon.
     * Initially by default no icon will be shown on any dialogs.
     */
    public void setDialogsIcon(@DrawableRes int iconID) {
        SimpleDialog.with(this).setDialogsIcon(iconID);
    }

    /**
     * Globally sets the title that dialogs created with show_Xxx_Dialog(...)
     * should display.
     * Initially by default no title will be shown.
     * Set to empty string or null to remove the title.
     */
    public void setDialogsTitle(String title) {
        SimpleDialog.with(this).setDialogsTitle(title);
    }

    /**
     * Globally sets the title that dialogs created with show_Xxx_Dialog(...)
     * should display.
     * Initially by default no title will be shown.
     * Set to empty string or null to remove the title.
     */
    public void setDialogsTitle(@StringRes int titleID) {
        SimpleDialog.with(this).setDialogsTitle(titleID);
    }

    // Helper that prints a warning message about a method
    // that probably should exist in the client's activity.
    private void __warnAboutMissingMethod(String text) {
        if (SHOULD_WARN_ABOUT_MISSING_METHODS) {
            Log.w("SimpleActivity", "You need to write a method " + text
                    + " with proper signature in your activity class.");
        }
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onAlertDialogClose(AlertDialog dialog) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onDateInputDialogClose(View view, int year, int month, int day) {
        // empty; override me
        __warnAboutMissingMethod("onDateInputDialogClose");
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onInputDialogClose(AlertDialog dialog, String input) {
        // empty; override me
        __warnAboutMissingMethod("onInputDialogClose");
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onMultiInputDialogClose(AlertDialog dialog, String[] inputs) {
        // empty; override me
        __warnAboutMissingMethod("onMultiInputDialogClose");
    }

    public void onProgressDialogClose(ProgressDialog dialog) {
        // empty; override me
        // no warning about missing method here because you might not want to respond
        // to the dialog being closed
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onTimeInputDialogClose(View view, int hour, int minute) {
        // empty; override me
        __warnAboutMissingMethod("onTimeInputDialogClose");
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onDialogNegativeClick(DialogInterface dialog) {
        // empty; override me
    }

    /**
     * Empty event listener method to be overridden.
     */
    public void onDialogCancel(DialogInterface dialog) {
        // empty; override me
    }

    /// end methods related to dialogs

    /// begin methods related to screen size / resolution / density

    /**
     * Returns the width of the current Android device's screen, in pixels.
     */
    public int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Returns the height of the current Android device's screen, in pixels.
     */
    public int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * Returns the 'real' width of the current Android device's screen, in pixels.
     * This is the width without any title bars, borders, etc. that would otherwise
     * surround the app.
     */
    public int getScreenRealWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        } else {
            display.getSize(size);
        }
        return size.x;
    }

    /**
     * Returns the 'real' height of the current Android device's screen, in pixels.
     * This is the height without any title bars, borders, etc. that would otherwise
     * surround the app.
     */
    public int getScreenRealHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        } else {
            display.getSize(size);
        }
        return size.y;
    }

    /**
     * Returns the current device's screen density in dots-per-inch (DPI), rounded down
     * to the nearest integer.
     */
    public int getScreenDpi() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.densityDpi;
    }

    /**
     * Returns the current device's screen density in the x dimension
     * in dots-per-inch (DPI), rounded down to the nearest integer.
     * This is often but not always the same as the result from {@code getScreenDpi},
     * depending on the device.
     */
    public int getScreenDpiX() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) metrics.xdpi;
    }

    /**
     * Returns the current device's screen density in the x dimension
     * in dots-per-inch (DPI), rounded down to the nearest integer.
     * This is often but not always the same as the result from {@code getScreenDpi},
     * depending on the device.
     */
    public int getScreenDpiY() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) metrics.ydpi;
    }

    /**
     * Returns a string representing the current device's screen density,
     * based on the following mapping:
     *
     * <ul>
     *     <li>up to 120 dpi: "ldpi"
     *     <li>120 - 160 dpi: "mdpi"
     *     <li>160 - 240 dpi: "hdpi"
     *     <li>240 - 320 dpi: "xhdpi"
     *     <li>320 - 480 dpi: "xxhdpi"
     *     <li>above 480 dpi: "xxxhdpi"
     * </ul>
     */
    public String getScreenDensity() {
        int dpi = getScreenDpi();
        if (dpi <= DisplayMetrics.DENSITY_LOW) {
            return "ldpi";
        } else if (dpi <= DisplayMetrics.DENSITY_MEDIUM) {
            return "mdpi";
        } else if (dpi <= DisplayMetrics.DENSITY_HIGH) {
            return "hdpi";
        } else if (dpi <= DisplayMetrics.DENSITY_XHIGH) {
            return "xhdpi";
        } else if (dpi <= DisplayMetrics.DENSITY_XXHIGH) {
            return "xxhdpi";
        } else {  // if (dpi <= DisplayMetrics.DENSITY_XXXHIGH) {
            return "xxxhdpi";
        }
    }

    /**
     * Returns the device's physical screen width in inches,
     * based on its screen resolution in pixels and its density in DPI.
     */
    public float getScreenWidthInches() {
        return (float) getScreenRealWidth() / getScreenDpiX();
    }

    /**
     * Returns the device's physical screen height in inches,
     * based on its screen resolution in pixels and its density in DPI.
     */
    public float getScreenHeightInches() {
        return (float) getScreenRealHeight() / getScreenDpiY();
    }

    /**
     * Returns the device's physical screen diagonal size in inches,
     * based on its screen resolution in pixels and its density in DPI.
     */
    public float getScreenSizeInches() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        } else {
            display.getSize(size);
        }
        double x = Math.pow(size.x / dm.xdpi, 2);
        double y = Math.pow(size.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return (float) screenInches;
    }

    /// end methods related to screen size / resolution / density

    /// begin permission and security methods

    /**
     * Returns true if our application has the given kind of permission,
     * such as Manifest.permission.CAMERA.
     */
    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Pops up a request for our application to acquire the given kind of permission(s),
     * such as Manifest.permission.CAMERA.
     * The activity uses a request code of REQ_CODE_REQUEST_PERMISSIONS.
     */
    public void requestPermission(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, REQ_CODE_REQUEST_PERMISSIONS);
    }

    /**
     * Checks whether the current app has all of the given permissions;
     * if it does not have any, pops up a request for our application to
     * acquire them.
     * The activity uses a request code of REQ_CODE_REQUEST_PERMISSIONS.
     */
    public void ensurePermission(String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                ActivityCompat.requestPermissions(this, permissions, REQ_CODE_REQUEST_PERMISSIONS);
                break;
            }
        }
    }

    /// end permission and security methods

    /// begin layout inflater methods

    /**
     * Returns the resource ID of the layout to use for this activity, as passed to the
     * constructor or setLayoutID.
     */
    public @LayoutRes int getLayoutID() {
        return layoutID;
    }

    /**
     * Sets the resource ID of the layout to use for this activity.
     * Does not re-lay-out the activity.
     */
    public void setLayoutID(@LayoutRes int layoutID) {
        this.layoutID = layoutID;
    }

    /**
     * A shortcut for getting the layout inflater and asking it to inflate
     * the layout with the given ID, with a null root/parent parameter.
     * The return type is generic so that you do not need to cast it into
     * any View subclass.
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T inflateLayout(@LayoutRes int id) {
        return (T) getLayoutInflater().inflate(id, /* root */ null);
    }

    /// end layout inflater methods

    /// begin methods for location/GPS

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_CODE_REQUEST_PERMISSIONS);
            }
            return null;
        }
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            // fall back to network if GPS is not available
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return loc;
    }

    /// end methods for location/GPS

    /// begin methods related to databases

    /**
     * Opens or creates a database with the given name in MODE_PRIVATE mode.
     */
    public SQLiteDatabase openOrCreateDatabase(String name) {
        return openOrCreateDatabase(name, MODE_PRIVATE, /* cursor factory */ null);
    }

    /**
     * Returns true if a database with the given name exists.
     */
    public boolean databaseExists(String name) {
        File path = getDatabasePath(name);
        return path != null && path.exists();
    }

    /// end methods related to databases

    /**
     * Instructs Android to allow your app to perform networking operations on the main thread.
     * Normally trying to do networking on the main thread would cause an exception to be
     * thrown because it can lock up the app's UI.  Calling this method disables that restriction.
     * Note that performing networking operations on the main thread is not good practice
     * and should be done only for temporary debugging purposes.
     */
    public void allowNetworkingOnMainThread() {
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
