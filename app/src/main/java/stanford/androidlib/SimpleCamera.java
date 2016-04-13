/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * A utility class for event handling.
 *
 * <pre>
 * SimpleCamera.with(this).takePhoto();
 * </pre>
 */
public final class SimpleCamera {
    private static Activity context = null;
    private static final SimpleCamera INSTANCE = new SimpleCamera();

    /**
     * Request code for Intent to take a photo.
     */
    protected static final int REQ_CODE_TAKE_PICTURE = 0x193a;

    /**
     * Request code for Intent to launch photo gallery.
     */
    protected static final int REQ_CODE_PHOTO_GALLERY = 0x193c;

    /**
     * Returns a singleton SimpleCamera instance bound to the given context.
     */
    public static SimpleCamera with(Activity context) {
        SimpleCamera.context = context;
        return INSTANCE;
    }

    private SimpleCamera() {
        // empty
    }

    /**
     * Returns true if the device has a camera.
     */
    public boolean cameraExists() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Instructs the device to take a photo without saving it to a file.
     * The onPhotoReady method will be called when the photo is ready.
     * Logs the error if the action failed (e.g. if this device cannot support speech-to-text).
     * May want to check cameraExists before calling this.
     */
    // @RequiresPermission(Manifest.permission.CAMERA)
    public SimpleCamera takePhoto() {
        try {
            Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            context.startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException anfe) {
            Log.wtf("SimpleSpeech", anfe);
        }
        return this;
    }

    /**
     * Instructs the device to take a photo and save it to a file.
     * The onPhotoReady method will be called when the photo is ready.
     */
    // @RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public SimpleCamera takePhoto(String filename) {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // create the file to save the image into
        File photosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!photosDir.exists()) {
            if (!photosDir.mkdirs()) {
                return this;
            }
        }
        File photoFile = new File(photosDir, filename);
        picIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        context.startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
        return this;
    }

    /**
     * Launches the device's photo gallery activity.
     * Once the photo has been chosen, the onPhotoReady method will be called.
     * Note that your app needs the permissions android.permission.MANAGE_DOCUMENTS and
     * android.permission.READ_EXTERNAL_STORAGE to read photos from the photo gallery.
     */
    // @RequiresPermission("android.permission.READ_EXTERNAL_STORAGE")
    public SimpleCamera photoGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        context.startActivityForResult(intent, REQ_CODE_PHOTO_GALLERY);
        return this;
    }
}
