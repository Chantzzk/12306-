-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application

-keep class com.ticket12306.android.data.model.** { *; }
-keep class com.ticket12306.android.data.local.entity.** { *; }
-keep class com.ticket12306.android.data.remote.api.** { *; }
-keep class com.ticket12306.android.di.** { *; }

-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }

-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keep class dagger.hilt.** { *; }
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

-keep class androidx.hilt.** { *; }

-keep class **_HiltModules* { *; }
-keep class **_HiltComponents* { *; }
-keep class **_GeneratedInjector { *; }
-keep class **_MemberInjector { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclassmembers class * {
    @dagger.hilt.InstallIn <methods>;
    @dagger.hilt.EntryPoint <methods>;
}

-keep class dagger.hilt.internal.** { *; }
-keep class hilt_aggregated_deps.** { *; }

-keep class dagger.assisted.** { *; }
-keep @dagger.assisted.AssistedInject class * { *; }
-keepclassmembers class * {
    @dagger.assisted.AssistedInject <init>(...);
}
-keep class **_AssistedFactory { *; }
-keep class **_Factory { *; }

-keep class androidx.work.** { *; }

-keep class androidx.datastore.** { *; }

-keep class com.facebook.shimmer.** { *; }

-keep class androidx.navigation.** { *; }
-keep class * extends androidx.navigation.NavArgs { *; }
-keepclassmembers class * extends androidx.navigation.NavArgs {
    <init>(...);
}
-keep class **Args { *; }
-keep class **Directions { *; }

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers @kotlinx.parcelize.Parcelize class * {
    public static final ** CREATOR;
}
-keep @kotlinx.parcelize.Parcelize class * { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class coil.** { *; }

-keep class com.ticket12306.android.BookingWorker { *; }
-keep class com.ticket12306.android.booking.** { *; }
-keep class com.ticket12306.android.service.** { *; }
-keep class com.ticket12306.android.util.** { *; }
-keep class com.ticket12306.android.data.local.dao.** { *; }
-keep class com.ticket12306.android.data.local.database.** { *; }
-keep class com.ticket12306.android.data.local.preferences.** { *; }
-keep class com.ticket12306.android.data.repository.** { *; }
-keep class com.ticket12306.android.ui.** { *; }

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
