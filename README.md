# ICMA OM Study Portal

An installable, offline-first study portal for ICMA Pakistan Managerial Level-1 M3 Organizational Management.

## Web / PWA
The root files provide the lightweight study portal and can be deployed as a static website through GitHub Pages or another HTTPS host.

## Android APK
The Android app is generated from the same portal using Capacitor 8. GitHub Actions installs Node.js 22, JDK 21, Android command-line tools, Android platform API 36, and Build Tools 36.0.0, then generates the native Android project and builds a debug APK.

### Build
1. Push changes to `main`, or open **Actions → Build OM Portal Android APK → Run workflow**.
2. Open the completed workflow run.
3. Download the `OM-Portal-Android-APK` artifact.
4. Extract the ZIP and install `app-debug.apk` on Android.

The workflow also attempts to attach the APK to the GitHub release tagged `latest-apk`.

## Important
- The debug APK is suitable for direct testing/sideloading on Android.
- It is not a Play Store release; production distribution requires a private signing key and a signed release build.
- API keys must never be placed in browser JavaScript or committed to the repository.
- Study checklist progress remains local to the device/browser storage.

## Study scope
- Operations Management prioritized (35%).
- HRM (20%), Marketing Management (25%), and Performance Management (20%).
- Topic checklist and quick MCQ practice.
