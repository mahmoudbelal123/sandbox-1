# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/atul/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn java.awt.**

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn okhttp3.**

-dontwarn rx.internal.util.unsafe.**

-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-dontwarn sun.misc.Unsafe
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn com.squareup.picasso.**
-keepclasseswithmembernames class * {
    native <methods>;
}

#rx java
-dontwarn sun.misc.**

-dontwarn com.fasterxml.**
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.http.**

-keep class com.firebase.** { *; }
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.leakcanary.** { *; }
-keep class com.squareup.** { *; }

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-dontwarn retrofit2.adapter.rxjava.CompletableHelper$**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

#-keep class com.akexorcist.roundcornerprogressbar.** { *; }
-dontwarn com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar
-dontwarn gun0912.tedbottompicker.**
-dontwarn gun0912.tedbottompicker.TedBottomPicker
#-keep class gun0912.tedbottompicker.** { *; }
-dontwarn org.androidannotations.**
-keep class android.widget.** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

-keep class com.google.gson.** { *; }

-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit2.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.**

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}



# event bus
-keepattributes *Annotation*


-keep class com.appster.turtle.model.** {
*;
}

-keep class com.appster.turtle.network.request.** {
 *;
}

-keep class com.appster.turtle.network.response.** {
 *;
}

-keep class com.appster.turtle.network.** {
 *;
}

-keep class com.appster.turtle.** {
 *;
}

-keep class com.facebook.** {
   *;
}




-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-keepattributes *Annotation*,EnclosingMethod
-keepclasseswithmembers class * {
   public <init>(android.content.Context, android.util.AttributeSet, int);
 }

 -keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
  public static final *** NULL;
 }

 -keepnames @com.google.android.gms.common.annotation.KeepName class *
 -keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
  }

 -keepnames class * implements android.os.Parcelable {
  public static final ** CREATOR;
 }

 # Hide warnings about references to newer platforms in the library
 -dontwarn android.support.v7.**
 # don't process support library
 -keep class android.support.v7.** { *; }
 -keep interface android.support.v7.** { *; }
 -keep class android.support.v4.** { *; }
 -dontnote android.support.v4.**


  -dontwarn android.databinding.**
  -keep class android.databinding.** { *; }
  -keepclassmembers class * {
      @android.webkit.JavascriptInterface <methods>;
  }
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int, java.lang.String);}

-keep class com.wonderkiln.blurkit.** { *; }

-dontwarn android.support.v8.renderscript.*
-keepclassmembers class android.support.v8.renderscript.RenderScript {
  native *** rsn*(...);
  native *** n*(...);
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

