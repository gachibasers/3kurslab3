# Лабораторна робота №3 — Морський бій (Android, Kotlin)

## Опис
Android-застосунок гри "Морський бій" (гравець проти комп'ютера) на Kotlin. Реалізовано логіку гри, дошки 10×10, розстановку кораблів, постріли, хід комп'ютера, логування та підтримку кількох мов (i18n).

## Особливості
- **Мова:** Kotlin.
- **Логування:** `GameLogger` — centralized `android.util.Log` (Debug/Info/Warn/Error).
- **i18n:** ресурси строк у `values/strings.xml` (EN) та `values-uk/strings.xml` (UK).
- **Гра:** гравець тапить по клітинці дошки суперника, комп'ютер стріляє випадково з затримкою.

## Структура проєкту
```
lab3/
├── app/src/main/AndroidManifest.xml
├── app/src/main/res/layout/activity_main.xml
├── app/src/main/res/values/strings.xml           # EN
├── app/src/main/res/values-uk/strings.xml        # UK
├── app/src/main/java/com/lab3/battleship/
│   ├── GameLogger.kt                             # логування
│   ├── Ship.kt                                   # модель корабля
│   ├── GameBoard.kt                              # дошка, постріли, авто-розташування
│   └── MainActivity.kt                           # екран гри
├── app/build.gradle
├── build.gradle
├── settings.gradle
└── .gitignore
```

## Як зібрати
Проєкт підключується до Android Studio. Залежності:
- `androidx.core:core-ktx:1.10.1`
- `androidx.appcompat:appcompat:1.6.1`
- `com.google.android.material:material:1.9.0`
- `androidx.constraintlayout:constraintlayout:2.1.4`

compileSdk 33, minSdk 21.

## Репозиторій
https://github.com/gachibasers/3kurslab3
