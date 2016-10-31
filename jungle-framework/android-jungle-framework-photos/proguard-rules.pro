# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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


-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


# QQ Share.
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}


# WX Share.
-keep class com.tencent.mm.**{*;}
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}


# GDT.
-keep class com.qq.e.**{
    public protected *;
}

-keep class com.tencent.gdt.**{
    public protected *;
}


# Domob.
-keep class cn.domob.android.ads.**{*;}


-keep class com.google.protobuf.**{*;}
-keep class * extends com.google.protobuf.GeneratedMessageLite{*;}

# Support V4.
-keep class android.support.v4.**{*;}


-keepattributes *Annotation*

-keepclassmembers class * {
    @com.halfstraw.kernel.protocol.HandleCmd public *;
    @com.halfstraw.kernel.protocol.HandleCmdError public *;
    @com.halfstraw.kernel.protocol.HandleCmd private *;
    @com.halfstraw.kernel.protocol.HandleCmdError private *;
    @com.halfstraw.kernel.protocol.HandleCmd protected *;
    @com.halfstraw.kernel.protocol.HandleCmdError protected *;
    @com.halfstraw.kernel.protocol.HandleCmd *;
    @com.halfstraw.kernel.protocol.HandleCmdError *;
}

# Android System Depend.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.webkit.WebView


-keepclasseswithmembernames class * {
    native <methods>;
    private native <methods>;
    public native <methods>;
}


-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


-keepattributes Signature


-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}


-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}


-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}


-keepattributes *Annotation*


-keepclassmembers enum * {
*;
}


-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}


-keepclassmembers class **.R$* {
    public static <fields>;
}


-keep class * extends com.jungle.simpleorm.BaseEntity {*;}

-keepclassmembers class * extends com.jungle.simpleorm.BaseEntity {
*;
}

