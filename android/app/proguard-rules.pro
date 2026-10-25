# Methods called from the demo page's JavaScript (window.WTMSDemo) must keep their names
-keepclassmembers class com.woodyouloveit.wtms.demo.DemoBridge {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
