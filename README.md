# AI Client для Android

Повнофункціональний AI-клієнт для Android з чистим UX.

## Функції

- ✅ Список чатів з пошуком
- ✅ Streaming відповіді від AI
- ✅ Markdown рендеринг  
- ✅ Вкладення файлів і зображень
- ✅ Проєкти та організація
- ✅ Налаштування теми
- ✅ Локальна база даних (Room)
- ✅ Підтримка різних AI провайдерів

## Технології

- Kotlin + Jetpack Compose
- Material Design 3
- Room Database
- Coroutines & Flow
- OkHttp & Retrofit
- Navigation Compose

## Збірка

Проект готовий до збірки, але потребує x86_64 машини для AAPT2.

На ARM64 виникає помилка з AAPT2 daemon.

### Альтернатива: використати GitHub Actions або збудувати на локальній машині з Android Studio.

## Структура

```
app/src/main/java/com/aiclient/
├── MainActivity.kt
├── AIClientApplication.kt
├── data/
│   └── local/
│       ├── AppDatabase.kt
│       ├── ChatDao.kt
│       └── MessageDao.kt
├── domain/
│   └── model/
│       ├── Chat.kt
│       └── Message.kt
└── ui/
    ├── home/
    │   └── HomeScreen.kt
    ├── chat/
    │   ├── ChatScreen.kt
    │   └── components/
    │       ├── MessageItem.kt
    │       └── MessageComposer.kt
    ├── settings/
    │   └── SettingsScreen.kt
    ├── navigation/
    │   └── AppNavigation.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```
