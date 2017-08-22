/*
 * SimpleFirebase library, by Marty Stepp
 *
 * This library is intended to make it easier to use the Firebase remote database system.
 * This incomplete library resource is a work in progress.
 * Please report any bugs/issues to the author.
 *
 * Documentation available at:
 * - http://web.stanford.edu/class/cs193a/lib/
 *
 * @version 2017/02/27
 * - added version-printing code
 */

package stanford.androidlib.data.firebase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.tasks.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

/**
 * This class provides a simplified interface to the Firebase remote database system.
 * Methods require the context such as an {@code Activity}, so you
 * must call {@code SimpleFirebase.with(yourActivity)} first.
 *
 * <p>
 * By design of Firebase, most methods are asynchronous
 * ({@code get}, {@code set}, {@code signIn}, {@code push}, {@code watch});
 * they return immediately and will notify your code when their results are available
 * by contacting an appropriate listener.
 *
 * <p>
 * The most common intended usage pattern is to make your activity implement {@code GetListener},
 * {@code SetListener}, {@code SignInListener}, etc. or to write small inner classes that implement
 * these interfaces.
 * We recommend you also implement {@code ErrorListener} so that you can see when operations fail on
 * your database, or at least call {@code setLogging(true)} to turn on a default error listener.
 *
 * @author Marty Stepp (stepp AT stanford)
 * @version 2017/02/27
 * - added version-print on init
 * @version 2017/02/26
 * - added auto-incrementing ID key support
 * - added transaction support
 * - added sign-in using auth tokens
 * @version 2017/02/21
 * - initial version (CS 193A 17wi)
 */
public final class SimpleFirebase {
    /**
     * An event listener that can respond to database errors.
     */
    public interface ErrorListener {
        public void onError(DatabaseError error);
    }

    /**
     * An event listener that can respond to the result of {@code get()} calls.
     */
    public interface GetListener {
        public void onGet(String path, DataSnapshot data);
    }

    /**
     * An event listener that can respond to the result of {@code push()} or {@code pushById()} calls.
     */
    public interface PushListener {
        public void onPush(String path, DatabaseReference ref);
    }

    /**
     * An event listener that can respond to the result of {@code set()} calls.
     */
    public interface SetListener {
        public void onSet(String path);
    }

    /**
     * An event listener that can respond to the result of {@code signIn()} calls.
     */
    public interface SignInListener {
        public void onSignIn(boolean successful);
    }

    /**
     * An event listener that can respond to the result of {@code transaction()} calls.
     */
    public interface TransactionListener {
        public void onTransaction(String path, MutableData mdata);
    }

    /**
     * An event listener that can respond to the result of {@code watch()} calls.
     */
    public interface WatchListener {
        public void onDataChange(DataSnapshot data);
    }

    // tag for debug logging
    private static final String LOG_TAG = "SimpleFirebase";

    // whether the Firebase db has been initialized
    private static boolean sInitialized = false;

    private Context context;                         // activity/fragment used to load resources
    private FirebaseAuth mAuth = null;               // authentication/signin object
    private FirebaseUser user = null;                // currently signed in user (null if none)
    private ErrorListener errorListener;             // responds to database errors (null if none)
    private DatabaseError lastDatabaseError = null;  // last database error that occurred (null if none)
    private String lastQueryPath = null;             // last string/Query from get()/etc.
    private Query lastQuery = null;
    private boolean signInComplete = false;          // true if finished signing in to db
    private boolean logging = false;                 // true if we should Log various debug messages

