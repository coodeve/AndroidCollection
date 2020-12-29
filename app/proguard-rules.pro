# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# -- 依赖混淆处理 --
 #Gradle Retrolambda混淆规则
 -dontwarn java.lang.invoke.*
 -dontwarn **$$Lambda$*
# ARouter
 -keep public class com.alibaba.android.arouter.routes.**{*;}
 -keep public class com.alibaba.android.arouter.facade.**{*;}
 -keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
 # 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
 -keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
 # 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
 # -keep class * implements com.alibaba.android.arouter.facade.template.IProvider



# -- 通用混淆规则 --

# 指定压缩级别
-optimizationpasses 5
# 不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers
# 混淆时所采用的算法(谷歌推荐算法)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
# 把混淆类中的方法名也混淆了
-useuniqueclassmembernames
# 优化是允许访问并修改有修饰符的类和类成员
-allowaccessmodification
# 将文件来源命名为"SourceFile"的字符串
-renamesourcefileattribute SourceFile
# 保留行号
-keepattributes SourceFile,LineNumberTable
# 日志和记录
# 混淆是是否记录日志
-dontpreverify
-verbose
# apk包内所有class的内部结构
-dump class_files.txt
# 为混淆的类成员
-printseeds seeds.txt
# 列出从apk中删除的代码
-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt
# Android基本组件
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
# 保持 Google 原生服务需要的类不被混淆
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
# Support包规则
-dontwarn android.support.**
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * { ####
     native <methods>;
}
# 保留注解不混淆
-keepattributes *Annotation*
 # 保留自定义控件(继承自View)不被混淆
-keep public class * extends android.view.View { ####
 *** get*();
 void set*(***);
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保留指定格式的构造方法不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class **.R$* { *; }
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
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
# WebView处理，项目中没有使用到webView忽略即可
# 保持Android与JavaScript进行交互的类不被混淆
-keepattributes *JavascriptInterface*
-keep class **.AndroidJavaScript { *; }
-keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void *(android.webkit.WebView,java.lang.String);
}

# 删除代码中Log相关的代码
#-assumenosideeffects class android.util.Log {
#    public static boolean isLoggable(java.lang.String, int);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}