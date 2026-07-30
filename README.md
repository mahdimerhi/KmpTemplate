# KmpTemplate

A multi-module Kotlin Multiplatform (KMP) project template with Compose Multiplatform, designed to
be a starting point for new KMP projects targeting Android, iOS, and Desktop (JVM).

## Architecture

- **Clean Architecture** with multi-module setup managed by **Gradle Convention Plugins**
- `base/` - Core utilities and base classes
- `feature/` - Feature modules (api/data/presentation layers)
- `data/` - Core module aggregating feature data modules
- `shared/` - Dependency injection wiring with Metro
- `androidApp/`, `iosApp/`, `desktopApp/` - App entry points

## Tech Stack

- **Kotlin Multiplatform** - Android, iOS, Desktop
- **Compose Multiplatform** - Shared UI
- **Metro** - Dependency Injection
- **Ktor** - HTTP client
- **Room (KMP)** - Local database
- **DataStore** - Local preferences
- **Kotlinx Serialization** - JSON
- **Coil** - Image loading
- **Navigation Compose** - Type-safe navigation

## Modules

| Module | Description |
|--------|-------------|
| `:base:api` | Shared interfaces (Settings, Qualifiers, Pagination) |
| `:base:data` | Data layer abstractions (Room, DataStore, Ktor) |
| `:base:presentation` | UI utilities and theming |
| `:feature:home` | Home feature with list/detail screens |
| `:feature:settings` | Settings screen with theme picker |
| `:data` | Database and network configuration |
| `:shared` | DI graph and app wiring |
| `:androidApp` | Android entry point |
| `:desktopApp` | Desktop entry point |

## Getting Started

1. Rename project: update `settings.gradle.kts`, package names, and iOS config
2. Set up signing for Android and iOS
3. Add your features following the existing module pattern

## License

Apache License 2.0
