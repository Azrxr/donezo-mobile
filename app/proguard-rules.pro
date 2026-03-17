# ==========================================
# PROGUARD RULES UNTUK DONEZO (COMPOSE + HILT + ROOM)
# ==========================================

# 1. MELINDUNGI LIFECYCLE & COMPOSE VIEW TREE (Pencegah Crash setContent)
-keep class * extends android.app.Activity
-keep class * extends android.app.Application
-keep class androidx.lifecycle.** { *; }
-keep class androidx.savedstate.** { *; }
-keep class androidx.compose.ui.platform.** { *; }

# 2. MELINDUNGI MODEL DATA & ROOM DATABASE
-keep class com.jasawira.donezo.domain.model.** { *; }
-keep class com.jasawira.donezo.data.local.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# 3. MELINDUNGI HILT & VIEWMODEL
-keep class * extends androidx.lifecycle.ViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class *
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# 4. MELINDUNGI KOTLINX SERIALIZATION (PENTING UNTUK NAVIGASI!)
-keepattributes *Annotation*, InnerClasses, Signature
-keep class kotlinx.serialization.** { *; }
-keepnames class com.jasawira.donezo.**$$serializer { *; }
-keepclassmembers class com.jasawira.donezo.** {
    *** Companion;
}
-keepclasseswithmembers class com.jasawira.donezo.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# 5. MELINDUNGI COROUTINES
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**