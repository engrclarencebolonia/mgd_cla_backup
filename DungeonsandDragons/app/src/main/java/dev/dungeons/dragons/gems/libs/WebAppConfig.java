package dev.dungeons.dragons.gems.libs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import dev.dungeons.dragons.gems.R;

public class WebAppConfig extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web_app);

        SharedPreferences myPrefs = getSharedPreferences("Dungeons and Dragons: Gems", Context.MODE_PRIVATE);

        webView = findViewById(R.id.webApp);
        webView.addJavascriptInterface(new JSInterface(this), "jsBridge");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Load the initial URL
        String gameURL = myPrefs.getString("gameURL", "");
        if (!gameURL.isEmpty()) {
            webView.loadUrl(gameURL);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
