# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Preserve Room database entities and DAOs
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class com.example.data.entity.** { *; }
-keep class com.example.data.dao.** { *; }
-keep class com.example.data.database.** { *; }
-keep class com.example.model.** { *; }
