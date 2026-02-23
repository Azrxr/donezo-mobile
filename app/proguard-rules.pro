# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============ Hilt Dependency Injection ============
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep interface dagger.hilt.** { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
    @dagger.hilt.* <fields>;
}

# ============ Room Database ============
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-keepclasseswithmembernames class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# ============ Kotlin Serialization ============
-keepattributes *Annotation*
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.* <methods>;
}

# ============ Jetpack Compose ============
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclasseswithmembernames class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ============ AndroidX Core ============
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.work.** { *; }

# ============ Your App Classes ============
-keep class com.jasawira.donezo.domain.** { *; }
-keep class com.jasawira.donezo.data.** { *; }
-keep class com.jasawira.donezo.presentation.** { *; }

# ============ Enum Classes ============
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============ Data Classes & Models ============
-keep class com.jasawira.donezo.domain.model.** { *; }
-keep class com.jasawira.donezo.data.local.entity.** { *; }

# ============ Keep All Public Methods ============
-keepclassmembers class * {
    public <methods>;
}

# ============ Suppress Warnings ============
-dontwarn kotlin.**
-dontwarn kotlinx.**
-dontwarn android.**
-dontwarn androidx.**
-ignorewarnings


