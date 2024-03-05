package dev.dungeons.dragons.gems.libs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;
import java.util.Map;

import dev.dungeons.dragons.gems.MainMenu;

public class JSInterface {

    private final Context context;
    private final String policyStatus = "policyStatus";

    public JSInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void postMessage(String name, String data) {
        Log.i("Dungeons and Dragons: Gems", "name = " + name + "    data = " + data);
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(data)) {
            return;
        }
        handleCustomEvents((Activity) context, name, data);
    }

    private void handleCustomEvents(Activity context, String name, String data) {
        Map<String, Object> eventValue = new HashMap<>();
        switch (name) {
            case "UserConsent":
                handleUserConsentEvent(context, data);
                break;
            case "firstrecharge":
            case "recharge":
                handleRechargeEvent(eventValue, data);
                break;
            case "withdrawOrderSuccess":
                handleWithdrawOrderSuccessEvent(eventValue, data);
                break;
            default:
                eventValue.put(name, data);
        }


        AppsFlyerLib.getInstance().logEvent(context, name, eventValue);

        // Log the event
        Log.i("Dungeons and Dragons: Gems", "Event logged: " + name + ", Data: " + eventValue);
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
    }

    private void handleUserConsentEvent(Activity context, String data) {
        SharedPreferences appPreferences = context.getSharedPreferences("Dungeons and Dragons: Gems", Context.MODE_PRIVATE);

        if ("Accepted".equals(data)) {
            appPreferences.edit().putBoolean(policyStatus, true).apply();

            Log.i("Dungeons and Dragons: Gems", "User Consent Accepted");

            // Add AppsFlyer tracking for user consent here
            Map<String, Object> eventValue = new HashMap<>();
            eventValue.put(AFInAppEventParameterName.CONTENT, "User Consent Accepted");

            // Log the event
            Log.i("Dungeons and Dragons: Gems", "Event logged: UserConsent, Data: " + eventValue);

            // Check if the custom scheme is triggered
            if ("mainmenu".equals(data)) {
                Intent intent = new Intent(context, MainMenu.class);
                context.startActivity(intent);
                context.finish();
            }
        } else {
            appPreferences.edit().putBoolean(policyStatus, false).apply();
            context.finishAffinity();
        }
    }

    private void handleRechargeEvent(Map<String, Object> eventValue, String data) {
        Map<String, Object> maps = parseDataMap(data);
        eventValue.put(AFInAppEventParameterName.REVENUE, maps.get("amount") != null ? maps.get("amount") : 0.0f);
        eventValue.put("otherRevenue", maps.get("amount") != null ? ((Number) maps.get("amount")).floatValue() : 0.0f);
    }

    private void handleWithdrawOrderSuccessEvent(Map<String, Object> eventValue, String data) {
        Map<String, Object> maps = parseDataMap(data);
        String amount = maps.get("amount") != null ? maps.get("amount").toString() : null;
        if (amount != null && !amount.isEmpty()) {
            float revenue = Float.parseFloat(amount);
            eventValue.put(AFInAppEventParameterName.REVENUE, revenue);
        }
        eventValue.put(AFInAppEventParameterName.CURRENCY, maps.get("currency"));
    }

    private Map<String, Object> parseDataMap(String data) {
        try {
            return JSON.parseObject(data);
        } catch (Exception e) {
            Log.e("UserConsent", "Error parsing data map: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
