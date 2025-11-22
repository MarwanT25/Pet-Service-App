# حل مشكلة Gradle Cache التالف

## الخطوات المطلوبة:

### 1. في Android Studio:
- File → Invalidate Caches / Restart
- اختر "Invalidate and Restart"

### 2. من Terminal (في مجلد المشروع):
```bash
# تنظيف build
./gradlew clean

# حذف Gradle cache يدوياً (اختياري)
# Windows:
rmdir /s /q "%USERPROFILE%\.gradle\caches\transforms-3"
rmdir /s /q "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\androidx.constraintlayout"
rmdir /s /q "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\androidx.profileinstaller"

# أو حذف كل cache:
rmdir /s /q "%USERPROFILE%\.gradle\caches"
```

### 3. إعادة البناء:
```bash
./gradlew build
```

## ما تم إصلاحه:
✅ تم تحديث constraintlayout من 2.2.1 إلى 2.1.4
✅ تم إضافة multidex support
✅ تم إصلاح AndroidManifest.xml

## ملاحظة:
إذا استمرت المشكلة، جرب:
1. تحديث Android Gradle Plugin
2. تحديث Gradle wrapper
3. حذف مجلد `.gradle` في المشروع

