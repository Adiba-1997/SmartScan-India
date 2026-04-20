# SmartScan India

Android scanner app in Kotlin (`com.azhar.smartscanindia`) targeting Android Studio **4.2.2** / AGP **4.2.2**.

## Implemented features
- CameraX live document scanner activity with capture and multi-page save to PDF.
- Auto-crop enhancement for captured document images.
- OCR using ML Kit Text Recognition with language selection (English/Hindi/Telugu/Urdu/Tamil UI options).
- Working PDF tools via PDFBox Android: merge, split, compress, password-lock, image-to-PDF.
- Signature pad save + apply signature image to PDF.
- App lock PIN persistence and lock screen.
- Aadhaar front/back capture and combined PDF.
- PAN capture and save to PDF.

## Build
1. Open in Android Studio 4.2.2.
2. Ensure Android SDK 31 + JDK 8.
3. Sync Gradle and run `app`.
