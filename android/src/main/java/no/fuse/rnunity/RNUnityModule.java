
package no.fuse.rnunity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.util.Log;
import android.os.Build;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.unity3d.player.UnityPlayer;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import java.lang.reflect.InvocationTargetException;

public class RNUnityModule extends ReactContextBaseJavaModule {
    static RNUnityModule instance;
    private static UPlayer unityPlayer;
    public static boolean _isUnityReady;
    public static boolean _isUnityPaused;
    public static boolean _fullScreen;

    // Called by C#
    public static RNUnityModule getInstance() {
        return instance;
    }

    public static UPlayer getPlayer() {
        if (!_isUnityReady) {
            return null;
        }
        return unityPlayer;
    }

    public static boolean isUnityReady() {
        return _isUnityReady;
    }

    public static boolean isUnityPaused() {
        return _isUnityPaused;
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

    @ReactMethod
    public void unloadUnity() {
        final Activity activity = getCurrentActivity();
                if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RNUnityModule", "Unload Unity");
                    unload();
                }
            });
        }
    }

    public static void createPlayer(final Activity activity, final UnityPlayerCallback callback) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (unityPlayer != null) {
            callback.onReady();

            return;
        }

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.getWindow().setFormat(PixelFormat.RGBA_8888);
                    int flag = activity.getWindow().getAttributes().flags;
                    boolean fullScreen = false;
                    if ((flag & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                        fullScreen = true;
                    }

                    try {
                        unityPlayer = new UPlayer(activity, callback);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {}

                    try {
                        // wait a moment. fix unity cannot start when startup.
                        Thread.sleep(1000);
                    } catch (Exception e) {}

                    // start unity
                    try {
                        addUnityViewToBackground();
                    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {}

                    unityPlayer.windowFocusChanged(true);

                    try {
                        unityPlayer.requestFocusPlayer();
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {}

                    unityPlayer.resume();

                    if (!fullScreen) {
                        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }

                    _isUnityReady = true;

                    try {
                        callback.onReady();
                    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {}
                }
            });
        }
    }

    public static void pause() {
        if (unityPlayer != null) {
            unityPlayer.pause();
            _isUnityPaused = true;
        }
    }

    public static void resume() {
        if (unityPlayer != null) {
            unityPlayer.resume();
            _isUnityPaused = false;
        }
    }

    public static void unload() {
        if (unityPlayer != null) {
            unityPlayer.unload();
            _isUnityPaused = false;
        }
    }

    public static void addUnityViewToBackground() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (unityPlayer == null) {
            return;
        }

        if (unityPlayer.getParentPlayer() != null) {
            // NOTE: If we're being detached as part of the transition, make sure
            // to explicitly finish the transition first, as it might still keep
            // the view's parent around despite calling `removeView()` here. This
            // prevents a crash on an `addContentView()` later on.
            // Otherwise, if there's no transition, it's a no-op.
            // See https://stackoverflow.com/a/58247331
            ((ViewGroup) unityPlayer.getParentPlayer()).endViewTransition(unityPlayer.requestFrame());
            ((ViewGroup) unityPlayer.getParentPlayer()).removeView(unityPlayer.requestFrame());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            unityPlayer.setZ(-1f);
        }

        final Activity activity = ((Activity) unityPlayer.getContextPlayer());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(1, 1);
        activity.addContentView(unityPlayer.requestFrame(), layoutParams);
    }

    public static void addUnityViewToGroup(ViewGroup group) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (unityPlayer == null) {
            return;
        }

        if (unityPlayer.getParentPlayer() != null) {
            ((ViewGroup) unityPlayer.getParentPlayer()).removeView(unityPlayer.requestFrame());
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        group.addView(unityPlayer.requestFrame(), 0, layoutParams);
        unityPlayer.windowFocusChanged(true);
        unityPlayer.requestFocusPlayer();
        unityPlayer.resume();
    }

    public interface UnityPlayerCallback {
        void onReady() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

        void onUnload();

        void onQuit();
    }
}
