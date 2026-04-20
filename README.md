# SmartScan India (Premium UI Build)

Kotlin Android app package: `com.azhar.smartscanindia`.

## What's improved
- Premium-style home experience with gradient background, card grid, and smooth card animations.
- New splash screen activity and polished launcher foreground icon.
- Improved OCR pipeline with pre-processing (upscale + grayscale + contrast boost) before ML Kit recognition.
- Real PDF compression by rasterizing pages at reduced resolution/quality and rebuilding compressed PDF.
- AdMob integration upgraded with banner lifecycle handling and interstitial display cadence.
- Build/profile optimizations for release (`shrinkResources`, minification, packaging exclusions, language/resource trimming hints).

## Build target
- Android Studio 4.2.2
- AGP 4.2.2
- compileSdk 31 / targetSdk 31
- JDK 8 recommended
