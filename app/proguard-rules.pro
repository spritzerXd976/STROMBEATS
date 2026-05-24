# Default ProGuard rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson / Models
-keep class com.stormbeats.app.data.model.** { *; }
-keepclassmembers class com.stormbeats.app.data.model.** { *; }
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Navigation
-keep class androidx.navigation.** { *; }

# BuildConfig
-keep class com.stormbeats.app.BuildConfig { *; }
