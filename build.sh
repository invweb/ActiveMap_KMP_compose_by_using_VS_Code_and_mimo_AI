#!/bin/bash

# Active Map - Build and Run Script

echo "=== Active Map - Сборка и запуск ==="
echo ""

case "$1" in
    "android")
        echo "Сборка Android приложения..."
        ./gradlew :androidApp:installDebug
        echo "Приложение установлено на устройство"
        ;;
    "desktop")
        echo "Запуск Desktop приложения..."
        ./gradlew :desktopApp:run
        ;;
    "web")
        echo "Запуск Web приложения..."
        ./gradlew :webApp:jsBrowserDevelopmentRun
        ;;
    "test")
        echo "Запуск тестов..."
        ./gradlew :shared:allTests
        ;;
    "clean")
        echo "Очистка проекта..."
        ./gradlew clean
        ;;
    *)
        echo "Использование: $0 {android|desktop|web|test|clean}"
        echo ""
        echo "Команды:"
        echo "  android  - Сборка и установка Android приложения"
        echo "  desktop  - Запуск Desktop приложения"
        echo "  web      - Запуск Web приложения"
        echo "  test     - Запуск тестов"
        echo "  clean    - Очистка проекта"
        ;;
esac
