/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for playing media.
 *
 * <pre>
 * SimpleMedia.with(this).play(R.raw.mysong);
 * </pre>
 */
public final class SimpleMedia {
    private static Context context = null;
    private static final SimpleMedia INSTANCE = new SimpleMedia();

    /**
     * Returns a singleton SimpleMedia instance bound to the given context.
     */
    public static SimpleMedia with(Context context) {
        SimpleMedia.context = context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleMedia instance bound to the given view's context.
     */
    public static SimpleMedia with(View context) {
        Context newContext = context.getContext();
        if (SimpleMedia.context == null || newContext != null) {
            SimpleMedia.context = newContext;
        }
        return INSTANCE;
    }

    private Map<Integer, MediaPlayer> mediaPlayers = null;

    private SimpleMedia() {
        // empty
    }

    private void __ensureMediaPlayerMap() {
        if (mediaPlayers == null) {
            mediaPlayers = new HashMap<>();
        }
    }

    // helper to make sure media player is loaded for clip with given id
    private MediaPlayer __ensureMediaPlayer(@RawRes int id) {
        __ensureMediaPlayerMap();
        if (!mediaPlayers.containsKey(id)) {
            MediaPlayer player = MediaPlayer.create(context, id);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayers.put(id, player);
        }
        return mediaPlayers.get(id);
    }

    /**
     * Returns whether the media clip with the given ID is currently playing in 'loop' mode.
     */
    public boolean isLooping(@RawRes int id) {
        __ensureMediaPlayerMap();
        if (!mediaPlayers.containsKey(id)) {
            return false;
        }
        MediaPlayer mediaPlayer = mediaPlayers.get(id);
        return mediaPlayer.isLooping();
    }

    /**
     * Returns whether the media clip with the given ID is currently playing or looping.
     */
    public boolean isPlaying(@RawRes int id) {
        __ensureMediaPlayerMap();
        if (!mediaPlayers.containsKey(id)) {
            return false;
        }
        MediaPlayer mediaPlayer = mediaPlayers.get(id);
        return mediaPlayer.isPlaying() || mediaPlayer.isLooping();
    }

    /**
     * Plays the media clip with the given ID in 'looping' mode.
     */
    public SimpleMedia loop(@RawRes int id) {
        MediaPlayer mediaPlayer = __ensureMediaPlayer(id);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        return this;
    }

    /**
     * Plays the media clip with the given ID.
     */
    public SimpleMedia play(@RawRes int id) {
        MediaPlayer mediaPlayer = __ensureMediaPlayer(id);
        mediaPlayer.setOnCompletionListener(new CompletionListener());
        mediaPlayer.start();
        return this;
    }

    /**
     * Pauses the media clip with the given ID if it is playing.
     */
    public SimpleMedia pause(@RawRes int id) {
        if (isPlaying(id)) {
            MediaPlayer mediaPlayer = mediaPlayers.get(id);
            mediaPlayer.pause();
        }
        return this;
    }

    /**
     * Returns the time position in milliseconds for the media clip with the given ID if it is playing.
     */
    public int getPosition(@RawRes int id) {
        if (isPlaying(id)) {
            MediaPlayer mediaPlayer = mediaPlayers.get(id);
            return mediaPlayer.getCurrentPosition();
        } else {
            return -1;
        }
    }

    /**
     * Sets the time position in milliseconds for the media clip with the given ID.
     * If the clip was not playing, it starts playing.
     */
    public SimpleMedia setPosition(@RawRes int id, int position) {
        if (isPlaying(id)) {
            MediaPlayer mediaPlayer = mediaPlayers.get(id);
            mediaPlayer.seekTo(position);
        } else {
            MediaPlayer mediaPlayer = mediaPlayers.get(id);
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
        }
        return this;
    }

    /**
     * Stops the media clip with the given ID if it was playing.
     */
    public SimpleMedia stop(@RawRes int id) {
        __ensureMediaPlayerMap();
        MediaPlayer mediaPlayer = mediaPlayers.get(id);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayers.remove(id);
        }
        return this;
    }

    private class CompletionListener implements MediaPlayer.OnCompletionListener {
        /**
         * Called when a MediaPlayer is done playing.
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mediaPlayers == null) {
                return;
            }
            int id = -1;
            for (int eachID : mediaPlayers.keySet()) {
                MediaPlayer eachMP = mediaPlayers.get(eachID);
                if (eachMP == mp) {
                    id = eachID;
                    break;
                }
            }
            if (id > 0) {
                MediaPlayer mediaPlayer = mediaPlayers.get(id);
                mediaPlayers.remove(id);
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    } catch (Exception e) {
                        // empty
                    }
                }
            }
        }
    }
}
