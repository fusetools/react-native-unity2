# RNUnity app

An example embedding Unity 3D content inside a React Native app.

Prebuilt artifacts of the Unity content is included in the repo so it is possible to work on the app without having Unity installed on the developer machine. Unity is required only if you want to build new artifacts.

## Running the app

**Install dependencies**

```shell
npm install
```

**Build & run (Android or iOS)**

```shell
npm run android
npm run ios
```

## Unity content

The included Unity content is located in the [`unity/`](unity/) directory.

Required files are automatically extracted from tarballs while running `npm install`.
