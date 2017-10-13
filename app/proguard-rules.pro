# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/dpr/android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep, allowobfuscation class oogbox.api.odoo.*
-keepclassmembers, allowobfuscation class * {
    *;
}

-keep class oogbox.api.odoo.client.helper.data.OdooRecord
-keep class oogbox.api.odoo.client.builder.data.OdooRecords
# To keep names as it is.
-keepnames class oogbox.api.odoo.OdooClient
-keepnames class oogbox.api.odoo.OdooClient$Builder
-keepnames class oogbox.api.odoo.client.OdooVersion
-keepnames class oogbox.api.odoo.OdooUser
-keepnames class oogbox.api.odoo.client.builder.DataResponse
-keepnames class oogbox.api.odoo.client.builder.RequestBuilder
-keepnames class oogbox.api.odoo.client.AuthError
-keepnames class oogbox.api.odoo.client.helper.data.OdooRecord
-keepnames class oogbox.api.odoo.client.builder.data.OdooRecords
-keepnames class oogbox.api.odoo.client.helper.data.OdooError
-keepnames class oogbox.api.odoo.client.helper.data.OdooResult
-keepnames class oogbox.api.odoo.client.helper.utils.OArguments
-keepnames class oogbox.api.odoo.client.helper.utils.ODomain
-keepnames class oogbox.api.odoo.client.helper.utils.OdooFields
-keepnames class oogbox.api.odoo.client.helper.utils.OdooParams
-keepnames class oogbox.api.odoo.client.helper.utils.OdooValues
-keepnames class oogbox.api.odoo.client.helper.OdooErrorException
-keepnames class oogbox.api.odoo.client.listeners.AuthenticateListener
-keepnames class oogbox.api.odoo.client.listeners.IOdooRecords
-keepnames class oogbox.api.odoo.client.listeners.IOdooResponse
-keepnames class oogbox.api.odoo.client.listeners.OdooConnectListener
-keepnames class oogbox.api.odoo.client.listeners.OdooErrorListener
-keepnames class oogbox.api.odoo.client.listeners.OdooVersionListener

-keepclassmembernames class oogbox.api.odoo.* {
    public <methods>;
    public <fields>;
}
-keepclassmembernames class oogbox.api.odoo.client.helper.* {
    public <methods>;
    public <fields>;
}
-keepclassmembernames class oogbox.api.odoo.client.helper.data.* {
    public <methods>;
    public <fields>;
}
-keepclassmembernames class oogbox.api.odoo.client.helper.utils.* {
    public <methods>;
    public <fields>;
}

-keepclassmembernames class oogbox.api.odoo.client.builder.* {
    public <methods>;
    public <fields>;
}

-keepclassmembernames class oogbox.api.odoo.client.builder.data.* {
    public <methods>;
    public <fields>;
}

-keepclassmembernames class oogbox.api.odoo.client.listeners.* {
    public <methods>;
    public <fields>;
}
-keepclassmembernames class oogbox.api.odoo.client.* {
    public <methods>;
    public <fields>;
}
-keepclassmembernames class oogbox.api.odoo.OdooClient$Builder {
    public <methods>;
    public <fields>;
}