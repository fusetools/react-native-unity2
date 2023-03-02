#!/bin/bash
set -e

pushd "builds/android" > /dev/null
gradle wrapper --gradle-version 7.2
./gradlew build
popd > /dev/null

rm -rf "android" && mkdir -p "android/libs"
cp "builds/android/unityLibrary/build/outputs/aar/unityLibrary-release.aar" "android"
cp "builds/android/unityLibrary/build/intermediates/aar_libs_directory/release/libs/unity-classes.jar" "android/libs"

cat << EOF >> "android/build.gradle"
configurations.maybeCreate("default")
artifacts.add("default", file("unityLibrary-release.aar"))
EOF

tar -czvf android.tgz android
du -h android.tgz
