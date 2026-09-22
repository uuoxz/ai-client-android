# ИТЕРАЦИЯ 1 — апгрейд стека

## Что изменено
Только версии сборки. UI, экраны, логика (AiRepository, SecurePrefs, Room-схема) не тронуты.

| Компонент             | Было              | Стало             |
|------------------------|-------------------|-------------------|
| AGP                    | 8.2.0             | 8.7.3             |
| Kotlin                 | 2.0.0             | 2.0.21            |
| KSP                    | 2.0.0-1.0.22      | 2.0.21-1.0.28     |
| Gradle wrapper         | 8.4               | 8.9               |
| Compose BOM            | 2024.01.00        | 2025.06.01        |
| material3 (явно)       | —                 | 1.4.0-alpha14     |
| compileSdk / targetSdk | 34 / 34           | 35 / 35           |
| core-ktx               | 1.12.0            | 1.15.0            |
| activity-compose       | 1.8.2             | 1.9.3             |
| lifecycle-*            | 2.7.0             | 2.8.7             |
| navigation-compose     | 2.7.6             | 2.8.4             |
| Java / jvmTarget       | 17                | 17 (без изменений — workflow на JDK 17) |

## Убрано
`com.halilibo.compose-richtext` (richtext-ui, richtext-ui-material3, richtext-commonmark) —
зависимость была подключена, но нигде в коде не использовалась
(`MessageItem.kt` рендерит обычный `Text`), и `richtext-ui-material3:0.17.0`
не резолвится из Maven Central при сборке. Удаление безопасно: изменений
кода не потребовалось.

## Файлы в этом архиве
- `build.gradle.kts`
- `app/build.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`

## После распаковки (Termux)
```bash
cd ~/ai-client-android
unzip -o ~/Download/iter1.zip -d ~/ai-client-android
git add -A
git commit -m "iter1: upgrade AGP 8.7.3 / Kotlin 2.0.21 / KSP / Compose BOM 2025.06.01 / SDK 35, drop unused richtext"
git push
```

## Проверить в GitHub Actions
- Workflow должен собраться зелёным на JDK 17.
- Внешне в приложении ничего не меняется — это чисто проверка сборки на новом стеке.
- Если упадёт на резолве material3 1.4.0-alpha14 (alpha-версия может быть
  снята с публикации) — сообщите лог ошибки, откачу на последнюю стабильную
  (material3 в составе BOM без явной версии).