    /**
     * Returns a new SimpleFirebase instance using the given activity or other context.
     */
    public static SimpleFirebase with(Context context) {
        SimpleFirebase fb = new SimpleFirebase();
        fb.context = context;

        if (!sInitialized) {
            synchronized (SimpleFirebase.class) {
                if (!sInitialized) {
                    FirebaseApp.initializeApp(context);
                    sInitialized = true;

                    final String stars = "******************************************************************************************";
                    Log.d(LOG_TAG, "");
                    Log.d(LOG_TAG, stars);
                    Log.d(LOG_TAG, stars);
                    Log.d(LOG_TAG, "** Stanford SimpleFirebase Library, by Marty Stepp, version " + Version.getLibraryVersion() + "          **");
                    // Log.d(LOG_TAG, "** For documentation and updates, visit " + stanford.androidlib.Version.getLibraryUrl() + " **");
                    Log.d(LOG_TAG, stars);
                    Log.d(LOG_TAG, stars);
                }
            }
        }

        return fb;
    }

    /*
     * Private constructor forbids direct construction.
     * Use SimpleFirebase.with(...) instead.
     */
    private SimpleFirebase() {
        // empty
    }

    /**
     * Returns a child of the overall database; equivalent to Firebase's {@code child()} method
     * or the {@code SimpleFirebase} {@code query()} method.
     * @see SimpleFirebase#query(String)
     */
    public DatabaseReference child(String queryText) {
        return query(queryText);
    }

