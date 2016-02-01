# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ortal\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-dontskipnonpubliclibraryclasses

-keepclassmembers class * extends android.support.v4.app.Fragment {
   public void *(android.view.View);
}

-keepclassmembers class * extends android.support.v4.app.Fragment {
   public void *(android.view.View);
}


# NewRelic
-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses

# Google Play Services
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
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

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Keep line numbers for stack trace
-keepattributes SourceFile,LineNumberTable

-keep class * implements java.io.Serializable { *; }

-keepnames class * implements java.io.Serializable

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

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Retrofit
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn okio.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

#Mixpanel
-dontwarn com.mixpanel.**

-keep class **.R$* {
    <fields>;
}

# ---- REQUIRED card.io CONFIG ----------------------------------------
# card.io is a native lib, so anything crossing JNI must not be changed

# Don't obfuscate DetectionInfo or public fields, since
# it is used by native methods
-keep class io.card.payment.DetectionInfo
-keepclassmembers class io.card.payment.DetectionInfo {
    public *;
}

-keep class io.card.payment.CreditCard
-keep class io.card.payment.CreditCard$1
-keepclassmembers class io.card.payment.CreditCard {
	*;
}

-keepclassmembers class io.card.payment.CardScanner {
	*** onEdgeUpdate(...);
}

# Don't mess with classes with native methods

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class io.card.payment.* {
    public protected *;
}


# -------- PayPal SDK ----------
# (does not include card.io)

-dontwarn com.google.android.gms.**
-dontwarn android.support.v4.*

## GSON 2.2.4 specific rules ##

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# ETBAPI
-keep class com.easytobook.api.** { *; }

# CORE INTERFACE
-keep class com.etb.app.core.model.** { *; }


# If your application, applet, servlet, library, etc., contains enumeration
# classes, you'll have to preserve some special methods. Enumerations were
# introduced in Java 5. The java compiler translates enumerations into classes
# with a special structure. Notably, the classes contain implementations of some
# static methods that the run-time environment accesses by introspection (Isn't
# that just grand? Introspection is the self-modifying code of a new
# generation). You have to specify these explicitly, to make sure they aren't
# removed or obfuscated:

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# More complex applications, applets, servlets, libraries, etc., may contain
# classes that are serialized. Depending on the way in which they are used, they
# may require special attention

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ---- REQUIRED card.io CONFIG ----------------------------------------
# card.io is a native lib, so anything crossing JNI must not be changed

# Don't obfuscate DetectionInfo or public fields, since
# it is used by native methods
-keep class io.card.payment.DetectionInfo
-keepclassmembers class io.card.payment.DetectionInfo {
    public *;
}

-keep class io.card.payment.CreditCard
-keep class io.card.payment.CreditCard$1
-keepclassmembers class io.card.payment.CreditCard {
	*;
}

-keepclassmembers class io.card.payment.CardScanner {
	*** onEdgeUpdate(...);
}

# Don't mess with classes with native methods

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class io.card.payment.* {
    public protected *;
}


# -------- PayPal SDK ----------
# (does not include card.io)

-dontwarn com.google.android.gms.**
-dontwarn android.support.v4.**

# License Dialog
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-keep class com.etb.app.widget.ScrollAwareFABBehavior { *; }
-keep class com.etb.app.widget.ScrollAwareViewBehavior { *; }

# Otto
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}