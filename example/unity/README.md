# Unity builds

This directory contains packaged artifacts exported from Unity:

* `android.tgz`
* `ios.tgz`

These files are stored using Git LFS.

## Unpacking

Unity artifacts must be unpacked before they can be consumed by our React Native app.

This is normally done automatically while running `npm install` (via `unpack.mjs`).

You can use the following commands to unpack the artifacts.

```shell
tar -xf android.tgz
tar -xf ios.tgz
```

## Updating

Please visit [this link](https://github.com/fusetools/react-native-unity2/tree/main/build-tool) for information about updating the artifacts.