    /**
     * Clears this object's record of any past database error.
     * If there was no past error, has no effect.
     */
    public void clearLastDatabaseError() {
        lastDatabaseError = null;
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code GetListener} interface, it will be notified when the data arrives.
     * @param path absolute database path such as "foo/bar/baz"
     */
    public SimpleFirebase get(String path) {
        return get(path, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given {@code GetListener} will be notified when the data has arrived.
     * @param path absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase get(String path, final GetListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = fb.child(path);
        return getWatchHelper(path, child, listener, /* watch */ false);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code GetListener} interface, it will be notified when the data arrives.
     * @param ref a {@code Query} object containing an absolute database reference
     */
    public SimpleFirebase get(Query ref) {
        return get(ref, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given {@code GetListener} will be notified when the data has arrived.
     * @param ref a {@code Query} object containing an absolute database reference
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase get(final Query ref, final GetListener listener) {
        return getWatchHelper(/* path */ null, ref, listener, /* watch */ false);
    }

    // common helper code for all overloads of get() and watch()
    private SimpleFirebase getWatchHelper(String path, Query ref, GetListener listener, boolean watch) {
        if (ref == null) {
            return this;
        } else if (path == null) {
            if (ref == lastQuery) {
                path = lastQueryPath;
            } else {
                lastQuery = ref;
                lastQueryPath = null;
                path = ref.toString();
            }
        }
        if (logging) { Log.d(LOG_TAG, "get/watch: path=" + path); }

        // listen to the data coming back
        if (listener == null && context instanceof GetListener) {
            listener = (GetListener) context;
        }
        InnerValueEventListener valueListener = new InnerValueEventListener();
        valueListener.path = path;
        valueListener.getListener = listener;

        // either listen once (get) or keep listening (watch)
        if (watch) {
            ref.addValueEventListener(valueListener);
        } else {
            ref.addListenerForSingleValueEvent(valueListener);
        }
        return this;
    }

    /**
     * Returns the user who is currently signed in, or {@code null} if no user is signed in.
     */
    public FirebaseUser getCurrentUser() {
        return user;
    }

    /*
     * Helper function to check for database errors and call listeners as needed.
     * Returns true if there was an error, false if not.
     */
    private boolean handleDatabaseError(DatabaseError error) {
        if (error != null) {
            lastDatabaseError = error;
            if (errorListener != null) {
                errorListener.onError(error);
            } else if (context instanceof ErrorListener) {
                ((ErrorListener) context).onError(error);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns {@code true} if there has been a database error that has not been cleared.
     */
    public boolean hasLastDatabaseError() {
        return lastDatabaseError != null;
    }

    /**
     * Returns {@code true} if a user is currently signed in.
     */
    public boolean isSignedIn() {
        return signInComplete;
    }

    /**
     * Returns the last database error that occurred, or {@code null} if no error has occurred.
     */
    public DatabaseError lastDatabaseError() {
        return lastDatabaseError;
    }

    /**
     * Signs in with the given username and password; an alias for {@code signIn()}.
     */
    public SimpleFirebase login(String username, String password) {
        return signIn(username, password);
    }

    /**
     * Adds a new object with a randomly-generated unique string key at the given path in the database,
     * and returns that newly pushed object.
     * @param path absolute database path such as "foo/bar/baz"
     */
    public DatabaseReference push(String path) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        return fb.child(path).push();
    }

    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * This call will query the given path to find the currently largest child ID, and set the newly
     * added child to have an ID that is +1 higher than that largest child ID.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code PushListener} interface, it will be notified when the new object's key is found
     * and the new object has been created.
     * @param path absolute database path such as "foo/bar/baz"
     */
    public SimpleFirebase pushById(String path) {
        return pushById(path, /* listener */ null);
    }

    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be stored with the given initial value.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code PushListener} interface, it will be notified when the new object's key is found
     * and the new object has been created.
     * @param path absolute database path such as "foo/bar/baz"
     * @param value value to store at this path
     */
    public SimpleFirebase pushById(String path, Object value) {
        return pushById(path, value, /* listener */ null);
    }

    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be temporarily given a value of boolean {@code false}.
     * The given {@code PushListener} will be notified when the data has been created.
     * @param path absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase pushById(String path, PushListener listener) {
        return pushById(path, /* value */ false, listener);
    }

    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be stored with the given initial value.
     * The given {@code PushListener} will be notified when the data has been created.
     * @param path absolute database path such as "foo/bar/baz"
     * @param value value to store at this path
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase pushById(String path, Object value, PushListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = fb.child(path);
        if (logging) { Log.d(LOG_TAG, "pushById: path=" + path + ", value=" + value); }

        if (listener == null && context instanceof PushListener) {
            listener = (PushListener) context;
        }

        // query to get largest current ID (may need to repeat)
        pushById_getMaxId(path, value, child, listener);

        return this;
    }

    /*
     * Helper that queries the db to find the max numeric ID in given area.
     * Once found, tries to start a transaction to add a new child with next available ID.
     */
    private void pushById_getMaxId(final String path, final Object value,
                                 final DatabaseReference child, final PushListener listener) {
        Query query = child.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                long key = 0;
                if (!data.hasChildren()) {
                    // this will be the first child
                    key = 0;
                } else {
                    DataSnapshot lastChild = data.getChildren().iterator().next();
                    String keyStr = lastChild.getKey();
                    try {
                        key = Long.parseLong(keyStr) + 1;   // increment to next key
                        pushById_addNewChild(path, value, child, key, listener);
                    } catch (NumberFormatException nfe) {
                        // empty
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    /*
     * Starts a transaction to add a new child with the given ID.
     * If the ID is taken by the time we get the transaction lock, retries
     * by querying again to get the next available ID.
     */
    private void pushById_addNewChild(final String path, final Object value,
                                      final DatabaseReference ref, final long idKey,
                                      final PushListener listener) {
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mdata) {
                // add the new child
                if (mdata.hasChild(String.valueOf(idKey))) {
                    // oops; somebody already claimed this ID; retry!
                    pushById_getMaxId(path, value, ref, listener);
                    return Transaction.abort();
                } else {
                    MutableData newChild = mdata.child(String.valueOf(idKey));
                    newChild.setValue(value);
                    return Transaction.success(mdata);
                }
            }

            @Override
            public void onComplete(DatabaseError error, boolean completed, DataSnapshot data) {
                if (!handleDatabaseError(error) && completed && listener != null) {
                    String childPath = path + (path.endsWith("/") ? "" : "/") + idKey;
                    DatabaseReference childRef = ref.child(String.valueOf(idKey));
                    listener.onPush(childPath, childRef);
                }
            }
        });
    }

    /**
     * Performs a query on the Firebase database.
     * Similar to the Firebase {@code child()} method.
     * Common intended usage:
     *
     * <pre>
     * SimpleFirebase fb = SimpleFirebase.with(this);
     * fb.get(fb.query("foo/bar/baz")
     *     .orderByChild("quux")
     *     .limitToFirst(1));
     * </pre>
     *
     * @param queryText absolute path in database such as "foo/bar/baz"
     */
    public DatabaseReference query(String queryText) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference query = fb.child(queryText);
        this.lastQuery = query;
        this.lastQueryPath = queryText;
        return query;
    }

    /**
     * Sets the given location in the database to store the given value.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code SetListener} interface, it will be notified when the data has been synced.
     * @param path an absolute database path such as "foo/bar/baz"
     * @param value the value to store there in the database
     */
    public SimpleFirebase set(String path, Object value) {
        return setHelper(path, /* key */ "", value, /* listener */ null);
    }

    /**
     * Sets the given location in the database to store the given value.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code SetListener} interface, it will be notified when the data has been synced.
     * @param path an absolute database path such as "foo/bar/baz"
     * @param key child key name within that path, such as "quux" to indicate "foo/bar/baz/quux"
     * @param value the value to store there in the database
     */
    public SimpleFirebase set(String path, String key, Object value) {
        return setHelper(path, key, value, /* listener */ null);
    }

    /**
     * Sets the given location in the database to store the given value.
     * The given {@code SetListener} will be notified when the data has been synced.
     * @param path an absolute database path such as "foo/bar/baz"
     * @param value the value to store there in the database
     */
    public SimpleFirebase set(String path, Object value, SetListener listener) {
        return setHelper(path, /* key */ "", value, /* listener */ listener);
    }

    /**
     * Sets the given location in the database to store the given value.
     * The given {@code SetListener} will be notified when the data has been synced.
     * @param path an absolute database path such as "foo/bar/baz"
     * @param key child key name within that path, such as "quux" to indicate "foo/bar/baz/quux"
     * @param value the value to store there in the database
     */
    public SimpleFirebase set(String path, String key, Object value, SetListener listener) {
        return setHelper(path, key, value, /* listener */ listener);
    }

    // helper for common set() code
    private SimpleFirebase setHelper(String path, String key, Object value, SetListener listener) {
        if (listener == null && context instanceof SetListener) {
            listener = (SetListener) context;
        }

        if (logging) { Log.d(LOG_TAG, "set: path=" + path + ", key=" + key + ", value=" + value); }

        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = null;
        if (key == null || key.isEmpty()) {
            child = fb.child(path);
        } else {
            if (!path.endsWith("/")) {
                path += "/";
            }
            path += key;
            child = fb.child(path);
        }
        if (child == null) {
            return this;
        }

        if (listener != null) {
            InnerCompletionListener myListener = new InnerCompletionListener();
            myListener.path = path;
            myListener.set = listener;
            child.setValue(value, listener);
        } else {
            child.setValue(value);
        }
        return this;
    }

    /**
     * Sets the given listener object to be notified of future database errors.
     * Pass null to disable listening for database errors.
     * If the context passed to {@code with()} implements {@code ErrorListener}, it will be automatically
     * notified of database errors even if you don't call {@code setErrorListener}.
     */
    public SimpleFirebase setErrorListener(ErrorListener listener) {
        this.errorListener = listener;
        return this;
    }

    /**
     * Sets whether the {@code SimpleFirebase} library should print log messages for debugging.
     */
    public SimpleFirebase setLogging(boolean logging) {
        this.logging = logging;
        if (errorListener == null) {
            // set up a default error logging listener if there is none
            errorListener = new InnerErrorListener();
        }
        return this;
    }

    /**
     * Signs in with the given username and password.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code SignInListener} interface, it will be notified when the sign-in is complete.
     * @param email email address or user name such as "jsmith12"
     * @param password user password such as "abc123"
     */
    public SimpleFirebase signIn(String email, String password) {
        return signInHelper(email, password, /* token */ null, /* authCredential */ null, /* listener */ null);
    }

    /**
     * Signs in with the given username and password.
     * The given {@code SignInListener} will be notified when the user has finished signing in.
     * @param email email address or user name such as "jsmith12"
     * @param password user password such as "abc123"
     */
    public SimpleFirebase signIn(String email, String password, SignInListener listener) {
        return signInHelper(email, password, /* token */ null, /* authCredential */ null, listener);
    }

    /**
     * Signs in to the database anonymously.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code SignInListener} interface, it will be notified when the sign-in is complete.
     */
    public SimpleFirebase signInAnonymously() {
        return signInHelper(/* username */ null, /* password */ null, /* token */ null, /* authCredential */ null, /* listener */ null);
    }

    /**
     * Signs in to the database anonymously.
     * The given {@code SignInListener} will be notified when the user has finished signing in.
     */
    public SimpleFirebase signInAnonymously(SignInListener listener) {
        return signInHelper(/* username */ null, /* password */ null, /* token */ null, /* authCredential */ null, listener);
    }

    /**
     * Signs in with the given username and password.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code SignInListener} interface, it will be notified when the sign-in is complete.
     */
    public SimpleFirebase signInWithCredential(AuthCredential authCredential) {
        return signInHelper(/* email */ null, /* password */ null, /* token */ null, authCredential, /* listener */ null);
    }

    /**
     * Signs in with the given username and password.
     * The given {@code SignInListener} will be notified when the user has finished signing in.
     */
    public SimpleFirebase signInWithCredential(AuthCredential authCredential, SignInListener listener) {
        return signInHelper(/* email */ null, /* password */ null, /* token */ null, authCredential, listener);
    }

    /**
     * Signs in to the database using the given custom token.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code SignInListener} interface, it will be notified when the sign-in is complete.
     */
    public SimpleFirebase signInWithCustomToken(String token) {
        return signInHelper(/* username */ null, /* password */ null, token, /* authCredential */ null, /* listener */ null);
    }

    /**
     * Signs in to the database using the given custom token.
     * The given {@code SignInListener} will be notified when the user has finished signing in.
     */
    public SimpleFirebase signInWithCustomToken(String token, SignInListener listener) {
        return signInHelper(/* username */ null, /* password */ null, token, /* authCredential */ null, listener);
    }

    // helper for common code in sign-in process
    private SimpleFirebase signInHelper(final String email, final String password,
                                        final String token, final AuthCredential authCredential,
                                        final SignInListener listener) {
        if (logging) { Log.d(LOG_TAG, "signIn: email=" + email + ", password=" + password); }

        // figure out listener
        InnerAuthListener innerListener = new InnerAuthListener();
        if (listener != null) {
            innerListener.signin = listener;
        } else if (context instanceof SignInListener) {
            innerListener.signin = (SignInListener) context;
        }

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(innerListener);
        }

        signInComplete = false;

        Task<AuthResult> task = null;
        if (email != null && !email.isEmpty()) {
            // sign-in with user name and password
            task = mAuth.signInWithEmailAndPassword(email, password);
        } else if (token != null && !token.isEmpty()) {
            task = mAuth.signInWithCustomToken(token);
        } else if (authCredential != null) {
            // sign-in with auth credential
            task = mAuth.signInWithCredential(authCredential);
        } else {
            // anonymous sign-in
            task = mAuth.signInAnonymously();
        }

        // listen for task to be completed
        if (task != null) {
            if (context instanceof Activity) {
                task.addOnCompleteListener((Activity) context, innerListener);
            } else {
                task.addOnCompleteListener(innerListener);
            }
        }

        return this;
    }

    /**
     * Signs out of the database, if currently signed in.
     * If not signed in, has no effect.
     */
    public SimpleFirebase signOut() {
        if (mAuth != null) {
            mAuth.signOut();
            this.user = null;
            this.signInComplete = false;
            mAuth = null;
        }
        return this;
    }


    /**
     * Initiates a request to perform a transaction on the data at the given path in the database.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code TransactionListener} interface, it will be notified when the mutable data has arrived.
     * @param path absolute database path such as "foo/bar/baz"
     */
    public SimpleFirebase transaction(String path) {
        return transaction(path, /* listener */ null);
    }

    /**
     * Initiates a request to perform a transaction on the data at the given path in the database.
     * The given {@code TransactionListener} will be notified when the mutable data has arrived.
     * @param path absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the mutable data has arrived
     */
    public SimpleFirebase transaction(String path, final TransactionListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = fb.child(path);

        // query to get largest current ID
        InnerTransactionHandler handler = new InnerTransactionHandler();
        handler.path = path;
        if (listener != null) {
            handler.listener = listener;
        } else if (context instanceof TransactionListener) {
            handler.listener = (TransactionListener) context;
        }
        child.runTransaction(handler);

        return this;
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * @param path absolute database path such as "foo/bar/baz"
     */
    public SimpleFirebase watch(String path) {
        return watch(path, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * @param path absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase watch(String path, final GetListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = fb.child(path);
        this.lastQuery = child;
        this.lastQueryPath = path;
        return getWatchHelper(path, child, listener, /* watch */ true);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to {@code with()} when creating this {@code SimpleFirebase} object implements
     * the {@code GetListener} interface, it will be notified when the data arrives.
     * @param ref a {@code Query} object representing an absolute database path
     */
    public SimpleFirebase watch(Query ref) {
        return watch(ref, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given {@code GetListener} will be notified when the data has arrived.
     * @param ref a {@code Query} object representing an absolute database path
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase watch(final Query ref, final GetListener listener) {
        return getWatchHelper(/* path */ null, ref, listener, /* watch */ true);
    }

    /*
     * Helper class that listens for authentication results; used by signin()
     */
    private class InnerAuthListener implements FirebaseAuth.AuthStateListener,
            OnCompleteListener<AuthResult> {
        private SignInListener signin;

        @Override
        public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
            if (logging) { Log.d(LOG_TAG, "onAuthStateChanged: " + firebaseAuth); }

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // User is signed in
                user = currentUser;
                if (logging) { Log.d(LOG_TAG, "signed in UID: " + currentUser.getUid()); }
            } else {
                // User is signed out
                if (logging) { Log.d(LOG_TAG, "signed out"); }
            }
            signInComplete = true;
            if (signin != null) {
                signin.onSignIn(signInComplete);
            }
        }

        @Override
        public void onComplete(Task<AuthResult> task) {
            Log.d(LOG_TAG, "signin complete: successful? " + task.isSuccessful());
            signInComplete = true;
            if (signin != null) {
                signin.onSignIn(task.isSuccessful());
            }
        }
    }

    /*
     * Helper class that listens for database task completion results; used by set().
     */
    private class InnerCompletionListener implements DatabaseReference.CompletionListener {
        private boolean complete = false;
        private DatabaseError error;
        private SetListener set;
        private String path;

        @Override
        public void onComplete(DatabaseError error, DatabaseReference ref) {
            complete = true;
            if (set != null) {
                set.onSet(path);
            }
            handleDatabaseError(error);
        }
    }

    /*
     * Helper class that listens for database errors and logs them to the Android Studio console.
     */
    private class InnerErrorListener implements ErrorListener {
        @Override
        public void onError(DatabaseError error) {
            Log.d(LOG_TAG, " *** DATABASE ERROR: " + error);
        }
    }

    private class InnerTransactionHandler implements Transaction.Handler {
        private String path;
        private TransactionListener listener;

        @Override
        public Transaction.Result doTransaction(MutableData mdata) {
            if (listener != null) {
                listener.onTransaction(path, mdata);
            }
            return Transaction.success(mdata);
        }

        @Override
        public void onComplete(DatabaseError error, boolean committed, DataSnapshot data) {
            handleDatabaseError(error);
        }
    }

    /*
     * Helper class that listens for data arrival results; used by get() and watch().
     */
    private class InnerValueEventListener implements ValueEventListener {
        private String path = null;
        private GetListener getListener;

        @Override
        public void onDataChange(DataSnapshot data) {
            if (getListener != null) {
                getListener.onGet(path, data);
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            handleDatabaseError(error);
        }
    }
}
