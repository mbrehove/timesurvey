# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android dual-module project (mobile + wear) for a time usage tracking app called "timesurvey". The app generates random alarms on a smartwatch, prompting users to categorize their current activity to eliminate memory bias in time tracking.

## Architecture

- **Mobile module** (`mobile/`): Companion phone app with traditional Android Views for data visualization and export
- **Wear module** (`wear/`): Wear OS app with Jetpack Compose UI for the main user interaction
- **Shared namespace**: `com.timesurvey` across both modules
- **Database**: Room database (SQLite) for local storage with entities for Categories and TimeUsageRecords
- **Communication**: Mobile and wear apps communicate via Wearable Data Layer API

## Key Components

### Wear Module (Primary Interface)
- `MainActivity.kt` - Main entry point using Jetpack Compose
- `SettingsScreen.kt` - Primary settings interface
- `AlarmScheduler.kt` - Handles random alarm scheduling
- `AlarmReceiver.kt` - Processes alarm notifications
- `CategoryActionReceiver.kt` - Handles category selection responses
- `MainViewModel.kt` - UI state management with ViewModelFactory
- `TimeUsageRepository.kt` - Data layer abstraction
- Database entities: `Category.kt`, `TimeUsageRecord.kt`, `AppDatabase.kt`, `TimeUsageDao.kt`

### Mobile Module (Companion)
- Basic Android activity setup for data visualization and CSV export functionality

## Development Commands

Since Java Runtime is not configured, standard Gradle commands may not work directly. Key commands would typically be:

- Build: `./gradlew build`
- Build wear module: `./gradlew :wear:build`
- Build mobile module: `./gradlew :mobile:build`
- Run tests: `./gradlew test`
- Install debug APK: `./gradlew installDebug`

## Key Dependencies

- **Wear OS**: Jetpack Compose for Wear, Tiles, Complications
- **Database**: Room with KSP (Kotlin Symbol Processing)
- **UI**: Jetpack Compose (wear), traditional Views (mobile)
- **Architecture**: ViewModel, Repository pattern
- **Build**: Gradle with Kotlin DSL, Android SDK 36, min SDK 30

## Important Notes

- Both modules use the same `com.timesurvey` package/namespace
- The project follows a clean architecture pattern with repository and ViewModel layers
- Wear module is the primary user interface; mobile module is secondary for data management
- Random alarm scheduling is a core feature implemented in the wear module