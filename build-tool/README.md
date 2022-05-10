# Unity build tool

Use this to create an **AAR library** (Android) and/or a **Universal Framework** (iOS) from your Unity project.

These artifacts can be used on machines without Unity (including compatible versions of dependencies) installed.

Also supporting both iOS Simulator and iOS Device, which plays better with React Native. Unity normally supports building for only one architecture at the time.

## Usage

### Android

Export your Unity project into this directory:

* `./builds/android/`

Run the following command:

```sh
./build-android.sh
```

Done! Find your AAR (and some other files) in `android/`, or as tarball `android.tgz`.

### iOS

Export your Unity project into these directories:

* `./builds/ios/` (Device SDK)
* `./builds/ios-sim/` (Simulator SDK)

Run the following command:

```sh
./build-ios.sh
```

Done! Find your Universal Framework in `ios/`, or as tarball `ios.tgz`.
