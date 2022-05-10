#!/bin/bash
set -e

INA="builds/ios/build/Release-iphoneos/UnityFramework.framework"
INB="builds/ios-sim/build/Release-iphonesimulator/UnityFramework.framework"
OUT="ios/UnityFramework.xcframework"

rm -rf "$OUT" ios.tgz
mkdir -p "$OUT"

xcodebuild -project "builds/ios/Unity-iPhone.xcodeproj" -target "UnityFramework" -configuration "Release" -sdk "iphoneos"
xcodebuild -project "builds/ios-sim/Unity-iPhone.xcodeproj" -target "UnityFramework" -configuration "Release" -sdk "iphonesimulator"
xcodebuild -create-xcframework -framework "$INA" -framework "$INB" -output "$OUT"

tar -czvf ios.tgz ios
du -h ios.tgz
