package dev.dungeons.dragons.gems;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MainMenu extends AppCompatActivity {
    private GameSetting gameSetting;
    private VideoView backgroundVideoView;
    private int videoPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);

        backgroundVideoView = findViewById(R.id.backgroundVideoView);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_background);
        backgroundVideoView.setVideoURI(videoUri);
        backgroundVideoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            if (videoPosition != 0) {
                mp.seekTo(videoPosition);
            }
        });
        backgroundVideoView.start();


        gameSetting = new GameSetting(this);

        // Find buttons
        AppCompatButton startBtn = findViewById(R.id.start);
        AppCompatButton playBtn = findViewById(R.id.play);
        AppCompatButton settingBtn = findViewById(R.id.settings);
        AppCompatButton aboutBtn = findViewById(R.id.about);

        startBtn.setOnClickListener(v -> {
            // Hide start button
            startBtn.setVisibility(View.INVISIBLE);
            // Show other buttons
            playBtn.setVisibility(View.VISIBLE);
            settingBtn.setVisibility(View.VISIBLE);
            aboutBtn.setVisibility(View.VISIBLE);
        });

        playBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, MainActivity.class);
            startActivity(intent);
        });

        settingBtn.setOnClickListener(v -> GameSetting.showSettingsDialog(this));


        aboutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, ReturnedPolicy.class);
            startActivity(intent);
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        backgroundVideoView.pause();
        gameSetting.stopSounds();
        videoPosition = backgroundVideoView.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameSetting.resumeSounds();
        backgroundVideoView.start();
    }
}
