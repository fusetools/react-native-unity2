
package no.fuse.rnunity;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.unity3d.player.UnityPlayer;

public class RNUnityModule extends ReactContextBaseJavaModule {
    static RNUnityModule instance;

    // Called by C#
    public static RNUnityModule getInstance() {
        return instance;
    }

    public RNUnityModule(ReactApplicationContext reactContext) {
        super(reactContext);
        instance = this;
    }

    @Override
    public String getName() {
        return "RNUnity";
    }

    // Called by C#
    public void emitEvent(String name, String data) {
        ReactContext context = getReactApplicationContext();
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
    }

    @ReactMethod
    public void sendMessage(String gameObject, String methodName, String message) {
        UnityPlayer.UnitySendMessage(gameObject, methodName, message);
    }

    @ReactMethod
    public void addListener(String eventName) {
        // Dummy method
    }

    @ReactMethod
    public void removeListeners(Integer count) {
        // Dummy method
    }
}
