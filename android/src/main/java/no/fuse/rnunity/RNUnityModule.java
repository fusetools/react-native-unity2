
package no.fuse.rnunity;

import android.app.Activity;
import android.util.Log;
import android.view.WindowManager;

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

    boolean keepAwake;

    public RNUnityModule(ReactApplicationContext reactContext) {
        super(reactContext);
        instance = this;
        keepAwake = false;
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

    public boolean getKeepAwake() {
        return keepAwake;
    }

    @ReactMethod
    public void setKeepAwake(boolean enabled) {
        keepAwake = enabled;
        final Activity activity = getCurrentActivity();

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (keepAwake) {
                        Log.d("RNUnityModule", "Turning on keep awake");
                        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } else {
                        Log.d("RNUnityModule", "Turning off keep awake");
                        activity.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                }
            });
        }
    }
}
