package dev.dungeons.dragons.gems;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

public class GameSetting {
    public static final String PREFS_FIRST_RUN = "FirstRun";
    private static final String PREFS_MUSIC = "music";
    private static final String PREFS_SOUND = "sound";
    public static SharedPreferences pref;
    private static int playmusic;
    private static int playsound;
    private static ImageView musicOff;
    private static ImageView musicOn;
    private static ImageView sfxon;
    private static ImageView sfxoff;
    private static MediaPlayer bgsound;
    private final boolean firstRun;
    private final MediaPlayer mp;
    private final MediaPlayer win;

    public GameSetting(Context context) {
        pref = context.getSharedPreferences(PREFS_FIRST_RUN, Context.MODE_PRIVATE);
        firstRun = pref.getBoolean(PREFS_FIRST_RUN, true);

        if (firstRun) {
            playmusic = 1;
            playsound = 1;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(PREFS_FIRST_RUN, false);
            editor.apply();
        } else {
            playmusic = pref.getInt(PREFS_MUSIC, 1);
            playsound = pref.getInt(PREFS_SOUND, 1);
        }

        bgsound = MediaPlayer.create(context, R.raw.bg_music);
        bgsound.setLooping(true);

        mp = MediaPlayer.create(context, R.raw.spin);
        win = MediaPlayer.create(context, R.raw.win);

        if (playmusic == 1) {
            checkmusic();
        }

    }

    public static void showSettingsDialog(Context context) {
        final Dialog dialog;

        dialog = new Dialog(context, R.style.WinDialog);
        dialog.getWindow().setContentView(R.layout.settings);

        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());

        musicOn = dialog.findViewById(R.id.music_on);
        musicOn.setOnClickListener(v -> {
            playmusic = 0;
            checkmusic();
            musicOn.setVisibility(View.INVISIBLE);
            musicOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(PREFS_MUSIC, playmusic);
            editor.apply();
        });

        musicOff = dialog.findViewById(R.id.music_off);
        musicOff.setOnClickListener(v -> {
            playmusic = 1;
            bgsound.start();
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(PREFS_MUSIC, playmusic);
            editor.apply();
        });

        sfxon = dialog.findViewById(R.id.sounds_on);
        sfxon.setOnClickListener(v -> {
            playsound = 0;
            sfxon.setVisibility(View.INVISIBLE);
            sfxoff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(PREFS_SOUND, playsound);
            editor.apply();
        });

        sfxoff = dialog.findViewById(R.id.sounds_off);
        sfxoff.setOnClickListener(v -> {
            playsound = 1;
            dialog.show();
            sfxon.setVisibility(View.VISIBLE);
            sfxoff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(PREFS_SOUND, playsound);
            editor.apply();
        });

        checkmusicdraw();
        checksounddraw();

        dialog.show();
    }

    private static void checkmusic() {
        if (playmusic == 1) {
            bgsound.start();
        } else {
            bgsound.pause();
        }
    }

    private static void checkmusicdraw() {
        if (playmusic == 1) {
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.INVISIBLE);
        } else {
            musicOn.setVisibility(View.INVISIBLE);
            musicOff.setVisibility(View.VISIBLE);
        }
    }

    private static void checksounddraw() {
        if (playsound == 1) {
            sfxon.setVisibility(View.VISIBLE);
            sfxoff.setVisibility(View.INVISIBLE);
        } else {
            sfxon.setVisibility(View.INVISIBLE);
            sfxoff.setVisibility(View.VISIBLE);
        }
    }

    public void resumeSounds() {
        if (playmusic == 1) {
            bgsound.start();
        }
    }

    public void stopSounds() {
        if (bgsound != null && bgsound.isPlaying()) {
            bgsound.pause();
        }

        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }

        if (win != null && win.isPlaying()) {
            win.pause();
        }
    }

    public void playSpinSound() {
        if (playsound == 1) {
            mp.start();
        }
    }

    public void playWinSound() {
        if (playsound == 1) {
            win.start();
        }
    }

    public int getPlaysound() {
        return playsound;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

}
