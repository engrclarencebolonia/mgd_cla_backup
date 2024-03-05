package dev.dungeons.dragons.gems;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;


public class NotificationConfig extends Application {

    private final static String TAG = "NotificationConfig";
    private final static String AF_ID = "eh3iDJLtVjxXwLFPyk8wpL";
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();


        this.context = getBaseContext();

        AppsFlyerLib.getInstance().start(context, AF_ID, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "Launch sent successfully, got 200 response code from server");
            }

            @Override
            public void onError(int i, @NonNull String s) {
                Log.e(TAG, "Launch failed to be sent:\n" + "Error code: " + i + "\n" + "Error description: " + s);
            }
        });
        AppsFlyerLib.getInstance().setDebugLog(true);


        PusherOptions options = new PusherOptions();
        options.setCluster("ap1");

        Pusher pusher = new Pusher("fdcb398aff6445bc7bd6", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.i("Pusher", "There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);


        Channel channel = pusher.subscribe(getPackageName());

        channel.bind("my-event", event -> {

            try {
                JSONObject notifyMsg = new JSONObject(event.getData());

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                if (!notificationManager.areNotificationsEnabled()) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    showNotification("Announcement", notifyMsg.getString("message"), notifyMsg.getString("url"));
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        });

    }


    private void showNotification(String title, String message, String link) {

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.my_notification);
        remoteViews.setTextViewText(R.id.notificationTitle, title);
        remoteViews.setTextViewText(R.id.notificationMessage, message);

        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openLinkIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.openLinkButton, pendingIntent);

        NotificationChannel channel = new NotificationChannel("my-channel", "Announcements", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my-channel")
                .setSmallIcon(R.drawable.notif)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews)
                .setAutoCancel(true);

        NotificationManagerCompat notificationMg = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationMg.notify(1, builder.build());

    }

}