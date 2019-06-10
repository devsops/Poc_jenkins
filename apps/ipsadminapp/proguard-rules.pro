#************Warnings******************
-dontwarn java.beans.**
-dontwarn java.awt.**
-dontwarn org.gradle.**
-dontwarn javax.security.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.**
-dontwarn org.slf4j.**
-dontwarn groovy.lang.**
-dontwarn com.caverock.**
-dontwarn com.squareup.**
-dontwarn com.android.org.**
-dontwarn org.apache.harmony.**
-dontwarn rx.**
-dontwarn retrofit2.*
-dontwarn com.google.**
-dontwarn java.io.*
-dontwarn okhttp3.**
-dontwarn AppletFrame
-dontwarn svm_toy
-dontwarn com.license4j.**

-dontwarn com.bosch.pai.bearing.**
-dontwarn com.bosch.pai.retail.analytics.**
-dontwarn proguard.gradle.*

-keep public class com.bosch.pai.bearing.** {*;}
-keep public interface com.bosch.pai.bearing.** {*;}
-keep public enum com.bosch.pai.bearing.** {*;}

-keep public class com.bosch.pai.retail.** {*;}
-keep public interface com.bosch.pai.retail.** {*;}
-keep public enum com.bosch.pai.retail.** {*;}

-keep public class com.bosch.pai.comms.** {*;}
-keep public interface com.bosch.pai.comms.** {*;}
-keep public enum com.bosch.pai.comms.** {*;}