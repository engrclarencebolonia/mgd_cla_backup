package dev.dungeons.dragons.gems;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        Toast.makeText(context, "Dungeons and Dragons: Gems Received Notification: " + title + " - " + message, Toast.LENGTH_SHORT).show();

    }
}
