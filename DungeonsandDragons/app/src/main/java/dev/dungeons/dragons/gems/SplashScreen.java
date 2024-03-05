package dev.dungeons.dragons.gems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import dev.dungeons.dragons.gems.libs.Crypt;
import dev.dungeons.dragons.gems.libs.WebAppConfig;


public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 2 seconds

    public static String gameURL = "";
    public static String appStatus = "";
    public static String apiResponse = "";
    SharedPreferences MyPrefs;
    private Crypt crypt;

    private ProgressBar progressBar;

    int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash);

        crypt = new Crypt();

        progress();




        MyPrefs = getSharedPreferences("Dungeons and Dragons: Gems", MODE_PRIVATE);
        boolean isFirstTime = MyPrefs.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            // If it's the first time, redirect to Policy.class
            MyPrefs.edit().putBoolean("isFirstTime", false).apply();
            Intent intent = new Intent(SplashScreen.this, GamePolicy.class);
            startActivity(intent);
            return;
        }

        VideoView videoView = findViewById(R.id.videoView);

        RequestQueue connectAPI = Volley.newRequestQueue(this);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("appid", "PG");
            requestBody.put("package", getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String endPoint = "https://backend.madgamingdev.com/api/gameid" + "?appid=PG&package=" + getPackageName();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                response -> {
                    apiResponse = response.toString();

                    try {
                        JSONObject jsonData = new JSONObject(apiResponse);
                        String decryptedData = crypt.decrypt(jsonData.getString("data"), "21913618CE86B5D53C7B84A75B3774CD");
                        JSONObject gameData = new JSONObject(decryptedData);

                        appStatus = jsonData.getString("gameKey");
                        gameURL = gameData.getString("gameURL");

                        MyPrefs.edit().putString("gameURL", gameURL).apply();


                        String videoPath = "android.resource://" + getPackageName() + File.separator + R.raw.splashscreen;
                        Uri uri = Uri.parse(videoPath);
                        videoView.setVideoURI(uri);
                        videoView.setOnCompletionListener(mp -> {

                            new Handler().postDelayed(() -> {

                                if (Boolean.parseBoolean(appStatus)) {
                                    Intent intent = new Intent(SplashScreen.this, WebAppConfig.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(SplashScreen.this, GamePolicy.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, SPLASH_TIME_OUT);
                        });

                        videoView.start();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }, error -> {
//            Log.d("API:RESPONSE", error.toString());
        });
        connectAPI.add(jsonRequest);
    }

    public void progress() {
        progressBar = findViewById(R.id.download_progressbar);

        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                counter++;
                progressBar.setProgress(counter);

                if (counter == 100)
                    t.cancel();
            }
        };

        t.schedule(tt, 0, 100);
    }
}