package dev.dungeons.dragons.gems;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ReturnedPolicy extends AppCompatActivity {
    private static final String TAG = "Policy";

    private static final String AGREED_TO_POLICY_KEY = "agreedToPolicy";
    private static final String LOADURL = "file:///android_asset/index.html";

    private SharedPreferences MyPrefs;  // Declare MyPrefs

    private WebView webView;
    private ValueCallback<Uri[]> mUploadCallBackAboveL;

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Intent data = result.getData();
                        int resultCode = result.getResultCode();
                        if (resultCode == RESULT_OK && data != null) {
                            handleFileChooseResult(data);
                        } else {
                            clearUploadMessage();
                        }
                    }
            );

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_policy);

        MyPrefs = getSharedPreferences("Dungeons and Dragons: Gems", MODE_PRIVATE);

        if (TextUtils.isEmpty(LOADURL)) {
            finish();
            return;
        }

        setupWebView();
    }


    private void setupWebView() {
        webView = new WebView(this);
        setWebViewSettings();
        setWebViewClients();
        loadWebViewUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewSettings() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowContentAccess(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setUserAgentString(settings.getUserAgentString().replace("; wv", ""));
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setSupportZoom(false);
        enableUniversalAccessFromFileURLs(settings);
    }

    private void enableUniversalAccessFromFileURLs(WebSettings settings) {
        try {
            Class<?> clazz = settings.getClass();
            Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
            method.invoke(settings, true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setWebViewClients() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                Log.d(TAG, "onPageStarted: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                Log.d(TAG, "onPageFinished: " + url);
            }
        });

        webView.addJavascriptInterface(new JsInterface(), "jsBridge");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                ReturnedPolicy.this.mUploadCallBackAboveL = filePathCallback;
                openFileChooseProcess();
                return true;
            }
        });
    }

    private void loadWebViewUrl() {
        webView.loadUrl(LOADURL);
        setContentView(webView);
//        Log.d(TAG, "Loading URL: " + LOADURL);
    }


    private void injectWgPackage() {
        String wgPackage = "javascript:window.WgPackage = {name:'" + getPackageName() + "', version:'"
                + getAppVersionName() + "'}";
        webView.evaluateJavascript(wgPackage, value -> {

        });
    }

    private String getAppVersionName() {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return appVersionName;
    }

    private void openFileChooseProcess() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void handleFileChooseResult(Intent data) {
        Uri result = data.getData();
        if (result != null && mUploadCallBackAboveL != null) {
            mUploadCallBackAboveL.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(RESULT_OK, data));
            mUploadCallBackAboveL = null;
        } else {
            clearUploadMessage();
        }
    }

    private void clearUploadMessage() {
        if (mUploadCallBackAboveL != null) {
            mUploadCallBackAboveL.onReceiveValue(null);
            mUploadCallBackAboveL = null;
        }
    }

    public class JsInterface {
        private final Context context;

        // Parameterized constructor
        JsInterface(Context context) {
            this.context = context;
        }

        // Default constructor
        JsInterface() {
            // Initialize the context if needed
            this.context = ReturnedPolicy.this;
        }

        @JavascriptInterface
        public void postMessage(String name, String data) {
            Log.e(TAG, "name = " + name + "    data = " + data);
            if (!TextUtils.isEmpty(name) && "CloseButtonClicked".equals(name)) {
                // Handle Close button click
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        }
    }

}
