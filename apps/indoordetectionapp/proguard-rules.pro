-keep class javax.** { *; }
-keep class java.** { *; }
-keep class org.** { *; }
-dontwarn com.bosch.pai.**
-dontwarn retrofit2.*
-dontwarn org.slf4j.**
-dontwarn okio.*
-dontwarn com.opencsv.bean.**
-dontwarn javax.security.**
-dontwarn groovy.lang.**
-dontwarn org.**
-dontwarn java.**
-dontwarn okhttp3.**
-dontwarn AppletFrame
-dontwarn svm_toy
-dontwarn com.license4j.**


-keepclassmembers enum * { *; }
-keepattributes *Annotation*,Signature

-keep class libsvm.svm_node {*;}
-keep class libsvm.svm_model {*;}
-keep class libsvm.svm_parameter {*;}