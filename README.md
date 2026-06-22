# Breathing Timer Widget

A simple and elegant Android widget that guides you through breathing exercises with the classic 4-7-8 technique:
- **4 seconds**: Inhale (Green)
- **7 seconds**: Hold (Orange)  
- **8 seconds**: Exhale (Blue)

## Features
- ✨ Beautiful widget interface
- 🔄 Continuous breathing cycle
- 📱 Runs in background
- ⚡ Always visible on home screen
- 🎨 Color-coded phases

## Installation

### Build from source:
```bash
./gradlew assembleRelease
```

The APK will be generated at:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

### Using GitHub Actions:
1. Push to the repository
2. Go to Actions tab
3. Download the APK from the build artifacts

## Usage
1. Install the APK on your Android device
2. Long-press on home screen and select "Widgets"
3. Find "Breathing Timer Widget" and add it
4. The widget will automatically start the breathing cycle

## Technical Details
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Language**: Kotlin
- **Architecture**: MVVM with background service

## How it works
- The `BreathingTimerService` runs a background timer
- The `BreathingTimerWidget` displays the current phase and countdown
- State is persisted in SharedPreferences
- Widget updates every second during the breathing cycle
