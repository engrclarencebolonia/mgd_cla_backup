# Keep your package and its subpackages intact
-keep class dev.dungeons.dragons.gems.* {
    public protected private *;
}

# Keep specific classes intact
-keep class dev.dungeons.dragons.gems.MainActivity { *; }
-keep class dev.dungeons.dragons.gems.CustomLayoutManager { *; }
-keep class dev.dungeons.dragons.gems.GamePolicy { *; }
-keep class dev.dungeons.dragons.gems.WebSettingGlobal { *; }
-keep class dev.dungeons.dragons.gems.EmptyMessageException { *; }
-keep class dev.dungeons.dragons.gems.GameLogic { *; }
-keep class dev.dungeons.dragons.gems.MainMenu { *; }
-keep class dev.dungeons.dragons.gems.SplashScreen { *; }
-keep class dev.dungeons.dragons.gems.ReturnedPolicy { *; }
-keep class dev.dungeons.dragons.gems.VideoService { *; }
-keep class dev.dungeons.dragons.gems.NotificationConfig { *; }
-keep class dev.dungeons.dragons.gems.CustomLayoutManager { *; }
-keep class dev.dungeons.dragons.gems.MyNotification { *; }
-keep class dev.dungeons.dragons.gems.libs.WebAppConfig { *; }
-keep class dev.dungeons.dragons.gems.libs.JSInterface { *; }
-keep class dev.dungeons.dragons.gems.libs.Crypt { *; }
-keep class dev.dungeons.dragons.gems.GameSetting { *; }

# Keep Google Play services and Firebase classes
-keep class com.google.android.gms.* { *; }
-keep class com.google.firebase.* { *; }

# Keep any other classes that are necessary for your application
-keep class dev.dungeons.dragons.gems.** { *; }

# Add any library-specific rules as recommended by the library documentation
# -keep class com.example.library.** { *; }

# Suppress warnings for specific classes
-dontwarn com.google.android.gms.tasks.OnCompleteListener
-dontwarn com.google.android.gms.tasks.Task
-dontwarn com.google.firebase.iid.FirebaseInstanceId
-dontwarn com.google.firebase.iid.InstanceIdResult
-dontwarn com.google.firebase.messaging.FirebaseMessagingService
-dontwarn com.google.firebase.messaging.RemoteMessage$Notification
-dontwarn com.google.firebase.messaging.RemoteMessage
-dontwarn java.awt.Color
-dontwarn java.awt.Font
-dontwarn java.awt.Point
